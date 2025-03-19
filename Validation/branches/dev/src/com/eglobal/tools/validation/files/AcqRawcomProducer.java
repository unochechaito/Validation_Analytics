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
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.eglobal.tools.parser.pojo.RawcomLine;
import com.eglobal.tools.validation.utils.FileNameUtils;
import com.eglobal.tools.validation.view.BaseLoaderPanel;

/**
 * @author egldt1029
 */
public class AcqRawcomProducer implements Runnable {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AcqRawcomProducer.class);
	private String failMsg = "Se requiere que la variable %s sea diferente de %s";
	private String[] pathFiles;
	private BlockingQueue<RawcomLine> queue;
	private PropertyChangeSupport property;
	private String startTime;
	private String endTime;
	private ExecutorService executorService;

	public AcqRawcomProducer(String[] pathFiles, String startTime, String endTime, BlockingQueue<RawcomLine> queue) {
		this.pathFiles = Objects.requireNonNull(pathFiles, String.format(this.failMsg, "pathFiles", "Null"));
		this.queue = Objects.requireNonNull(queue, String.format(this.failMsg, "queue", "Null"));
		this.startTime = Objects.requireNonNull(startTime, String.format(this.failMsg, "startTime", "Null"));
		this.endTime = Objects.requireNonNull(endTime, String.format(this.failMsg, "endTime", "Null"));
		this.executorService = Executors.newSingleThreadExecutor();
		this.property = new PropertyChangeSupport(this);
	}

	@Override
	public void run() {
		log.trace("Execute new Thread: AcqRawComLoader");
		log.trace("Running process file's:\n\t{}", (Object[]) pathFiles);
		
		if(this.pathFiles == null || this.pathFiles.length == 0) {
			log.warn("Variable pathFiles esta vacia o es null: {}", (Object[]) this.pathFiles);
			throw new NullPointerException("Variable pathFiles esta vacia o es null");
		}
		
		for (String strPath : pathFiles) {
			log.trace("Ciclo de archivos");
			Objects.requireNonNull(strPath, String.format(failMsg, "strPath", "Null"));
			File file = Paths.get(strPath).toFile();
			log.trace("archivo parseado");
			String nameFile = file.getName();
			log.trace("nombre de archivo: {}", nameFile);
			String extensionFile = FileNameUtils.getExtension(nameFile);
			log.trace("extension de archivo: {}", extensionFile);
			long sizeFile = file.length();
			log.trace("tamano del archivo: {}", sizeFile);

			property.firePropertyChange("text", "", String.format("[Producer] Path file: %s", nameFile));
			log.trace("[Producer] Path file: {}", nameFile);
			property.firePropertyChange("text", "", String.format("[Producer] Size file: %s", sizeFile));
			log.trace("[Producer] Size file: {}", sizeFile);
			
			IFileProccesor<File> fileProcessor = this.fileProcessorSelector(extensionFile);
			if (isGzFile(extensionFile)) {
				log.trace("procesando archivo zip.");
				this.processGZipFile(fileProcessor, file);
			} else {
				log.trace("procesando archivo de texto.");
				this.processTextFile(fileProcessor, file);
			}
			property.firePropertyChange("text", "", String.format("[Producer] %d lineas leídas", fileProcessor.linesRead()));
		}
		property.firePropertyChange("text", "", "[Producer] Terminado");
		log.trace("Se apaga hilo de JProgressBar");

		property.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, false, true);
		property.firePropertyChange(BaseLoaderPanel.STRING_PROGRESS_BAR, "", "Procesando datos extraidos de sus archivos...");
		
		executorService.shutdown();
		log.trace("Se apago el hilo.");
		RawcomLine lineEnd = new RawcomLine();
		lineEnd.setRawData("<END>");
		try {
			queue.put(lineEnd);
		} catch (InterruptedException e) {
			log.error("Error en el hilo (al escribir <END>)", e);
			Thread.currentThread().interrupt();
		}
	}

	public IFileProccesor<File> fileProcessorSelector(String extensionFile) {
		log.trace("Selector de tipo de archivo.");
		if (isGzFile(extensionFile)) {
			return new GzipFileProcessorImpl(this.startTime, this.endTime, this.queue);
		}
		return new TextFileProccesorImpl(this.startTime, this.endTime, this.queue);
	}

	public void addListener(PropertyChangeListener listener) {
		property.addPropertyChangeListener(listener);
	}

	private boolean isGzFile(String extension) {
		return extension.equals("gz");
	}

	private void processTextFile(IFileProccesor<File> fileProcessor, File file) {
		Runnable task = () -> {
			int oldValue = 0;
			while (fileProcessor.loadingPercent() < 100) {
				if(oldValue < fileProcessor.loadingPercent()) {
					property.firePropertyChange(BaseLoaderPanel.PERCENT, 0, fileProcessor.loadingPercent() + 1);
					oldValue = fileProcessor.loadingPercent();
				}
			}
		};

		try {
			executorService.execute(task);
			fileProcessor.setFile(file);
			fileProcessor.process();
			property.firePropertyChange(BaseLoaderPanel.PERCENT, 0, 100);
		} catch (InterruptedException e) {
			log.error("Error de hilo en el proceso de lectura de archivos.", e);
			Thread.currentThread().interrupt();
		} catch (Exception e){
			log.error("Error al ejecutar proceso de lectura de archivos.", e);
		}
	}

	private void processGZipFile(IFileProccesor<File> fileProcessor, File file) {
		property.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, false, true);
		property.firePropertyChange(BaseLoaderPanel.STRING_PROGRESS_BAR, "", String.format("Wait processing file %s...", file.getName()));
		try {

			fileProcessor.setFile(file);
			fileProcessor.process();
		} catch (InterruptedException e) {
			log.error("Error de hilo en el proceso de lectura de archivos.", e);
			Thread.currentThread().interrupt();
		} catch (Exception e){
			log.error("Error al ejecutar proceso de lectura de archivos.", e);
		}
		property.firePropertyChange(BaseLoaderPanel.PROPERTY_INDETERMINATE, true, false);
		property.firePropertyChange(BaseLoaderPanel.STRING_PROGRESS_BAR, "", "Done!");
	}
}
