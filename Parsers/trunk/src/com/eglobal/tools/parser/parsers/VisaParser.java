/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import com.eglobal.tools.parser.pojo.AParsedObject;
import com.eglobal.tools.parser.pojo.FieldExt;
import com.eglobal.tools.parser.pojo.FieldFormat;
import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.parser.pojo.util.Converter;
import com.google.gson.Gson;

/**
 * @author egldt1029
 */
public class VisaParser extends FieldLoader implements IParser {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VisaParser.class);
	private static VisaParser __INSTANCE;

	private VisaParser() {
		super.init("/resources/parsers/fieldVisa.json");
	}

	public static VisaParser getInstance() {
		if (__INSTANCE == null) {
			__INSTANCE = new VisaParser();
		}
		return __INSTANCE;
	}

	@Override
	public Message parse(ByteBuffer buffer, Object... objects) {
		Message message = new Message();
		FieldFormat fieldFormat = _fieldFormats.get(0); // header
		String bitmap = null;
		String mti = null;
		if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			Object value = parseField(buffer, fieldFormat);
			if (value instanceof FieldExt) {
				mti = (String) ((FieldExt) value).getField("MTI");
				log.debug("MTI: " + mti);
				message.setMti(mti);
				bitmap = (String) ((FieldExt) value).getField("Bitmap");
			}
			message.setField(fieldFormat.getName(), value);
		}
		List<Integer> fieldsPresent = getFieldsPresent(bitmap, 0);
		if (fieldsPresent.get(0) == 1) {
			// secondary bitmap
			fieldFormat = _fieldFormats.get(1);
			Object value = parseField(buffer, fieldFormat);
			List<Integer> _fieldsPresent = getFieldsPresent(value.toString(), 64);
			for (Integer integer : _fieldsPresent) {
				fieldsPresent.add(integer);
			}
			fieldsPresent.remove(0); // removing secondary bitmap
		}
		for (Integer fieldIndex : fieldsPresent) {
			fieldFormat = _fieldFormats.get(fieldIndex);
			if (fieldFormat == null) {
				log.error("Field " + fieldIndex + " is not recognized");
				break;
			}
			if ("0100".equals(mti) && fieldIndex == 52) {
				//PIN Block y POS
				fieldFormat.setLength(16);
			}
			Object value = parseField(buffer, fieldFormat);
			message.setField("" + fieldFormat.getIndex(), value);
		}
		message.setType("Visa");
		return message;
	}

	private List<Integer> getFieldsPresent(String bitmap, int offset) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bitmap.length(); i += 2) {
			String hex = bitmap.substring(i, i + 2);
			int value = Integer.parseInt(hex, 16);
			String binary = Integer.toBinaryString(value);
			binary = String.format("%8s", binary).replace(' ', '0');
			sb.append(binary);
		}
		if (log.isDebugEnabled()) {
			log.debug("Hex bitmap: " + bitmap + " -> Binary String: " + sb.toString());
		}
		List<Integer> fieldList = new ArrayList<>();
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '1') {
				fieldList.add(i + 1 + offset);
			}
		}
		if (log.isDebugEnabled()) {
			Gson gson = new Gson();
			log.debug("fields present: " + gson.toJson(fieldList));
		}
		return fieldList;
	}

	private Object parseField(ByteBuffer buffer, FieldFormat fieldFormat) {
		int numBytes = 0;
		log.debug("Parsing field: " + fieldFormat.getIndex());
		boolean bcdFlag = false;
		if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			int length = fieldFormat.getLength();
			if (length > 2 && !fieldFormat.getName().contains("Original")) {
				if ("BCD".equalsIgnoreCase(fieldFormat.getFormat())) {
					if (length % 2 != 0) {
						length = length + 1;
						bcdFlag = true;
					}
				}
			}
			numBytes = "BCD".equalsIgnoreCase(fieldFormat.getFormat()) ? length / 2 : length;
		} else {
			Pattern pattern = Pattern.compile("L+var");
			Matcher matcher = pattern.matcher(fieldFormat.getLengthIndicator());
			if (matcher.find()) {
				int counter = 0;
				for (int i = 0; i < fieldFormat.getLengthIndicator().length(); i++, counter++) {
					if (fieldFormat.getLengthIndicator().charAt(i) != 'L') {
						break;
					}
				}
				log.debug(counter + " position to length");
				counter = counter % 2 != 0 ? counter + 1 : counter;
				byte[] lengthBytes = new byte[counter / 2];
				buffer.get(lengthBytes);
				log.debug("length bytes" + new String(Hex.encodeHex(lengthBytes)).toUpperCase());
				numBytes = Integer.parseInt(new String(Hex.encodeHex(lengthBytes)).toUpperCase(), 16);
				log.debug("field length: " + numBytes);
				if (numBytes > 2) {
					if ("BCD".equalsIgnoreCase(fieldFormat.getFormat())) {
						if (numBytes % 2 != 0) {
							numBytes = numBytes + 1;
							bcdFlag = true;
						}
					}
				}
				numBytes = "BCD".equalsIgnoreCase(fieldFormat.getFormat()) ? numBytes / 2 : numBytes;
				if (numBytes == 0 && fieldFormat.getIndex() == 35) {
					numBytes = 19;
				}
			}
		}
		byte[] bytes = new byte[numBytes];
		buffer.get(bytes);
		Object value = convertParseField(fieldFormat.getFormat(), bytes);
		log.debug("value: " + value);
		if (fieldFormat.getSubfields() != null) {
			// has subfields
			FieldExt fieldExt = new FieldExt();
//			fieldExt.setRawData(value.toString());
			ByteBuffer b = ByteBuffer.wrap(bytes);
			if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
				for (FieldFormat subFieldFormat : fieldFormat.getSubfields()) {
					Object subfieldValue = parseField(b, subFieldFormat);
					fieldExt.setField(subFieldFormat.getName(), subfieldValue);
				}
			} else if ("Lvar".equalsIgnoreCase(fieldFormat.getLengthIndicator()) && "BCD".equalsIgnoreCase(fieldFormat.getFormat())) {
				int pos = 0;
				int i = 0;
				while (pos < value.toString().length()) {
					String _value = value.toString().substring(pos, pos += fieldFormat.getSubfields().get(i).getLength());
					fieldExt.setField(fieldFormat.getSubfields().get(i++).getName(), _value);
				}
			}
			if (fieldFormat.getSubfields().size() > 2 && "tag55".equalsIgnoreCase(fieldFormat.getSubfields().get(2).getLengthIndicator())) {
				for (int i = 0; i < 2; i++) {
					FieldFormat subFieldFormat = fieldFormat.getSubfields().get(i);
					Object subfieldValue = parseField(b, subFieldFormat);
					fieldExt.setField(subFieldFormat.getName(), subfieldValue);
				}
				while (b.hasRemaining()) {
					byte[] _tagName = new byte[1];
					_tagName[0] = b.get();
					String tagName = Hex.encodeHexString(_tagName).toUpperCase();
					if (!validSubfield(tagName, fieldFormat.getSubfields())) {
						byte[] _tagName2 = new byte[1];
						_tagName2[0] = b.get();
						tagName += Hex.encodeHexString(_tagName2).toUpperCase();
					}
					if (!validSubfield(tagName, fieldFormat.getSubfields())) {
						log.error("TagName " + tagName + " is not recognized \n" + Hex.encodeHexString(b.array()).toUpperCase());
						fieldExt.setRawData(Hex.encodeHexString(b.array()).toUpperCase());
						break; // TODO
					}
					byte[] _tagLength = new byte[1];
					b.get(_tagLength);
					Integer tagLength = Integer.parseInt(new String(Hex.encodeHex(_tagLength)), 16);
					byte[] _tagContent = new byte[tagLength];
					b.get(_tagContent);
					String tagContent = Hex.encodeHexString(_tagContent).toUpperCase();
					fieldExt.setField(tagName, tagContent);
					log.debug(tagName + ": " + tagContent);
				}
			}
			value = fieldExt;
		} else {
			if (bcdFlag) {
				value = value.toString().substring(1);
			}
		}
		return value;
	}

	private boolean validSubfield(String subfieldName, List<FieldFormat> list) {
		boolean valid = false;
		for (FieldFormat parserFieldFormats : list) {
			if (subfieldName.equalsIgnoreCase(parserFieldFormats.getName())) {
				valid = true;
				break;
			}
		}
		return valid;
	}

	private String convertParseField(String format, byte[] rawData) {
		String value = null;
		switch (format) {
		case "BCD": 
			value = Hex.encodeHexString(rawData);
			break;
		case "HEXA": 
			value = Hex.encodeHexString(rawData);
			break;
		case "ASCII": 
			value = new String(rawData);
			break;
		case "EBCDIC": 
			value = convertParseField("ASCII", Converter.ebcdicToAscii(rawData));
		}
		return value;
	}

	@Override
	public byte[] format(AParsedObject object) {
		Set<Integer> recognizedFields = _fieldFormats.keySet();
		Message message = (Message) object;
		ByteBuffer buffer = ByteBuffer.allocate(2000);
		List<Integer> fieldsPresent = new ArrayList<>();
		for (Integer recognizedField : recognizedFields) {
			if (message.getField("" + recognizedField) != null) {
				fieldsPresent.add(recognizedField);
			}
		}
		Gson gson = new Gson();
		log.debug("Fields present" + gson.toJson(fieldsPresent));
		String bitmap = toBitmap(fieldsPresent);
		log.debug("bitmap: " + bitmap);
		FieldExt header = ((FieldExt) message.getField("Header"));
		header.setField("Bitmap", bitmap);
		FieldFormat format = _fieldFormats.get(fieldFormats.get("Header"));
		byte[] formattedField = formatField(header, format);
		log.debug("Formated Field: " + new String(formattedField));
		buffer.put(formattedField);
		for (Integer _field : fieldsPresent) {
			Object field = message.getField("" + _field);
			format = _fieldFormats.get(_field);
			if (field != null) {
				formattedField = formatField(field, format);
				if (formattedField == null) {
					log.error("Can not format field " + _field);
					break;
				}
				buffer.put(formattedField);
			}
		}
		byte[] bytes = new byte[buffer.position()];
		buffer.clear();
		buffer.get(bytes);
		return bytes;
	}

	private byte[] formatField(Object field, FieldFormat format) {
		log.debug("formatting " + format.getIndex());
		ByteBuffer buffer = ByteBuffer.allocate(500);
		buffer.mark();
		if (field instanceof FieldExt) {
			FieldExt _field = (FieldExt) field;
			for (FieldFormat subField : format.getSubfields()) {
				if (_field.getField(subField.getName()) != null) {
					buffer.put(formatField(_field.getField(subField.getName()), subField));
				}
			}
		} else {
			if ("ASCII".equalsIgnoreCase(format.getFormat())) {
				buffer.put(field.toString().getBytes());
				log.debug("put " + field.toString());
			} else if ("EBCDIC".equalsIgnoreCase(format.getFormat())) {
				buffer.put(Converter.asciiToEbcdic(field.toString().getBytes()));
			} else if ("BCD".equalsIgnoreCase(format.getFormat()) || "HEXA".equalsIgnoreCase(format.getFormat())) {
				buffer.put(Converter.hexStringBytetoByteArray(field.toString()));
			}
		}
		String lengthIndicator = null;
		if (!"fixed".equalsIgnoreCase(format.getLengthIndicator())) {
			Pattern pattern = Pattern.compile("L+var");
			Matcher matcher = pattern.matcher(format.getLengthIndicator());
			if (matcher.matches()) {
				int counter = 0;
				for (int i = 0; i < format.getLengthIndicator().length(); i++, counter++) {
					if (format.getLengthIndicator().charAt(i) != 'L') {
						break;
					}
				}
				log.debug("buffer position: " + buffer.position());
				if (format.getIndex() == 55 || format.getIndex() == 59 || format.getIndex() == 60 || format.getIndex() == 62 || format.getIndex() == 63 || format.getIndex() == 44 || format.getIndex() == 104 || format.getIndex() == 121 || format.getIndex() == 126) {
					lengthIndicator = String.format("%" + counter + "s", Integer.toHexString(buffer.position()));
				} else {
					lengthIndicator = String.format("%" + counter + "s", Integer.toHexString(buffer.position() * 2));
				}
				lengthIndicator = lengthIndicator.replace(' ', '0');
			} else if ("tag55".equalsIgnoreCase(format.getLengthIndicator())) {
				StringBuilder prefix = new StringBuilder();
				prefix.append(format.getName());
				prefix.append(String.format("%02d", field.toString().length() / 2));
				lengthIndicator = prefix.toString();
			}
		}
		byte[] bytes = new byte[buffer.position()];
		buffer.clear();
		buffer.get(bytes);
		if (lengthIndicator != null) {
			ByteBuffer bf = null;
			if ("tag55".equalsIgnoreCase(format.getLengthIndicator())) {
				bf = ByteBuffer.allocate(lengthIndicator.getBytes().length / 2 + bytes.length);
				bf.put(Converter.hexStringBytetoByteArray(lengthIndicator));
			} else {
				if (lengthIndicator.length() % 2 != 0) {
					lengthIndicator = '0' + lengthIndicator;
				}
				byte[] b = Converter.hexStringBytetoByteArray(lengthIndicator);
				bf = ByteBuffer.allocate(b.length + bytes.length);
				bf.put(Converter.hexStringBytetoByteArray(lengthIndicator));
			}
			bf.put(bytes);
			bytes = bf.array();
		}
		return bytes;
	}
}
