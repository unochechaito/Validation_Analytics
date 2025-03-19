/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

import com.eglobal.tools.parser.pojo.*;
import com.eglobal.tools.parser.report.dto.MessageIssuesReportImpl;
import com.eglobal.tools.parser.report.observer.EventManager;
import com.eglobal.tools.parser.report.observer.EventType;
import com.eglobal.tools.parser.utils.EMVDecoder;
import org.apache.commons.codec.binary.Hex;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class DiscoverIsoParser extends FieldLoader implements IParser {
	@java.lang.SuppressWarnings("all")
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(DiscoverIsoParser.class);
	static final int HEADER = 0;
	static DiscoverIsoParser instance;
	static final Message message = new Message();
	static final Charset charset = Charset.forName("windows-1252");
	private boolean secondBitmapOn;
	static EventManager eventManager;

	private DiscoverIsoParser() {
		init("resources/parsers/fieldDiscoverIso.json");
	}

	public static DiscoverIsoParser getInstance() {
		if (instance == null) instance = new DiscoverIsoParser();
		return instance;
	}

	public static DiscoverIsoParser getInstance(EventManager evtMng) {
		if (instance == null) {
			eventManager = evtMng;
			instance = new DiscoverIsoParser();
		}
		return instance;
	}

	@Override
	public AParsedObject parse(ByteBuffer buffer, Object... objects) {
		buffer = this.changeCharset(buffer);
		processingHeader(buffer);
		com.eglobal.tools.parser.pojo.FieldExt header = (FieldExt) message.getField("0");
		java.lang.String bitmap = (String) header.getSubfields().get("1");
		java.util.List<com.eglobal.tools.parser.pojo.Field<java.lang.Object>> fieldsPrimaryBitmap = this.processingFieldsInBitmap(ByteBuffer.wrap(bitmap.getBytes()), buffer);
		fieldsPrimaryBitmap.forEach(field -> message.setField(String.valueOf(field.getIndex()), field.getValue()));
		if (secondBitmapOn) {
			java.util.List<com.eglobal.tools.parser.pojo.Field<java.lang.Object>> fieldsSecondBitmap = this.processingSecondaryBitmap(fieldsPrimaryBitmap.get(0).getRaw(), buffer);
			fieldsSecondBitmap.forEach(field -> message.setField(String.valueOf(field.getIndex()), field.getValue()));
		}
		return message;
	}

	/**
	 * Cambia el charset seteado por defecto a windows-1252.
	 * @param buffer arreglo de bytes en la clase ByteBuffer.
	 * @return un buffer con el charset nuevo.
	 */
	private ByteBuffer changeCharset(ByteBuffer buffer) {
		java.lang.String data = new String(buffer.array(), Charset.defaultCharset());
		byte[] dataWithCharset = data.getBytes(charset);
		return ByteBuffer.wrap(dataWithCharset);
	}

	@Override
	public byte[] format(AParsedObject msg) {
		throw new UnsupportedOperationException("format function fail");
	}

	private void processingHeader(ByteBuffer msg) {
		com.eglobal.tools.parser.pojo.FieldFormat headerFormat = this._fieldFormats.get(HEADER);
		java.util.Optional<com.eglobal.tools.parser.pojo.Field<java.lang.Object>> fieldOpt = processingField(headerFormat, msg);
		if (fieldOpt.isPresent()) {
			com.eglobal.tools.parser.pojo.Field<java.lang.Object> field = fieldOpt.get();
			com.eglobal.tools.parser.pojo.FieldExt header = (FieldExt) field.getValue();
			java.lang.Object mti = header.getSubfields().get("0");
			message.setMti((String) mti);
			message.setField(String.valueOf(field.getIndex()), field.getValue());
		}
	}

	private List<Field<Object>> processingFieldsInBitmap(ByteBuffer bitmap, ByteBuffer msg) {
		byte[] bitmapTemp = new byte[16];
		bitmap.get(bitmapTemp);
		java.lang.String hexBitmapStr = new String(bitmapTemp);
		byte[] binaryBitmap = this.hexBitmapToBinaryBitmap(hexBitmapStr);
		boolean existSecondBitmap = this.firstBitOnBitmapOn(binaryBitmap);
		if (existSecondBitmap) {
			this.secondBitmapOn = true;
		}
		int[] fieldsArray = this.fieldsInBitmap(binaryBitmap);
		java.util.List<java.lang.Integer> listBitmapFields = Arrays.stream(fieldsArray).boxed().collect(Collectors.toList());
		return listBitmapFields.stream().map(numField -> processingField(this._fieldFormats.get(numField), msg)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	private List<Field<Object>> processingSecondaryBitmap(ByteBuffer bitmap, ByteBuffer msg) {
		byte[] bitmapTemp = new byte[16];
		bitmap.get(bitmapTemp);
		java.lang.String hexBitmapStr = new String(bitmapTemp);
		byte[] binaryBitmap = this.hexBitmapToBinaryBitmap(hexBitmapStr);
		int[] fieldsArray = this.fieldsInBitmap(binaryBitmap);
		java.util.List<java.lang.Integer> listBitmapFields = Arrays.stream(fieldsArray).boxed().collect(Collectors.toList());
		return listBitmapFields.stream().map(numField -> processingField(this._fieldFormats.get(numField + 64), msg)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	private Optional<Field<Object>> processingField(FieldFormat fieldFormat, ByteBuffer msg) {
		log.debug("Processing field: {}", fieldFormat.getName());
		log.debug("message buffer: {}", msg.toString());
		com.eglobal.tools.parser.pojo.Field<java.lang.Object> field = new Field<>();
		ByteBuffer tempData = null;
		field.setIndex(fieldFormat.getIndex());
		field.setName(fieldFormat.getName());
		if (fieldFormat.getLengthIndicator().equals("fixed")) {
			tempData = ByteBuffer.wrap(fixedField(fieldFormat.getLength(), msg));
		}
		if (isLengthVariable(fieldFormat.getLengthIndicator())) {
			java.util.Optional<byte[]> lengthLVarOpt = lengthVariableField(fieldFormat, msg);
			if (lengthLVarOpt.isPresent()) {
				tempData = ByteBuffer.wrap(lengthLVarOpt.get());
			}
		}
		java.util.Optional<java.nio.ByteBuffer> tempDataOpt = Optional.ofNullable(tempData);
		if (tempDataOpt.isPresent()) {
			field.setRaw(tempDataOpt.get());
			if (!fieldFormat.getSubfields().isEmpty()) {
				com.eglobal.tools.parser.pojo.FieldExt listField = processingSubField(fieldFormat, tempDataOpt.get());
				field.setValue(listField);
			} else {
				field.setValue(fieldType(fieldFormat, tempDataOpt.get()));
			}
			log.debug("Value of field: {}", field.toString());
			return Optional.of(field);
		}
		return Optional.empty();
	}

	private FieldExt processingSubField(FieldFormat fieldFormat, ByteBuffer msg) {
		com.eglobal.tools.parser.pojo.FieldExt fieldExt = new FieldExt();
		java.util.List<com.eglobal.tools.parser.pojo.FieldFormat> subfield = fieldFormat.getSubfields();
		subfield.forEach(subField -> {
			log.debug("Processing subfield: {}", subField.getName());
			java.util.Optional<com.eglobal.tools.parser.pojo.Field<java.lang.Object>> fieldOpt = processingField(subField, msg);
			if (fieldOpt.isPresent()) {
				com.eglobal.tools.parser.pojo.Field<java.lang.Object> field = fieldOpt.get();
				fieldExt.setField(String.valueOf(field.getIndex()), field.getValue());
				log.debug("Subfield value: {}", field.toString());
			}
		});
		return fieldExt;
	}

	private boolean isLengthVariable(String lengthIndicator) {
		return lengthIndicator.matches("^L+Var$");
	}

	private String hexToBinaryStringArray(String hexStr) {
		int decimalValue = Integer.parseInt(hexStr, 16);
		String binaryValue = Integer.toBinaryString(decimalValue);
		return String.format("%8s", binaryValue).replace(' ', '0');
	}

	private byte[] hexBitmapToBinaryBitmap(String hexString) {
		java.lang.String[] arrHexBitmap = hexString.split("(?<=\\G.{2})");
		StringBuilder build = new StringBuilder();
		for (String hex : arrHexBitmap) {
			build.append(hexToBinaryStringArray(hex));
		}
		return build.toString().getBytes();
	}

	private static byte[] fixedField(int lengthIndicator, ByteBuffer msg) {
		log.debug("get lenght: {}", lengthIndicator);
		byte[] length = new byte[lengthIndicator];
		msg.get(length);
		return length;
	}

	private static Optional<byte[]> lengthVariableField(FieldFormat field, ByteBuffer msg) {
		byte[] byteLength = null;
		if (field.getLengthIndicator().equals("LLVar")) {
			byteLength = fixedField(2, msg);
		}
		if (field.getLengthIndicator().equals("LLLVar")) {
			byteLength = fixedField(3, msg);
		}
		java.util.Optional<byte[]> lengthValidation = Optional.ofNullable(byteLength);
		if (lengthValidation.isPresent()) {
			try {
				byte[] value = lengthValidation.get();
				int length = Integer.parseInt(new String(value));
				return Optional.of(fixedField(length, msg));
			} catch (NumberFormatException exception) {
				log.error(exception);
				com.eglobal.tools.parser.report.dto.MessageIssuesReportImpl report = MessageIssuesReportImpl.builder().currentBuffer(msg).fieldFormat(field).build();
				eventManager.notify(EventType.PARSE_FIELD_PROBLEM, report);
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	private Object fieldType(FieldFormat field, ByteBuffer msg) {
		java.lang.String format = field.getFormat();
		if (Objects.equals(format, "ascii")) {
			return new String(msg.array(), charset);
		}
		if (Objects.equals(format, "ber-tlv")) {
			java.lang.String hexData = Hex.encodeHexString(msg, false);
			java.util.List<com.eglobal.tools.parser.pojo.Tlv> tlvs = EMVDecoder.decodeHexString(hexData);
			com.eglobal.tools.parser.pojo.FieldExt fielExt = new FieldExt();
			fielExt.setRawData(hexData);
			for (com.eglobal.tools.parser.pojo.Tlv tlv : tlvs) {
				fielExt.setField(tlv.getTag(), tlv.getValue());
			}
			return fielExt;
		}
		return new String(msg.array(), charset);
	}

	private boolean firstBitOnBitmapOn(byte[] bitmap) {
		return bitmap[0] == 49;
	}

	private int[] fieldsInBitmap(byte[] bitmap) {
		int count = 0;
		java.util.ArrayList<java.lang.Integer> list = new ArrayList<Integer>();
		for (byte bit : bitmap) {
			if (bit == 49) {
				list.add(count + 1);
			}
			count++;
		}
		return list.stream().mapToInt(Integer::intValue).toArray();
	}
}
