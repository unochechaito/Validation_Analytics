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
import java.util.Objects;
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
public class IsoParser extends FieldLoader implements IParser {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IsoParser.class);
	private static IsoParser __INSTANCE;

	private IsoParser() {
		init("resources/parsers/fieldISO.json");
	}

	public static IsoParser getInstance() {
		if (__INSTANCE == null) {
			__INSTANCE = new IsoParser();
		}
		return __INSTANCE;
	}

	@Override
	public AParsedObject parse(ByteBuffer buffer, Object... objects) {
		Message message = new Message();
		FieldFormat fieldFormat = _fieldFormats.get(0); // header
		String bitmap = null;
		if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			Object value = parseField(buffer, fieldFormat);
			if (value instanceof FieldExt) {
				String mti = (String) ((FieldExt) value).getField("MTI");
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
			Object value = parseField(buffer, fieldFormat);
			message.setField("" + fieldFormat.getIndex(), value);
		}
		message.setType("ISO_POS");
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

	private Object parseField(ByteBuffer buffer, FieldFormat fieldFormat) throws NullPointerException, NumberFormatException {
		Objects.requireNonNull(buffer, "buffer de datos en Null");
		Objects.requireNonNull(fieldFormat, "fieldFormat en Null");
		int numBytes = 0;
		log.debug("Parsing field: " + fieldFormat.getIndex());
		if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			int length = fieldFormat.getLength();
			numBytes = length;
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
				byte[] lengthBytes = new byte[counter];
				buffer.get(lengthBytes);
				numBytes = Integer.parseInt(new String(lengthBytes));
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
					Object subfieldValue = parseField(b, subFieldFormats);
					fieldExt.setField(subFieldFormats.getName(), subfieldValue);
				}
			}
			if ("tokenISO".equalsIgnoreCase(fieldFormat.getSubfields().get(1).getLengthIndicator())) {
				int _numBytes = fieldFormat.getSubfields().get(0).getLength();
				byte[] _bytes = new byte[_numBytes];
				b.get(_bytes);
				String content = new String(_bytes);
				fieldExt.setField(fieldFormat.getSubfields().get(0).getName(), content);
				while (b.hasRemaining()) {
					byte _tokenIndicator = b.get();
					if (_tokenIndicator != '!') {
						log.warn("Token indicator (!) expected not found");
					}
					byte _space = b.get();
					if (_space != ' ') {
						log.warn("No token format");
						fieldExt.setRawData(Converter.asciiValue(bytes));
						break;
					}
					byte[] _tokenName = new byte[2];
					b.get(_tokenName);
					String tokenName = Converter.asciiValue(_tokenName);
					if (!validSubfield(tokenName, fieldFormat.getSubfields())) {
						log.warn("Token " + tokenName + " is not recognized");
					}
					byte[] _tokenLength = new byte[5];
					b.get(_tokenLength);
					Integer tokenLength = Integer.parseInt(Converter.asciiValue(_tokenLength));
					_space = b.get();
					if (_space != ' ') {
						log.warn("No token format");
						fieldExt.setRawData(Converter.asciiValue(bytes));
						break;
					}
					if (tokenLength <= b.remaining()) {
						byte[] _tokenContent = new byte[tokenLength];
						b.get(_tokenContent);
						String tokenContent = Converter.asciiValue(_tokenContent);
						log.debug("Token " + tokenName + ": " + tokenContent);
						fieldExt.setField(tokenName, tokenContent);
					} else {
						log.warn("No more bytes, trying to read " + tokenLength + " bytes. Has " + b.remaining() + "remaining");
						fieldExt.setRawData(Converter.asciiValue(bytes));
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
			value = fieldExt;
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
		case "ASCII": 
			value = new String(rawData) ;
			break;
		}
		return value;
	}

	@Override
	public byte[] format(AParsedObject object) {
		Set<Integer> recognizedFields = _fieldFormats.keySet();
		Message message = (Message) object;
		ByteBuffer buffer = ByteBuffer.allocate(1500);
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
		ByteBuffer buffer = ByteBuffer.allocate(1500);
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
				String format0Filler = "%0" + counter + "d";
				lengthIndicator = String.format(format0Filler, buffer.position());
			} else if ("tokenISO".equalsIgnoreCase(format.getLengthIndicator())) {
				StringBuilder prefix = new StringBuilder("! ");
				prefix.append(format.getName());
				prefix.append(String.format("%05d", field.toString().length()));
				prefix.append(" ");
				lengthIndicator = prefix.toString();
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
			ByteBuffer bf = ByteBuffer.allocate(lengthIndicator.getBytes().length + bytes.length);
			if ("tag55".equalsIgnoreCase(format.getLengthIndicator())) {
				bf = ByteBuffer.allocate(lengthIndicator.getBytes().length / 2 + bytes.length);
				bf.put(Converter.hexStringBytetoByteArray(lengthIndicator));
			} else {
				bf.put(lengthIndicator.getBytes());
			}
			bf.put(bytes);
			bytes = bf.array();
		}
		return bytes;
	}
}
