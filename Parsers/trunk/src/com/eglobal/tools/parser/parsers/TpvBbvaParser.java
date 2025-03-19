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
public class TpvBbvaParser extends FieldLoader implements IParser {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TpvBbvaParser.class);
	private static TpvBbvaParser __INSTANCE;

	private TpvBbvaParser() {
		super.init("resources/parsers/fieldTpvBbva.json");
	}

	public static TpvBbvaParser getInstance() {
		if (__INSTANCE == null) {
			__INSTANCE = new TpvBbvaParser();
		}
		return __INSTANCE;
	}

	@Override
	public Message parse(ByteBuffer buffer, Object... objects) {
		Message message = new Message();
		FieldFormat fieldFormat = _fieldFormats.get(0); // header
		String bitmap = null;
		if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			Object value = parseField(buffer, fieldFormat, null);
			if (value instanceof FieldExt) {
				String mti = (String) ((FieldExt) value).getField("MTI");
				log.debug("MTI: " + mti);
				message.setMti(mti);
				bitmap = (String) ((FieldExt) value).getField("Bitmap");
			}
			message.setField(fieldFormat.getName(), value);
		}
		List<Integer> fieldsPresent = getFieldsPresent(bitmap);
		for (Integer fieldIndex : fieldsPresent) {
			fieldFormat = _fieldFormats.get(fieldIndex);
			if (fieldFormat == null) {
				log.error("Field " + fieldIndex + " is not recognized {}", message);
				break;
			}
			Object value = parseField(buffer, fieldFormat, message.getMti());
			message.setField("" + fieldFormat.getIndex(), value);
		}
		message.setType("TPV_BBVA");
		return message;
	}

	private List<Integer> getFieldsPresent(String bitmap) {
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
				fieldList.add(i + 1);
			}
		}
		if (log.isDebugEnabled()) {
			Gson gson = new Gson();
			log.debug("fields present: " + gson.toJson(fieldList));
		}
		return fieldList;
	}

	private Object parseField(ByteBuffer buffer, FieldFormat fieldFormat, String mti) {
		int numBytes = 0;
		log.debug("Parsing field: " + fieldFormat.getIndex());
		if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			int length = fieldFormat.getLength();
			length = length % 2 != 0 ? length + 1 : length;
			numBytes = "BCD".equalsIgnoreCase(fieldFormat.getFormat()) ? length / 2 : length;
		} else {
			Pattern pattern = Pattern.compile("L+var");
			Matcher matcher = pattern.matcher(fieldFormat.getLengthIndicator());
			if (matcher.find()) {
				int i;
				for (i = 0; i < fieldFormat.getLengthIndicator().length(); i++) {
					if (fieldFormat.getLengthIndicator().charAt(i) != 'L') {
						break;
					}
				}
				log.debug(i + " position to length");
				i = i % 2 != 0 ? i + 1 : i;
				byte[] lengthBytes = new byte[i / 2];
				buffer.get(lengthBytes);
				numBytes = Integer.parseInt(Converter.byteArrayAsHexString(lengthBytes));
				if (fieldFormat.getFormat().equalsIgnoreCase("bcd")) {
					numBytes /= 2;
				}
				log.debug("field length: " + numBytes);
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
				for (FieldFormat subFieldFormats : fieldFormat.getSubfields()) {
					Object subfieldValue = parseField(b, subFieldFormats, mti);
					fieldExt.setField(subFieldFormats.getName(), subfieldValue);
				}
			}
			if ("TagName-Lvar".equalsIgnoreCase(fieldFormat.getSubfields().get(0).getLengthIndicator())) {
				while (b.hasRemaining()) {
					byte _tagName = b.get();
					String tagName = Converter.byteAsHexString(_tagName);
					byte[] _tagLength = new byte[1];
					b.get(_tagLength);
					Integer tagLength = Integer.parseInt(new String(Hex.encodeHex(_tagLength)), 16);
					byte[] _tagContent = new byte[tagLength];
					b.get(_tagContent);
					String tagContent = new String(_tagContent);
					fieldExt.setField(tagName, tagContent);
					log.debug(tagName + ": " + tagContent);
					if (!validSubfield(tagName, fieldFormat.getSubfields())) {
						log.warn("TagName " + tagName + " is not recognized (" + Converter.byteArrayAsHexString(b.array()) + ")");
						fieldExt.setRawData(Converter.byteArrayAsHexString(b.array()));
					}
				}
			}
			if ("tag55".equalsIgnoreCase(fieldFormat.getSubfields().get(0).getLengthIndicator())) {
				while (b.hasRemaining()) {
					byte _tagName = b.get();
					String tagName = Converter.byteAsHexString(_tagName);
					if (!validSubfield(tagName, fieldFormat.getSubfields())) {
						byte _tagName2 = b.get();
						tagName += Converter.byteAsHexString(_tagName2);
					}
					if (!validSubfield(tagName, fieldFormat.getSubfields())) {
						log.warn("TagName " + tagName + " is not recognized (" + Converter.byteArrayAsHexString(b.array()) + ")");
						fieldExt.setRawData(Converter.byteArrayAsHexString(b.array()));
						break; // TODO
					}
					byte[] _tagLength = new byte[1];
					b.get(_tagLength);
					Integer tagLength = Integer.parseInt(new String(Hex.encodeHex(_tagLength)), 16);
					byte[] _tagContent = new byte[tagLength];
					b.get(_tagContent);
					String tagContent = Converter.byteArrayAsHexString(_tagContent);
					fieldExt.setField(tagName, tagContent);
					log.debug(tagName + ": " + tagContent);
				}
			}
			if ("Token-BCD".equalsIgnoreCase(fieldFormat.getSubfields().get(0).getLengthIndicator())) {
				if ("0500".equals(mti) || "0510".equals(mti)) {
					fieldExt.setRawData(Converter.byteArrayAsHexString(bytes));
				} else {
					while (b.hasRemaining()) {
						byte[] _tokenName = new byte[2];
						b.get(_tokenName);
						String tokenName = Converter.asciiValue(_tokenName);
						if (!validSubfield(tokenName, fieldFormat.getSubfields())) {
							log.warn("TokenName " + tokenName + " is not recognized, buffer is not consumed (" + Converter.byteArrayAsHexString(bytes) + ")");
							fieldExt.setRawData(Converter.byteArrayAsHexString(bytes));
							break;
						}
						/*Modificacion para token RA y RT*/
						byte[] _length = null;
						if ("RA".equals(tokenName) || "RT".equals(tokenName)) {
							_length = new byte[3];
						} else {
							_length = new byte[2];
						}
						b.get(_length);
						Integer length = Integer.parseInt(Converter.asciiValue(_length));
						byte[] _content = new byte[length];
						b.get(_content);
						String content = null;
						if ("HEXA".equalsIgnoreCase(getSubfieldFormat(fieldFormat.getSubfields(), tokenName))) {
							content = Converter.byteArrayAsHexString(_content);
						} else {
							content = Converter.asciiValue(_content);
						}
						fieldExt.setField(tokenName, content);
						log.debug(tokenName + ": " + content);
					}
				}
			}
			value = fieldExt;
		}
		return value;
	}

	private String getSubfieldFormat(List<FieldFormat> formats, String name) {
		for (FieldFormat fieldFormat : formats) {
			if (fieldFormat.getName().equalsIgnoreCase(name)) {
				return fieldFormat.getFormat();
			}
		}
		return null;
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
		case "ASCII": 
			value = new String(rawData);
			break;
		}
		return value;
	}

	@Override
	public byte[] format(AParsedObject object) {
		Set<Integer> recognizedFields = _fieldFormats.keySet();
		Message message = (Message) object;
		ByteBuffer buffer = ByteBuffer.allocate(1200);
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
		log.debug("formatting " + (format.getIndex() != null ? format.getIndex() : format.getName()));
		ByteBuffer buffer = ByteBuffer.allocate(700);
		buffer.mark();
		if (field instanceof FieldExt) {
			FieldExt _field = (FieldExt) field;
			for (FieldFormat subField : format.getSubfields()) {
				if (_field.getField(subField.getName()) != null) {
					buffer.put(formatField(_field.getField(subField.getName()), subField));
				}
			}
		} else {
			if ("BCD".equalsIgnoreCase(format.getFormat()) || "HEXA".equalsIgnoreCase(format.getFormat())) {
				String value = field.toString();
				if (field.toString().length() % 2 != 0) {
					value += '0';
				}
				buffer.put(Converter.hexStringBytetoByteArray(value));
				log.debug("put " + Converter.hexStringBytetoByteArray(value));
			} else if ("ASCII".equalsIgnoreCase(format.getFormat())) {
				buffer.put(field.toString().getBytes());
				log.debug("put " + field.toString());
			} else if ("EBCDIC".equalsIgnoreCase(format.getFormat())) {
				buffer.put(Converter.asciiToEbcdic(field.toString().getBytes()));
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
				String fill0Format = "%0" + counter + "d";
				lengthIndicator = String.format(fill0Format, buffer.position());
			} else if ("tag55".equalsIgnoreCase(format.getLengthIndicator())) {
				StringBuilder prefix = new StringBuilder();
				prefix.append(format.getName());
				prefix.append(String.format("%02d", field.toString().length() / 2));
				lengthIndicator = prefix.toString();
			} else if ("TagName-Lvar".equalsIgnoreCase(format.getLengthIndicator())) {
				StringBuilder prefix = new StringBuilder();
				prefix.append(format.getName());
				String hex = Integer.toHexString(field.toString().length());
				prefix.append(hex.length() < 2 ? '0' + hex : hex);
				lengthIndicator = prefix.toString();
			} else if ("Token-BCD".equalsIgnoreCase(format.getLengthIndicator())) {
				StringBuilder prefix = new StringBuilder();
				prefix.append(format.getName());
				if ("HEXA".equalsIgnoreCase(format.getFormat())) {
					prefix.append(String.format("%02d", field.toString().length() / 2));
				} else {
					prefix.append(String.format("%02d", field.toString().length()));
				}
				lengthIndicator = Converter.asciiAsHexByteString(prefix.toString());
			}
		}
		byte[] bytes = new byte[buffer.position()];
		buffer.clear();
		buffer.get(bytes);
		if (lengthIndicator != null) {
			if (lengthIndicator.length() % 2 != 0) {
				lengthIndicator = '0' + lengthIndicator;
			}
			byte[] _lengthIndicator = Converter.hexStringBytetoByteArray(lengthIndicator);
			ByteBuffer bf = ByteBuffer.allocate(_lengthIndicator.length + bytes.length);
			bf.put(_lengthIndicator);
			bf.put(bytes);
			bytes = bf.array();
		}
		return bytes;
	}
}
