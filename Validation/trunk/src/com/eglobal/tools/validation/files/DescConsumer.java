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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import com.eglobal.tools.parser.parsers.DescAtm34Parser;
import com.eglobal.tools.parser.parsers.IDescParser;
import com.eglobal.tools.parser.parsers.IParser;
import com.eglobal.tools.parser.parsers.LayoutParserFactory;
import com.eglobal.tools.parser.parsers.LayoutParserFactory.Type;
import com.eglobal.tools.parser.pojo.FieldExt;
import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.db.model.EDescLinea;
import com.eglobal.tools.validation.db.model.EDescLineaAdd;
import com.eglobal.tools.validation.db.repository.DescAddRepository;
import com.eglobal.tools.validation.db.repository.DescRepository;
import com.eglobal.tools.validation.pojo.DescLinea;
import com.eglobal.tools.validation.pojo.DescLineaAdd;

public class DescConsumer implements Runnable {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DescConsumer.class);
	private SynchronousQueue<String> queue;
//	IDescParser parser;
	private long registers;
	private long parsedRegisters;
	private PropertyChangeSupport pts = new PropertyChangeSupport(this);
	private DescRepository descRepository;
	private String env;
	private DescAddRepository descAddRepository;
	private final int BATCH_SIZE = 5000;

	public DescConsumer(String env, SynchronousQueue<String> queue) {
		this.env = env;
		this.queue = queue;
		descRepository = new DescRepository();
		descAddRepository = new DescAddRepository();
	}

	@Override
	public void run() {
		execute();
	}

	public Void execute() {
		String smessage = "[Consumer]";
		pts.firePropertyChange("text", "", smessage);
		List<Message> messages = new ArrayList<>();
		try (Connection conn = DBManager.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			logAndFirePropertyChange("[Consumer] Obteniendo conexion a la Base de Datos");
			logAndFirePropertyChange("[Consumer] Inicializando la Base de Datos");
			List<EDescLinea> listDescLinea = new ArrayList<>();
			List<EDescLineaAdd> listDescLineaAdd = new ArrayList<>();
			while (true) {
				String line = queue.poll(10, TimeUnit.MILLISECONDS);
				if (line == null) {
					continue;
				}
				if ("<END>".equals(line)) {
					break;
				} else if (line != null && line.startsWith("<END FILE")) {
					String fileName = line.replace("<END FILE ", "").replace(">", "").trim();
					smessage = System.lineSeparator() + "[Consumer] Terminado (" + fileName + ")" + System.lineSeparator();
					pts.firePropertyChange("text", "", smessage);
					continue;
				}
				try {
					Message message = null;
					if ("ATM".equals(env)) {
						IDescParser parser = DescAtm34Parser.getInstance();
						message = (Message) parser.parse(line);
					} else if ("POS".equals(env)) {
						Type type = null;
						if (!isAdditional(line)) {
							type = Type.DESC_BASE;
						} else {
							if (line.length() >= 20) {
								String number = line.substring(17, 20);
								type = LayoutParserFactory.getTypeForname(number);
								if (type == null) {
									log.warn("Not a recognized additional {}", number);
									continue;
								}
							}
						}
						IParser parser = LayoutParserFactory.getLayoutParser(type);
						if (parser == null) {
							log.warn("Not a suitable parser for {}", line);
							continue;
						}
						message = (Message) parser.parse(line.getBytes());
					}
					if ("ATM".equals(env)) {
						DescLinea descLinea = new DescLinea();
						String segTrack2 = null;
						segTrack2 = (String) message.getField("SEG-TRACK2");
						if (segTrack2 != null && segTrack2.length() >= 8) {
							descLinea.setBin(segTrack2.substring(0, 8));
						} else {
							log.warn("Invalid BIN: {}", segTrack2);
						}
						Object acquirerDispatcher = null;
						acquirerDispatcher = message.getField("DESC_ATM-TRML-NBR");
						if (acquirerDispatcher != null) {
							descLinea.setAcquirerDispatcher(acquirerDispatcher.toString());
						} else {
							log.warn("Invalid Acquirer Dispatcher: {}", acquirerDispatcher);
						}
						Object issuerDispatcher = null;
						issuerDispatcher = message.getField("SEG-OUT-SWAP");
						if (issuerDispatcher != null) {
							descLinea.setIssuerDispatcher(issuerDispatcher.toString());
						} else {
							log.warn("Invalid Issuer Dispatcher: {}", issuerDispatcher);
						}
						String timestamp = null;
						timestamp = (String) message.getField("DESC_ATM-TRAN-TIME");
						if (timestamp != null) {
							descLinea.setTimestamp(timestamp);
						} else {
							log.warn("Invalid Timestamp: {}", timestamp);
						}
						Object segRrn = null;
						segRrn = message.getField("SEG-RRN");
						Object segAtmSwitchInSeqNbr = null;
						segAtmSwitchInSeqNbr = message.getField("DESC_ATM-SWITCH-IN-SEQ-NBR");
						if (segTrack2 != null && segRrn != null && segAtmSwitchInSeqNbr != null && timestamp != null) {
							descLinea.setKeyAcq(segTrack2.trim() + "-" + segRrn + "-" + segAtmSwitchInSeqNbr + "-" + timestamp);
						} else {
							log.warn("Invalid Acq Key: {}", message);
						}
						Object segRrnIss = message.getField("SEG-RRN-ISS");
						Object segAtmSwitchSeqNbr = message.getField("DESC_ATM-SWITCH-SEQ-NBR");
						if (segTrack2 != null && segRrn != null && segAtmSwitchSeqNbr != null) {
							String keyIss = segTrack2.trim() + "-" + (segRrnIss == null || segRrnIss.toString().trim().isEmpty() ? segRrn : segRrnIss) + "-" + segAtmSwitchSeqNbr;
							descLinea.setKeyIss(keyIss);
						} else {
							log.warn("Invalid Iss Key: {}", message);
						}
						descLinea.setData(message);
						listDescLinea.add(DescLinea.toEntity(descLinea));
						log.debug("Parsed message: {}", message);
						parsedRegisters++;
						if (listDescLinea.size() == BATCH_SIZE) {
							descRepository.insert(listDescLinea, conn);
							conn.commit();
							listDescLinea = new ArrayList<>();
						}
					} else if ("POS".equals(env)) {
						if (!isAdditional(line)) {
							// DESC Base
							DescLinea descLinea = new DescLinea();
							String segTrack2 = null;
							segTrack2 = (String) message.getField("POS-SEG-TRACK2");
							if (segTrack2 != null && segTrack2.length() >= 8) {
								descLinea.setBin(segTrack2.substring(0, 8));
							} else {
								log.warn("Invalid BIN: {}", segTrack2);
							}
							Object acquirerDispatcher = null;
							acquirerDispatcher = message.getField("DESC-POS-TERML-NBR");
							if (acquirerDispatcher != null) {
								descLinea.setAcquirerDispatcher(acquirerDispatcher.toString());
							} else {
								log.warn("Invalid Acquirer Dispatcher: {}", acquirerDispatcher);
							}
							Object issuerDispatcher = null;
							issuerDispatcher = message.getField("POS-SEG-OUT-SWAP");
							if (issuerDispatcher != null) {
								descLinea.setIssuerDispatcher(issuerDispatcher.toString());
							} else {
								log.warn("Invalid Issuer Dispatcher: {}", issuerDispatcher);
							}
							String timestamp = null;
							timestamp = (String) message.getField("DESC-POS-TRAN-TIME");
							if (timestamp != null) {
								descLinea.setTimestamp(timestamp);
							} else {
								log.warn("Invalid Timestamp: {}", timestamp);
							}
							Object segRrn = null;
							segRrn = message.getField("POS-SEG-RRN");
							Object segAtmSwitchInSeqNbr = null;
							segAtmSwitchInSeqNbr = message.getField("POS-SEG-STAN");
							if (segTrack2 != null && segRrn != null && segAtmSwitchInSeqNbr != null && timestamp != null) {
								descLinea.setKeyAcq(segTrack2.trim() + "-" + segRrn + "-" + segAtmSwitchInSeqNbr + "-" + timestamp);
							} else {
								log.warn("Invalid Acq Key: {}", message);
							}
							//TODO Revisar que otros campos se pueden utilizar.
							if (segTrack2 != null) {
								String keyIss = segTrack2.trim();
								descLinea.setKeyIss(keyIss);
							}
							descLinea.setKey("" + message.getField("DESC-POS-PERIODO-DESC") + message.getField("DESC-POS-SEQ-DESC"));
							descLinea.setData(message);
							listDescLinea.add(DescLinea.toEntity(descLinea));
							log.debug("Parsed message: {}", message);
							parsedRegisters++;
							if (listDescLinea.size() == BATCH_SIZE) {
								descRepository.insert(listDescLinea, conn);
								conn.commit();
								listDescLinea = new ArrayList<>();
							}
						} else {
							//Adicionales
							//						log.warn("Additional {}", message);
							DescLineaAdd descLineaAdd = new DescLineaAdd();
							Object periodo = message.getField("Header.PERIODO");
							Object secuencia = message.getField("Header.SEQ");
							if (periodo != null && secuencia != null) {
								descLineaAdd.setDescKey("" + periodo + secuencia);
							} else {
								log.warn("Invalid additional key. Period: {} Seq: {} \nLine: {} \nMessage: {}", periodo, secuencia, line, message);
							}
							descLineaAdd.setData(message);
							listDescLineaAdd.add(DescLineaAdd.toEntity(descLineaAdd));
						}
						if (listDescLineaAdd.size() == BATCH_SIZE) {
							descAddRepository.insert(listDescLineaAdd, conn);
							conn.commit();
							listDescLineaAdd = new ArrayList<>();
						}
					}
				} catch (ClassCastException | IllegalArgumentException e) {
					log.error("Register cannot be parsed", e);
				} catch (Throwable e) {
					log.error("Excepcion no controlada: " + line, e);
				}
				registers++;
			}
			if (!listDescLinea.isEmpty()) {
				descRepository.insert(listDescLinea, conn);
				conn.commit();
			}
			if (!listDescLineaAdd.isEmpty()) {
				descAddRepository.insert(listDescLineaAdd, conn);
				conn.commit();
			}
		} catch (InterruptedException e) {
			log.error("Thread was interrupted", e);
			Thread.currentThread().interrupt();
		} catch (SQLException e) {
			log.error("Database error", e);
		} catch (Throwable e) {
			log.error("Excepcion no controlada", e);
		}
		log.debug("Done. Processed: {} - Parsed: {}", registers, parsedRegisters);
		smessage = "[Consumer] Terminado ";
		pts.firePropertyChange("text", "", smessage);
		if (!messages.isEmpty()) {
			log.warn("{} messages without match!", messages.size());
		}
		return null;
	}

	private void logAndFirePropertyChange(String message) {
		pts.firePropertyChange("text", "", message);
		log.info(message);
	}

	public void addListener(PropertyChangeListener listener) {
		pts.addPropertyChangeListener(listener);
	}

	private boolean isAdditional(String line) {
		if (line.length() != 500) {
			return true;
		}
		return false;
	}
}
