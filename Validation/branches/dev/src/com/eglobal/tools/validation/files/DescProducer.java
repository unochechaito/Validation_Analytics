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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import com.eglobal.tools.validation.utils.FileNameUtils;
import com.eglobal.tools.validation.view.BaseLoaderPanel;

/**
 * @author egldt1029
 */
public class DescProducer implements Runnable {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DescProducer.class);
	private String[] pathFiles;
	private BlockingQueue<String> queue;
	private int percent = 0;
	private PropertyChangeSupport property;
	private ExecutorService executorService;
	private String startTime;
	private String endTime;
	private TimeIntervalChecker timeIntervalChecker;
	private volatile long fileSize = 0L;
	private String fileName;
	private String fileExtension;
	private String env;
	private volatile long readedBytes = 0L;

	public DescProducer(String env, String[] pathFiles, BlockingQueue<String> queue) {
		this.pathFiles = pathFiles;
		this.queue = queue;
		this.env = env;
		this.executorService = Executors.newSingleThreadExecutor();
		this.property = new PropertyChangeSupport(this);
	}

	public DescProducer(String env, String[] pathFiles, String startTime, String endTime, BlockingQueue<String> queue) {
		this.pathFiles = pathFiles;
		this.queue = queue;
		this.timeIntervalChecker = new TimeIntervalChecker("HHmmss");
		this.startTime = startTime;
		this.endTime = endTime;
		this.env = env;
		this.executorService = Executors.newSingleThreadExecutor();
		this.property = new PropertyChangeSupport(this);
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (NullPointerException e) {
			log.error("Error durante la ejecucion de la tarea", e);
		}
	}

	public void execute() {
		log.trace("Executando extraccion de data en los archivos: {}", (Object) pathFiles);

		if(pathFiles == null || pathFiles.length == 0) {
			throw new NullPointerException("Variable pathFiles esta vacia o es Null");
		}

		for (String pathFile : pathFiles) {

			if(pathFile == null || pathFile.isEmpty()) {
				throw new NullPointerException("La varibale pathFile esta vacia o es Null");
			}
			
			log.debug("pathFile: " + pathFile);
			File path = Paths.get(pathFile).toFile();
			
			this.property.firePropertyChange("text", "", String.format("[Producer] Path file: %s", pathFile));
			this.fileName = path.getName();
			log.trace("fileName: {}", fileName);
			this.fileExtension = FileNameUtils.getExtension(fileName);
			log.trace("fileExtension: {}", fileExtension);
			this.readedBytes = 0L;
			this.fileSize = path.length();
			log.trace("fileSize: {}", fileSize);
			
			if (this.fileExtension.equals("gz")) {
				this.processGZipFile(path);
			} else {
				this.processTextFile(path);
			}
			log.debug("File size: " + fileSize);
			this.property.firePropertyChange("text", "", String.format("[Producer] File size: %d", fileSize));
		}
		try {
			this.executorService.shutdown();
			this.executorService.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.warn("Error al terminar la tarea del executor service", e);
			this.executorService.shutdownNow();
		}
		try {
			this.queue.put("<END>");
		} catch (InterruptedException e) {
			log.error("Error en el hilo (al escribir <END>)", e);
		}
		property.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, false, true);
		property.firePropertyChange(BaseLoaderPanel.STRING_PROGRESS_BAR, "", "Procesando datos extraidos de sus archivos...");
		return;
	}

	private void processFile(BufferedReader reader) throws IOException, InterruptedException {
		long lines = 0L;
		String line;
		while ((line = reader.readLine()) != null) {
			this.readedBytes += line.length() + System.lineSeparator().length();
			boolean isWithinInterval = true;
			if ("ATM".equals(env)) {
				if (line.length() >= 77) {
					isWithinInterval = timeIntervalChecker.isTimeWithinInterval(line.substring(71, 77), startTime, endTime);
				}
			}
			if ("POS".equals(env)) {
				if (!isAdditional(line)) {
					if (line.length() >= 43) {
						isWithinInterval = timeIntervalChecker.isTimeWithinInterval(line.substring(37, 43), startTime, endTime);
					}
				} else {
					if (line.length() >= 10) {
						isWithinInterval = timeIntervalChecker.isPeriodWithinInterval(line.substring(8, 10), startTime, endTime, line.substring(0, 6));
					}
				}
			}
			if (isWithinInterval) {
				queue.put(line);
				lines++;
			}
		}
		queue.put("<END FILE " + this.fileName + ">");
		log.debug("Done " + lines + " procesed");
		this.property.firePropertyChange("text", "", "[Producer] " + lines + " lineas leídas");
		this.property.firePropertyChange("text", "", "[Producer] Terminado");
	}

	public void addListener(PropertyChangeListener listener) {
		this.property.addPropertyChangeListener(listener);
	}

	private boolean isAdditional(String line) {
		return line.length() != 500;
	}

	private void processGZipFile(File file) {
		this.property.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, false, true);
		this.property.firePropertyChange(BaseLoaderPanel.STRING_PROGRESS_BAR, "", String.format("Wait processing file %s...", file.getName()));
		try (GZIPInputStream inputFile = new GZIPInputStream(new FileInputStream(file))) {
			java.io.BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile));
			this.processFile(reader);
		} catch (IOException e) {
			log.error("Error al descomprimir archivo.", e);
		} catch (InterruptedException e) {
			log.error("Interrupcion al descomprimir archivo.", e);
		}
		this.property.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, true, false);
		this.property.firePropertyChange(BaseLoaderPanel.STRING_PROGRESS_BAR, "", "Done!");
	}

	private void processTextFile(File file) {
		Runnable calculateNewPercentTask = () -> {
			while (this.percent < 100) {
				if (this.readedBytes != 0) {
					int newPercent = (int) (this.readedBytes * 100 / this.fileSize);
					if (this.percent != newPercent) {
						this.property.firePropertyChange(BaseLoaderPanel.PERCENT, this.percent, newPercent);
						log.debug("File size: " + fileSize + "\nReaded bytes:" + readedBytes + "\nPercent: " + newPercent);
						this.percent = newPercent;
					}
				}
			}
		};
		property.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, false, false);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			this.fileSize = file.length();
			this.executorService.execute(calculateNewPercentTask);
			this.processFile(reader);
		} catch (IOException e) {
			log.error("Error al leer el archivo.", e);
		} catch (InterruptedException e) {
			log.error("Interrupcion al leer el archivo.", e);
		}
	}
}
