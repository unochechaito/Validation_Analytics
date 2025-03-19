/**
 * Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
 * es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
 * o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
 * identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
 * de correo seginfo@eglobal.com.mx                                                         *
 **/
package com.eglobal.tools.validation.files;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

import com.eglobal.tools.parser.parsers.MastercardParser;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.eglobal.tools.analytics.category.IMessageCategorizer;
import com.eglobal.tools.analytics.category.MessageCategorizerFactory;
import com.eglobal.tools.analytics.category.fingerprint.FingerPrintMessage;
import com.eglobal.tools.analytics.matchers.MessageMatcher;
import com.eglobal.tools.parser.parsers.IParser;
import com.eglobal.tools.parser.parsers.ParserFactory;
import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.parser.pojo.RawcomLine;
import com.eglobal.tools.parser.pojo.util.Converter;
import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.db.model.EIssuerView;
import com.eglobal.tools.validation.db.repository.IssuerViewRepository;
import com.eglobal.tools.validation.pojo.IssuerView;
import com.eglobal.tools.validation.view.BaseLoaderPanel;
import com.google.gson.Gson;

public class IssRawcomConsumer implements Runnable {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IssRawcomConsumer.class);

	private BlockingQueue<RawcomLine> queue;
	private long registers, parsedRegisters, requirements, responses;
	private PropertyChangeSupport pts = new PropertyChangeSupport(this);
	private Map<String, Long> uniqueCases = new HashMap<>();
	private IssuerViewRepository issViewRepository = new IssuerViewRepository();
	private final int BATCH_SIZE = 5000;
	private final List<String> DISCARD_TXN = Arrays.asList("SIGN", "ECHO", "KEY EXCHANGE");
	private final String env;

	public IssRawcomConsumer(String env, BlockingQueue<RawcomLine> queue) {
		this.env = env;
		this.queue = queue;
	}


	@Override
	public void run() {
		execute();
	}


	public Void execute() {
		Gson gson = new Gson();
		MessageMatcher matcher = new MessageMatcher(new String[]{"11", "37"});
		pts.firePropertyChange("text", "", "[Consumer] Usando campos para match " + gson.toJson(matcher.getFieldsToMatch()));

		List<Message> messages = new ArrayList<>();
		List<EIssuerView> list = new ArrayList<>();

		try (Connection conn = DBManager.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			pts.firePropertyChange("text", "", "[Consumer] Obteniendo conexion a la Base de Datos");

			while (true) {
				RawcomLine line = queue.poll();
				if (line == null) {
					Thread.sleep(10);
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
					message.setTimestamp(line.getTimestamp().substring(0, 2)
							+ line.getTimestamp().substring(3, 5)
							+ line.getTimestamp().substring(6, 8)
					);
					parsedRegisters++;

					if (isRequirement(line, message)) {
						requirements++;
						messages.add(message);
					} else if (isResponse(line, message)) {
						responses++;
						processResponse(messages, matcher, message, line, list, conn);
					}
				} catch (DecoderException e) {
					log.error("Error procesando línea: {}", line.getTimestamp(), e);
				} catch (Throwable t) {
					log.error("Excepcion no controlada en línea: {}", line.getTimestamp(), t);
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
		}
		logCompletion(messages);
		pts.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, true, false);
		pts.firePropertyChange(BaseLoaderPanel.STRING_PROGRESS_BAR, "", "Los datos han sido procesados!");
		pts.firePropertyChange(BaseLoaderPanel.ENABLED_START_BUTTON, false, true);
		log.trace("se habilita el boton de Start");
		return null;
	}


	private IParser getParser(RawcomLine line) {
		String iap = line.getIap() != null ? line.getIap().toUpperCase() : "";
		String rawData = new String(line.getRawByteData());

		if (iap.contains("TOTAL") || iap.contains("STANDIN")) {
			log.warn("Skipping line due to TOTAL/STANDIN exclusion: {}", line);
			return null;
		}

		for (Map.Entry<Predicate<String>, ParserFactory.Type> entry : PARSER_RULES.entrySet()) {
			if (entry.getKey().test(iap)) {
				return ParserFactory.getParser(entry.getValue());
			}
		}

		if (rawData.startsWith("ISO")) {
			if ("POS".equals(env)) {
				return ParserFactory.getParser(ParserFactory.Type.ISO);
			} else if ("ATM".equals(env)) {
				return ParserFactory.getParser(ParserFactory.Type.ISO_ATM);
			}
		} else if (rawData.startsWith("1200") || rawData.startsWith("1210")
				|| rawData.startsWith("1400") || rawData.startsWith("1410")) {
			return ParserFactory.getParser(ParserFactory.Type.STRATUS);
		}

		log.error("No suitable parser for line: {}", line);
		return null;
	}


	private Message parseLine(IParser parser, RawcomLine line) throws DecoderException {
		String cad = new String(line.getRawByteData(), StandardCharsets.ISO_8859_1);
		String iap = line.getIap() != null ? line.getIap().toUpperCase() : "";

		if (parser instanceof MastercardParser && "POS".equals(env) && cad.length() % 2 != 0) {
			String panebcdic = Converter.asciiStringToEbcdicString(line.getPan());
			int index = cad.lastIndexOf(panebcdic);
			if (index != -1) {
				cad = cad.substring(0, index + panebcdic.length())
						+ cad.substring(index + panebcdic.length() + 1);
			}
		}

		if (!cad.startsWith("ISO") && !line.getIap().contains("DISCV")) {
			cad = cad.replaceAll("\\*", "0").replaceAll("x", "0");
			line.setRawByteData(Hex.decodeHex(cad));
		}
		if (cad.startsWith("ISO") || line.getIap().contains("DISCV")) {
			return (Message) parser.parse(cad.getBytes());
		}
		return (Message) parser.parse(Hex.decodeHex(cad));
	}


	private static final Map<Predicate<String>, ParserFactory.Type> PARSER_RULES = new LinkedHashMap<>();

	static {
		PARSER_RULES.put(iap -> iap.contains("MC") || iap.contains("MASTER") || iap.contains("MDS"), ParserFactory.Type.MASTERCARD);
		PARSER_RULES.put(iap -> iap.contains("PLUS") || iap.contains("VISA"), ParserFactory.Type.VISA);
        PARSER_RULES.put(iap -> iap.contains("AMEX") || iap.contains("AMEX_OPTBLUE"), ParserFactory.Type.AMEX_TPVS);
		PARSER_RULES.put(iap -> iap.contains("DISCV"), ParserFactory.Type.DISCOVER);
		PARSER_RULES.put(iap -> iap.contains("TPV"), ParserFactory.Type.TPV_BBVA);
		PARSER_RULES.put(iap -> iap.contains("JCB"), ParserFactory.Type.JCB);
		PARSER_RULES.put(iap -> iap.contains("STRATUS"), ParserFactory.Type.STRATUS);
	}


	private boolean isRequirement(RawcomLine line, Message message) {
		return message.getMti().charAt(2) % 2 == 0 && line.getReadWrite() == 'W';
	}


	private boolean isResponse(RawcomLine line, Message message) {
		return message.getMti().charAt(2) % 2 != 0 && line.getReadWrite() == 'R';
	}


	private boolean isTxnDiscard(String category) {
		for (String discard : DISCARD_TXN) {
			if (category != null && category.toUpperCase().contains(discard)) {
				return true;
			}
		}
		return false;
	}


	private void processResponse(List<Message> messages, MessageMatcher matcher, Message response, RawcomLine line, List<EIssuerView> batchList, Connection conn) {
		Iterator<Message> it = messages.iterator();
//        Set<Message> processedMessages = new HashSet<>();
		while (it.hasNext()) {
			Message m = it.next();
			if (matcher.match(m, response)) {
				IssuerView issView = new IssuerView();
				issView.setRequirementMessage(m);
				issView.setResponseMessage(response);

				issView.setBin(line.getPan().length() >= 8 ? line.getPan().substring(0, 8) : line.getPan());
				issView.setDispatcher(String.valueOf(line.getDispatcher()));
				issView.setTimestamp(m.getTimestamp());

				IMessageCategorizer categorizer = MessageCategorizerFactory.getCategorizer(m.getType());
				assert categorizer != null;
				String category = categorizer.categorize(m);
				issView.setCategory(category);
				String fingerprint = FingerPrintMessage.calculateFingerPrint(m);
				issView.setFingerprint(fingerprint);
//                list.add(IssuerView.toEntity(issView));
//                processedMessages.add(m);
				if ("POS".equals(env)) {
					issView.setKey(line.getPan());
				} else if ("ATM".equals(env)) {
					issView.setKey(line.getPan() + "-" + m.getField("37") + "-" + m.getField("11"));
				}

				EIssuerView entity = IssuerView.toEntity(issView);

				if (category != null && !isTxnDiscard(category)) {
					log.debug("Key: {}", entity.getKey());
					batchList.add(entity);
				}

				if (batchList.size() >= BATCH_SIZE) {
					try {
						issViewRepository.insert(batchList, conn);
						conn.commit();
						batchList.clear();
						log.debug(BATCH_SIZE + " registros insertados correctamente.");
					} catch (SQLException e) {
						log.error("Error al realizar commit del lote", e);
					}
				}
				uniqueCases.merge(line.getDispatcher() + "-" + category, 1L, Long::sum);
				it.remove();
				break;
			}
		}
	}


	private void commitBatch(List<EIssuerView> list, Connection conn) throws SQLException {
		issViewRepository.insert(list, conn);
		conn.commit();
		list.clear();
		log.info("Commit successful for batch.");
	}


	private void logCompletion(List<Message> messages) {
		log.debug("Done {} processed - Parsed registers: {} - Requirements: {} - Responses: {}",
				registers, parsedRegisters, requirements, responses);

		String smessage = "[Consumer] Terminado ";
		pts.firePropertyChange("text", "", smessage);

		StringBuilder sb = new StringBuilder();
		sb.append("Unique Cases: ")
				.append(uniqueCases.keySet().size())
				.append("\n");

		for (Map.Entry<String, Long> entry : uniqueCases.entrySet()) {
			sb.append("\t")
					.append(entry.getValue())
					.append(": ")
					.append(entry.getKey())
					.append("\n");
		}

		log.debug(sb.toString());
		if (!messages.isEmpty()) {
			log.warn("{} messages without match!", messages.size());
		}
	}


	public void addListener(PropertyChangeListener listener) {
		pts.addPropertyChangeListener(listener);
	}
}
