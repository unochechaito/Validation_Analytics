/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

import java.nio.ByteBuffer;
import com.eglobal.tools.parser.pojo.AParsedObject;
import com.eglobal.tools.parser.pojo.FieldExt;
import com.eglobal.tools.parser.pojo.FieldFormat;
import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.parser.pojo.util.Converter;

/**
 * @author egldt1029
 */
public class LayoutParser extends FieldLoader implements IParser {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LayoutParser.class);
	public static final String LENGTH_INDICADOR_REMAINING = "remaining";

	protected LayoutParser(String file) {
		init(file);
	}

	public int getRegisterLength() {
		int length = 0;
		for (int i = 0; i < _fieldFormats.size(); i++) {
			length += _fieldFormats.get(i).getLength();
		}
		return length;
	}

	@Override
	public AParsedObject parse(ByteBuffer buffer, Object... objects) {
		Message parsedObject = new Message();
		for (int i = 0; i < _fieldFormats.size(); i++) {
			FieldFormat fieldFormat = _fieldFormats.get(i);
			if (fieldFormat == null) {
				log.error("Field " + i + " is not recognized {}", parsedObject);
				break;
			}
			Object value = parseField(buffer, fieldFormat);
			if (value != null) {
				parsedObject.setField(fieldFormat.getName(), value);
			} else {
				break; //No hay mas campos por parsear
			}
		}
		return parsedObject;
	}

	private Object parseField(ByteBuffer buffer, FieldFormat fieldFormat) {
		int numBytes = 0;
		log.debug("Parsing field: " + fieldFormat.getIndex());
		if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			int length = fieldFormat.getLength();
			numBytes = length;
		}
		if (LENGTH_INDICADOR_REMAINING.equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
			if (log.isDebugEnabled()) log.debug("the number of elements remaining in this buffer: {}", buffer.remaining());
			numBytes = buffer.remaining();
		}
		if (buffer.remaining() < numBytes) {
			log.warn("No hay suficientes bytes en el buffer para el campo: " + fieldFormat.getName() + "Se requieren " + numBytes + " bytes, pero solo restan " + buffer.remaining() + " bytes. {}", new String(buffer.array()));
			return null;
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
			value = fieldExt;
		}
		return value;
	}

	private String convertParseField(String format, byte[] rawData) {
		String value = null;
		switch (format) {
		case "BCD": 
			value = Converter.bcdValue(rawData);
			break;
		case "ASCII": 
			value = new String(rawData);
			break;
		}
		return value;
	}

	@Override
	public byte[] format(AParsedObject object) {
		return null;
	}
}
