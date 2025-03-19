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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.FileNameUtils;
import com.eglobal.tools.parser.pojo.RawcomLine;
import com.eglobal.tools.validation.AcqLoaderPnl;

/**
 * @author egldt1029
 */
public class IssRawcomProducer implements Runnable {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IssRawcomProducer.class);
	private String failMsg = "Se requiere que la variable %s sea diferente de %s";
	private String[] pathFiles;
	private SynchronousQueue<RawcomLine> queue;
	private int percent;
	private String startTime;
	private String endTime;
	private TimeIntervalChecker timeIntervalChecker;
	private PropertyChangeSupport property;
	private ExecutorService executorService;
	private IFileProccesor<File> fileProcessor;

	public IssRawcomProducer(String[] pathFiles, SynchronousQueue<RawcomLine> queue) {
		this.pathFiles = pathFiles;
		this.queue = queue;
		this.executorService = Executors.newSingleThreadExecutor();
		this.property = new PropertyChangeSupport(this);
	}

	public IssRawcomProducer(String[] pathFiles, String startTime, String endTime, SynchronousQueue<RawcomLine> queue) {
		this.pathFiles = Objects.requireNonNull(pathFiles, String.format(failMsg, "pathFiles", "Null"));
		this.queue = Objects.requireNonNull(queue, String.format(failMsg, "queue", "Null"));
		this.timeIntervalChecker = new TimeIntervalChecker("HH:mm:ss");
		this.startTime = Objects.requireNonNull(startTime, String.format(failMsg, "startTime", "Null"));
		this.endTime = Objects.requireNonNull(endTime, String.format(failMsg, "endTime", "Null"));
		this.executorService = Executors.newSingleThreadExecutor();
		this.property = new PropertyChangeSupport(this);
	}

	@Override
	public void run() {
		this.processFile();
	}

	@Deprecated
	public Void execute() {
		for (String pathFile : this.pathFiles) {
			File f = new File(pathFile);
			log.debug("pathFile: " + pathFile);
			String message = "[Producer] Path file: " + pathFile;
			property.firePropertyChange("text", "", message);
			long lines = 0;
			long fileSize = f.length();
			log.debug("File size: " + fileSize);
			message = "[Producer] File size: " + fileSize;
			property.firePropertyChange("text", "", message);
			long readedBytes = 0L;
			try (BufferedInputStream dis = new BufferedInputStream(new FileInputStream(f))) {
				do {
					List<String> parts = new ArrayList<>();
					while (dis.available() != 0) {
						byte b = 0;
						b = (byte) dis.read();
						readedBytes++;
						if (b == 91) {
							// [
							// ByteBuffer bf = ByteBuffer.allocate(64);
							byte[] arr = new byte[64];
							int pos = 0;
							while (b != 93) {
								// ]
								b = (byte) dis.read();
								readedBytes++;
								// bf.put(b);
								arr[pos++] = b;
							}
							byte[] bytes = new byte[pos - 1];
							// bf.clear();
							System.arraycopy(arr, 0, bytes, 0, pos - 1);
							// bf.get(bytes);
							parts.add(new String(bytes));
							if (parts.size() == 7) {
								break;
							}
						}
					}
					if (parts.size() == 7) {
						lines++;
						int len = Integer.parseInt(parts.get(6).substring(parts.get(6).indexOf(':') + 1).trim());
						byte[] data = new byte[len];
						dis.read(data);
						readedBytes += len;
						RawcomLine line = new RawcomLine();
						line.setTimestamp(parts.get(0).substring(3));
						line.setDispatcher(Integer.parseInt(parts.get(1).substring(parts.get(1).indexOf(':') + 1).trim()));
						line.setPan(parts.get(2).substring(parts.get(2).indexOf(':') + 1).trim());
						line.setIap(parts.get(3).substring(parts.get(3).indexOf(':') + 1).trim());
						line.setListProcessor(parts.get(4).substring(parts.get(4).indexOf(':') + 1).trim());
						line.setReadWrite(parts.get(5).charAt(4));
						line.setLength(len);
						line.setRawByteData(data);
						boolean isWithinInterval = true;
						if (line.getTimestamp().length() >= 8) {
							isWithinInterval = timeIntervalChecker.isTimeWithinInterval(line.getTimestamp().substring(0, 8), startTime, endTime);
						}
						if (isWithinInterval) {
							// System.out.println(line.getTimestamp());
							queue.put(line);
							++lines;
						}
					}
					int newPercent = (int) (readedBytes * 100 / fileSize);
					if (percent != newPercent) {
						property.firePropertyChange("percent", percent, newPercent);
						percent = newPercent;
						log.debug("File size: " + fileSize + "\nReaded bytes:" + readedBytes + "\nPercent: " + newPercent);
					}
				} while 
				// Thread.sleep(2);
				(dis.available() != 0);
				RawcomLine lineEnd = new RawcomLine();
				lineEnd.setRawData("<END FILE " + f.getName() + ">");
				queue.put(lineEnd);
			} catch (FileNotFoundException e) {
				log.error("Archivo no encontrado", e);
			} catch (IOException e) {
				log.error("Error al leer el archivo", e);
			} catch (InterruptedException e) {
				log.error("Error en el hilo", e);
			} catch (Throwable t) {
				log.error("Excepcion no controlada", t);
			}
			log.debug("Done " + lines + " procesed");
			message = "[Producer] " + lines + " lineas leídas";
			property.firePropertyChange("text", "", message);
			message = "[Producer] Terminado";
			property.firePropertyChange("text", "", message);
		}
		RawcomLine lineEnd = new RawcomLine();
		lineEnd.setRawData("<END>");
		try {
			queue.put(lineEnd);
		} catch (InterruptedException e) {
			log.error("Error en el hilo (al escribir <END>)", e);
		}
		return null;
	}

	public void processFile() {
		for (java.lang.String strPath : pathFiles) {
			strPath = Objects.requireNonNull(strPath, String.format(failMsg, "strPath", "Null"));
			File file = Paths.get(strPath).toFile();
			String nameFile = file.getName();
			String extensionFile = FileNameUtils.getExtension(file.toPath());
			long sizeFile = file.length();
			property.firePropertyChange("text", "", String.format("[Producer] Path file: %s", nameFile));
			property.firePropertyChange("text", "", String.format("[Producer] Size file: %s", sizeFile));
			this.fileProcessor = this.fileProcessorSelector(extensionFile, file);
			if (extensionFile.equals("gz")) {
				this.processGZipFile(file);
			} else {
				this.processTextFile();
			}
		}
		property.firePropertyChange(AcqLoaderPnl.ENABLED_START_BUTTON, false, true);
		property.firePropertyChange("text", "", "[Producer] Terminado");
		try {
			executorService.awaitTermination(1, TimeUnit.SECONDS);
			executorService.shutdownNow();
		} catch (InterruptedException e) {
			log.error("Error al esperar el hilo que actualiza el progress bar", e);
			executorService.shutdownNow();
		}
		RawcomLine lineEnd = new RawcomLine();
		lineEnd.setRawData("<END>");
		try {
			queue.put(lineEnd);
		} catch (InterruptedException e) {
			log.error("Error en el hilo (al escribir <END>)", e);
		}
	}

	public IFileProccesor<File> fileProcessorSelector(String extensionFile, File file) {
		if (extensionFile.equals("gz")) {
			return new GzipFileProcessorImpl(this.startTime, this.endTime, this.queue, file);
		}
		return new TextFileProccesorImpl(this.startTime, this.endTime, this.queue, file);
	}

	public void addListener(PropertyChangeListener listener) {
		property.addPropertyChangeListener(listener);
	}

	private void processGZipFile(File file) {
		property.firePropertyChange(AcqLoaderPnl.PROPERTY_INDETERMINATE, false, true);
		property.firePropertyChange(AcqLoaderPnl.STRING_PROGRESS_BAR, "", String.format("Wait processing file %s...", file.getName()));
		try {
			fileProcessor.process();
		} catch (InterruptedException | IOException e) {
			log.error("Error al ejecutar el proceso de lectura de archivos.", e);
		}
		property.firePropertyChange(AcqLoaderPnl.PROPERTY_INDETERMINATE, true, false);
		property.firePropertyChange(AcqLoaderPnl.STRING_PROGRESS_BAR, "", "Done!");
	}

	private void processTextFile() {
		Runnable task = () -> {
			while (fileProcessor.loadingPercent() < 100) {
				property.firePropertyChange("percent", 0, fileProcessor.loadingPercent() + 1);
			}
		};
		try {
			executorService.submit(task);
			fileProcessor.process();
			property.firePropertyChange("text", "", String.format("[Producer] %d lineas leídas", fileProcessor.linesRead()));
		} catch (InterruptedException | IOException e) {
			log.error("", e);
		}
	}
}
