/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.files;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import com.eglobal.tools.analytics.category.IMessageCategorizer;
import com.eglobal.tools.analytics.category.MessageCategorizerFactory;
import com.eglobal.tools.analytics.category.fingerprint.FingerPrintMessage;
import com.eglobal.tools.analytics.matchers.MessageMatcher;
import com.eglobal.tools.parser.parsers.IParser;
import com.eglobal.tools.parser.parsers.MastercardParser;
import com.eglobal.tools.parser.parsers.ParserFactory;
import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.parser.pojo.RawcomLine;
import com.eglobal.tools.parser.pojo.util.Converter;
import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.db.model.EAcquirerView;
import com.eglobal.tools.validation.db.repository.AcquirerViewRepository;
import com.eglobal.tools.validation.pojo.AcquirerView;
import com.google.gson.Gson;

public class AcqRawcomConsumer implements Runnable {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AcqRawcomConsumer.class);
	private SynchronousQueue<RawcomLine> queue;
	private long registers;
	private long parsedRegisters;
	private long requirements;
	private long responses;
	private PropertyChangeSupport pts = new PropertyChangeSupport(this);
	private Map<String, Long> uniqueCases;
	private AcquirerViewRepository acqViewRepository;
	private final int BATCH_SIZE = 5000;
	private final List<String> DISCARD_TXN = Arrays.asList("SIGN", "ECHO", "INTERCAMBIO");
	private final String env;

	public AcqRawcomConsumer(String env, SynchronousQueue<RawcomLine> queue) {
		this.env = env;
		this.queue = queue;
		uniqueCases = new HashMap<>();
		acqViewRepository = new AcquirerViewRepository();
	}

	@Override
	public void run() {
		execute();
	}

	public Void execute() {
		Gson gson = new Gson();
		MessageMatcher matcher = new MessageMatcher(new String[] {"11", "37"});
		pts.firePropertyChange("text", "", "[Consumer] Usando campos para match " + gson.toJson(matcher.getFieldsToMatch()));
		List<Message> messages = new ArrayList<>();
		List<EAcquirerView> list = new ArrayList<>();
		try (Connection conn = DBManager.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			pts.firePropertyChange("text", "", "[Consumer] Obteniendo conexion a la Base de Datos");
			Thread.sleep(10);
			while (true) {
				RawcomLine line = queue.poll();
				if (line == null) {
					Thread.sleep(1);
					continue;
				}
				if ("<END>".equals(line.getRawData())) {
					break;
				}
				if (line.getRawData() != null && line.getRawData().startsWith("<END FILE")) {
					pts.firePropertyChange("text", "", "[Consumer] Terminado (" + line.getRawData() + ")");
					continue;
				}
				try {
					IParser parser = getParser(line);
					if (parser == null) {
						continue;
					}
					Message message = parseLine(parser, line);
					message.setTimestamp(line.getTimestamp().substring(0, 2) + line.getTimestamp().substring(3, 5) + line.getTimestamp().substring(6, 8));
					parsedRegisters++;
					if (isRequirement(line, message)) {
						requirements++;
						messages.add(message);
					} else if (isResponse(line, message)) {
						responses++;
						processResponse(messages, matcher, message, line, list, conn);
					}
					if (list.size() >= BATCH_SIZE) {
						commitBatch(list, conn);
					}
				} catch (SQLException | DecoderException e) {
					log.error("Error procesando línea: {}", line.getRawData(), e);
				} catch (Throwable t) {
					log.error("Excepcion no controlada en linea: {}", line.getTimestamp(), t);
				}
				registers++;
			}
			if (!list.isEmpty()) {
				commitBatch(list, conn);
			}
		} catch (InterruptedException e) {
			log.error("El hilo fue interrumpido", e);
		} catch (SQLException e) {
			log.error("Error en Base de Datos", e);
		} catch (Throwable t) {
			log.error("Excepcion no controlada", t);
		}
		logCompletion(messages);
		StringBuilder sb = new StringBuilder();
		sb.append("Unique Cases: ");
		sb.append(uniqueCases.keySet().size());
		sb.append("\n");
		for (String _case : uniqueCases.keySet()) {
			sb.append("\t");
			sb.append(" ").append(uniqueCases.get(_case));
			sb.append(":");
			sb.append("\t");
			sb.append(_case);
			sb.append("\n");
		}
		log.debug(sb.toString());
		return null;
	}

	private IParser getParser(RawcomLine line) {
		String iap = line.getIap() != null ? line.getIap().toUpperCase() : "";
		String rawData = new String(line.getRawByteData());
		if (rawData.startsWith("ISO")) {
			return "POS".equals(env) ? ParserFactory.getParser(ParserFactory.Type.ISO) : ParserFactory.getParser(ParserFactory.Type.ISO_ATM);
		} else if (iap.contains("MC") || iap.contains("MASTER") || iap.contains("MDS")) {
			return ParserFactory.getParser(ParserFactory.Type.MASTERCARD);
		} else if (iap.contains("PLUS") || iap.contains("VISA")) {
			return ParserFactory.getParser(ParserFactory.Type.VISA);
		} else 
//		} else if (iap.contains("AMEX") || iap.contains("AMEX_OPTBLUE")) {
//			return ParserFactory.getParser(ParserFactory.Type.AMEX_TPVS);
		if (iap.contains("DISCV")) {
			return ParserFactory.getParser(ParserFactory.Type.DISCOVER);
		} else if (iap.contains("TPV")) {
			return ParserFactory.getParser(ParserFactory.Type.TPV_BBVA);
		} else if (iap.contains("JCB")) {
			return ParserFactory.getParser(ParserFactory.Type.JCB);
		}
		log.error("No suitable parser for line: {}", line);
		return null;
	}

	private Message parseLine(IParser parser, RawcomLine line) throws DecoderException {
		String cad = new String(line.getRawByteData(), StandardCharsets.ISO_8859_1);
		if (parser instanceof MastercardParser && "POS".equals(env) && cad.length() % 2 != 0) {
			String panebcdic = Converter.asciiStringToEbcdicString(line.getPan());
			cad = cad.replaceFirst(panebcdic + ".", panebcdic);
		}
		if (!cad.startsWith("ISO")) {
			cad = cad.replaceAll("\\*", "0").replaceAll("x", "0");
			line.setRawByteData(Hex.decodeHex(cad));
		}
		if (cad.startsWith("ISO")) {
			return (Message) parser.parse(cad.getBytes());
		}
		return (Message) parser.parse(Hex.decodeHex(cad));
	}

	private boolean isRequirement(RawcomLine line, Message message) {
		return message.getMti().charAt(2) % 2 == 0 && line.getReadWrite() == 'R';
	}

	private boolean isResponse(RawcomLine line, Message message) {
		return message.getMti().charAt(2) % 2 != 0 && line.getReadWrite() == 'W';
	}

	private void processResponse(List<Message> messages, MessageMatcher matcher, Message response, RawcomLine line, List<EAcquirerView> list, Connection conn) {
		Set<Message> processedMessages = new HashSet<>();
		for (Message m : messages) {
			if (matcher.match(m, response) && !processedMessages.contains(m)) {
				AcquirerView acqView = new AcquirerView();
				acqView.setRequirementMessage(m);
				acqView.setResponseMessage(response);
				acqView.setBin(line.getPan().length() >= 8 ? line.getPan().substring(0, 8) : line.getPan());
				acqView.setDispatcher(String.valueOf(line.getDispatcher()));
				acqView.setTimestamp(m.getTimestamp());
				//IMessageCategorizer categorizer = MessageCategorizerFactory.getCategorizer(response.getType());
				IMessageCategorizer categorizer = MessageCategorizerFactory.getCategorizer(m.getType());
				//assert categorizer != null;
				String category = categorizer.categorize(m);
				acqView.setCategory(category);
				String fingerprint = FingerPrintMessage.calculateFingerPrint(m);
				acqView.setFingerprint(fingerprint);
				acqView.setKey(line.getPan() + "-" + m.getField("37") + "-" + m.getField("11") + "-" + m.getTimestamp());
				processedMessages.add(m);
				EAcquirerView t = AcquirerView.toEntity(acqView);
				if (t.getCategory() != null && !isTxnDiscard(t.getCategory())) {
					log.debug("Key: " + t.getKey());
					list.add(t);
				}
				if (list.size() >= BATCH_SIZE) {
					try {
						acqViewRepository.insert(list, conn);
						conn.commit();
						list.clear();
						log.debug(BATCH_SIZE + " registros insertados correctamente.");
					} catch (SQLException e) {
						log.error("Error al realizar commit del lote", e);
					}
				}
				uniqueCases.merge(line.getDispatcher() + "-" + category, 1L, Long::sum);
			}
		}
	}

	private void commitBatch(List<EAcquirerView> list, Connection conn) throws SQLException {
		acqViewRepository.insert(list, conn);
		conn.commit();
		list.clear();
		log.info("Commit successful for batch.");
	}

	private void logCompletion(List<Message> messages) {
		log.debug("Done {} processed - Parsed: {} - Requirements: {} - Responses: {}", registers, parsedRegisters, requirements, responses);
		if (!messages.isEmpty()) {
			log.warn("{} messages without match!", messages.size());
		}
	}

	public void addListener(PropertyChangeListener listener) {
		pts.addPropertyChangeListener(listener);
	}

	private boolean isTxnDiscard(String category) {
		for (String discard : DISCARD_TXN) {
			if (category.toUpperCase().contains(discard)) {
				return true;
			}
		}
		return false;
	}
}
