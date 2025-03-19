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
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.eglobal.tools.validation.db.model.EIssuerView;
import com.eglobal.tools.validation.db.repository.IssuerViewRepository;
import com.eglobal.tools.validation.pojo.IssuerView;
import com.google.gson.Gson;

public class IssRawcomConsumer implements Runnable {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IssRawcomConsumer.class);
	private SynchronousQueue<RawcomLine> queue;
	private long registers;
	private long parsedRegisters;
	private long requirements;
	private long responses;
	private PropertyChangeSupport pts = new PropertyChangeSupport(this);
	private Map<String, Long> uniqueCases;
	private IssuerViewRepository issViewRepository;
	private final int BATCH_SIZE = 5000;
	private final List<String> DISCARD_TXN;
	private final String env;

	public IssRawcomConsumer(String env, SynchronousQueue<RawcomLine> queue) {
		this.queue = queue;
		this.env = env;
		uniqueCases = new HashMap<>();
		issViewRepository = new IssuerViewRepository();
		DISCARD_TXN = new ArrayList<>();
		DISCARD_TXN.add("SIGN");
		DISCARD_TXN.add("ECHO");
		DISCARD_TXN.add("KEY EXCHANGE");
	}

	@Override
	public void run() {
		execute();
	}

	public Void execute() {
		Gson gson = new Gson();
		MessageMatcher matcher = new MessageMatcher(new String[] {"11", "37"});
		String smessage = "[Consumer] Usando campos para match " + gson.toJson(matcher.getFieldsToMatch());
		pts.firePropertyChange("text", "", smessage);
		List<Message> messages = new ArrayList<>();
		try (Connection conn = DBManager.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			smessage = "[Consumer] Obteniendo conexion a la Base de Datos";
			pts.firePropertyChange("text", "", smessage);
			smessage = "[Consumer] Inicializando la Base de Datos";
			pts.firePropertyChange("text", "", smessage);
			Thread.sleep(10);
			List<EIssuerView> list = new ArrayList<>();
			while (true) {
				RawcomLine line = queue.poll();
				if (line != null) {
					if ("<END>".equals(line.getRawData())) {
						break;
					} else if (line.getRawData() != null && line.getRawData().startsWith("<END FILE")) {
						String fileName = line.getRawData().replace("<END FILE ", "").replace(">", "").trim();
						smessage = System.lineSeparator() + "[Consumer] Terminado (" + fileName + ")" + System.lineSeparator();
						pts.firePropertyChange("text", "", smessage);
						continue;
					}
					try {
						if (line.getIap().toUpperCase().contains("TOTAL") || line.getIap().toUpperCase().contains("STANDIN")) {
							continue;
						}
//                 IParser parser = ParserFactory.getSuitableParser(new String(line.getRawByteData()));
						IParser parser = null;
						if (line.getIap() == null) {
							log.error("No IAP for line: " + line);
							continue;
						}
						String iap = line.getIap().toUpperCase();
						if (new String(line.getRawByteData()).startsWith("ISO") && "POS".equals(env)) {
							parser = ParserFactory.getParser(ParserFactory.Type.ISO);
						} else if (new String(line.getRawByteData()).startsWith("ISO") && "ATM".equals(env)) {
							parser = ParserFactory.getParser(ParserFactory.Type.ISO_ATM);
						} else if (iap.contains("MC") || iap.contains("MASTER") || iap.contains("MDS")) {
							parser = ParserFactory.getParser(ParserFactory.Type.MASTERCARD);
						} else if (iap.contains("PLUS") || iap.contains("VISA")) {
							parser = ParserFactory.getParser(ParserFactory.Type.VISA);
						} else 
//						} else if(iap.contains("AMEX")) {
//							parser = ParserFactory.getParser(ParserFactory.Type.AMEX_TPVS);
						if (iap.contains("DISCV")) {
							parser = ParserFactory.getParser(ParserFactory.Type.DISCOVER);
						} else if (iap.contains("STRATUS") || new String(line.getRawByteData()).startsWith("1200") || new String(line.getRawByteData()).startsWith("1210") || new String(line.getRawByteData()).startsWith("1400") || new String(line.getRawByteData()).startsWith("1410")) {
							parser = ParserFactory.getParser(ParserFactory.Type.STRATUS);
						} else if (iap.contains("JCB")) {
							parser = ParserFactory.getParser(ParserFactory.Type.JCB);
						}
						if (parser == null) {
							log.error("No suitable parser for line" + line);
							continue;
						} else {
							String cad = new String(line.getRawByteData(), Charset.forName("ISO-8859-1"));
							if (parser instanceof MastercardParser && "POS".equals(env)) {
								if (cad.length() % 2 != 0) {
									String panebcdic = Converter.asciiStringToEbcdicString(line.getPan());
									int index = cad.lastIndexOf(panebcdic);
									cad = cad.substring(0, index + panebcdic.length()) + cad.substring(index + panebcdic.length() + 1);
								}
							}
							if (!cad.startsWith("ISO") && !iap.contains("DISCV")) {
								cad = cad.replaceAll("\\*", "0").replaceAll("x", "0");
								line.setRawByteData(Hex.decodeHex(cad));
							}
						}
						Message message = (Message) parser.parse(line.getRawByteData());
						message.setTimestamp(line.getTimestamp().substring(0, 2) + line.getTimestamp().substring(3, 5) + line.getTimestamp().substring(6, 8));
						parsedRegisters++;
						if (message.getMti().charAt(2) % 2 == 0 && line.getReadWrite() == 'W') {
							requirements++;
							messages.add(message);
						} else if (message.getMti().charAt(2) % 2 != 0 && line.getReadWrite() == 'R') {
							responses++;
							Iterator<Message> it = messages.iterator();
							while (it.hasNext()) {
								Message m = it.next();
								if (matcher.match(m, message)) {
									IssuerView issView = new IssuerView();
									issView.setRequirementMessage(m);
									issView.setResponseMessage(message);
									issView.setBin(line.getPan().length() >= 8 ? line.getPan().substring(0, 8) : line.getPan());
									issView.setDispatcher("" + line.getDispatcher());
									issView.setTimestamp(m.getTimestamp());
									IMessageCategorizer categorizer = MessageCategorizerFactory.getCategorizer(message.getType());
									String category = categorizer.categorize(m);
									issView.setCategory(category);
									String fingerprint = FingerPrintMessage.calculateFingerPrint(m);
									issView.setFingerprint(fingerprint);
									if ("POS".equals(env)) {
										issView.setKey(line.getPan());
									} else if ("ATM".equals(env)) {
										issView.setKey(line.getPan() + "-" + m.getField("37") + "-" + m.getField("11"));
									}
									it.remove();
									EIssuerView t = IssuerView.toEntity(issView);
									if (category != null && !isTxnDiscard(category)) {
										log.debug("Key: " + t.getKey());
										list.add(t);
									}
									if (list.size() == BATCH_SIZE) {
										log.debug("" + BATCH_SIZE + " insert...");
										issViewRepository.insert(list, conn);
										conn.commit();
										list = new ArrayList<>();
									}
									if (!uniqueCases.keySet().contains("" + line.getDispatcher() + "-" + category)) {
										uniqueCases.put("" + line.getDispatcher() + "-" + category, 1L);
									} else {
										uniqueCases.replace("" + line.getDispatcher() + "-" + category, uniqueCases.get("" + line.getDispatcher() + "-" + category) + 1);
									}
									break;
								}
							}
						}
					} catch (SQLException e) {
						log.error("Database error processing line " + line.getTimestamp(), e);
					} catch (DecoderException ex) {
						log.error("Error decoding hex msg: {}", line.getTimestamp(), ex);
					} catch (Throwable e) {
						log.error("Excepcion no controlada: {}", line.getTimestamp(), e);
					}
					registers++;
				}
			}
			if (list.size() > 0) {
				issViewRepository.insert(list, conn);
				conn.commit();
			}
		} catch (InterruptedException e) {
			log.error("Thread interrupted", e);
		} catch (SQLException e) {
			log.error("Database error", e);
		}
		log.debug("Done " + registers + " processed - Parsed registers " + parsedRegisters + " - Requirements " + requirements + " - Responses " + responses);
		smessage = "[Consumer] Terminado ";
		pts.firePropertyChange("text", "", smessage);
		StringBuilder sb = new StringBuilder();
		sb.append("Unique Cases: ");
		sb.append(uniqueCases.keySet().size());
		sb.append("\n");
		for (String _case : uniqueCases.keySet()) {
			sb.append("\t");
			sb.append("" + uniqueCases.get(_case));
			sb.append(":");
			sb.append("\t");
			sb.append(_case);
			sb.append("\n");
		}
		log.debug(sb.toString());
		if (messages.size() != 0) {
			log.warn("" + messages.size() + " messages without match!");
		}
		return null;
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
