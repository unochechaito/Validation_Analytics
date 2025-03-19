/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

import com.eglobal.tools.parser.pojo.AParsedObject;
import com.eglobal.tools.parser.pojo.FieldExt;
import com.eglobal.tools.parser.pojo.Message;
import org.jpos.iso.ISOComponent;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class StratusParser implements IParser {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StratusParser.class);
	static StratusParser instance;
	private final Message message = new Message();
	private final List<String> mtiReq = Arrays.asList("1200", "1220", "1320", "1321", "1421");
	private final List<String> redReq = Collections.singletonList("1800");
	private final List<String> mtiRes = Arrays.asList("1210", "1230", "1330", "1430");
	private final List<String> redRes = Collections.singletonList("1810");

	public static StratusParser getInstance() {
		if (instance == null) instance = new StratusParser();
		return instance;
	}

	@Override
	public AParsedObject parse(ByteBuffer buffer, Object... objects) {
		byte[] req = buffer.array();
		String mti = new String(req, 0, 4);
		String format = "mti: {}";
		if (mtiReq.contains(mti)) {
			log.info(format, mti);
			return this.request(buffer);
		}
		if (mtiRes.contains(mti)) {
			log.info(format, mti);
			return this.response(buffer);
		}
		if (redReq.contains(mti)) {
			log.info(format, mti);
			return this.requestRed(buffer);
		}
		if (redRes.contains(mti)) {
			log.info(format, mti);
			return this.responseRed(buffer);
		}
		log.warn("El mti no coincide con ninguno en la lista, mti: {}", mti);
		return message;
	}

	@Override
	public byte[] format(AParsedObject object) {
		return new byte[0];
	}

	private Map<Integer, Object> extractComponent(ISOComponent root) throws ISOException {
		Map<Integer, Object> component = new HashMap<>();
		if (root instanceof ISOField) {
			component.put((Integer) root.getKey(), root.getValue());
		}
		if (root.getValue() instanceof ISOMsg) {
			ISOMsg subfield = (ISOMsg) root.getValue();
			Map<Integer, Object> mappedFields = new HashMap<>();
			for (int i = 1; i <= subfield.getMaxField(); i++) {
				if (subfield.hasField(i)) {
					Map<Integer, Object> map = extractComponent(subfield.getComponent(i));
					mappedFields.putAll(map);
				}
			}
			FieldExt fieldExt = new FieldExt();
			mappedFields.forEach((integer, o) -> fieldExt.setField(String.valueOf(integer), o));
			component.put((Integer) root.getKey(), fieldExt);
		}
		return component;
	}

	private AParsedObject parseWithJPos(ByteBuffer req, String relativePathXml) {
		Logger logger = new Logger();
		logger.addListener(new SimpleLogListener());
		try (InputStream file = StratusParser.class.getResourceAsStream(relativePathXml)) {
			GenericPackager packager = new GenericPackager(file);
			packager.setLogger(logger, "packager");
			ISOMsg msg = new ISOMsg();
			msg.setPackager(packager);
			msg.unpack(req.array());
			Map<Integer, Object> fields = new HashMap<>();
			for (int i = 0; i <= msg.getMaxField(); i++) {
				if (msg.hasField(i)) {
					if (i == 1) message.setMti(msg.getString(i));
					ISOComponent component = msg.getComponent(i);
					Map<Integer, Object> map = extractComponent(component);
					fields.putAll(map);
				}
			}
			fields.forEach((index, value) -> message.setField(String.valueOf(index), value));
		} catch (IOException | ISOException exception) {
			log.error("Error al parsear la cadena iso: {}", new String(req.array()), exception);
		}
		return message;
	}

	private AParsedObject request(ByteBuffer req) {
		return this.parseWithJPos(req, "/resources/parsers/StratusRequest.xml");
	}

	private AParsedObject response(ByteBuffer req) {
		return this.parseWithJPos(req, "/resources/parsers/StratusResponse.xml");
	}

	private AParsedObject requestRed(ByteBuffer req) {
		return this.parseWithJPos(req, "/resources/parsers/StratusReqRed.xml");
	}

	private AParsedObject responseRed(ByteBuffer req) {
		return this.parseWithJPos(req, "/resources/parsers/StratusResRed.xml");
	}

	@java.lang.SuppressWarnings("all")
	private StratusParser() {
	}
}
