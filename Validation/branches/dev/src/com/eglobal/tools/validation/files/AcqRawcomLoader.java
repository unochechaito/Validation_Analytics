/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.files;

import java.beans.PropertyChangeListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.eglobal.tools.parser.pojo.RawcomLine;

public class AcqRawcomLoader {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AcqRawcomLoader.class);
	private AcqRawcomConsumer acqRawcomConsumer;
	private AcqRawcomProducer acqRawcomProducer;
	private BlockingQueue<RawcomLine> queue;
	private String env;
	private static final int QUEUE_SIZE = 1000;

	public AcqRawcomLoader(String env, String[] inputPaths, String startTime, String endTime) {
		this.env = env;
		queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
		acqRawcomProducer = new AcqRawcomProducer(inputPaths, startTime, endTime, queue);
		acqRawcomConsumer = new AcqRawcomConsumer(env, queue);
	}

	public void start() {
		ExecutorService executorServiceMain = Executors.newSingleThreadScheduledExecutor();
		executorServiceMain.execute(() -> {
			ExecutorService executorProducer = Executors.newSingleThreadExecutor(r -> new Thread(r, "Acq Rawcom Producer"));
			log.debug("Executor Producer created!");

			ExecutorService executorConsumer = Executors
					.newSingleThreadExecutor(r -> new Thread(r, "Acq Rawcom Consumer"));
			log.debug("Executor Consumer created!");

			executorProducer.execute(acqRawcomProducer);
			log.trace("Se envio tarea acqRawcomProducer");
			executorConsumer.execute(acqRawcomConsumer::execute);
			log.trace("Se envio tarea acqRawcomConsumer::execute");

			executorProducer.shutdown();
			log.trace("se envia shutdown a producer");
			executorConsumer.shutdown();
			log.trace("se envia shutdown a consumer");

			try {
				if (!executorProducer.awaitTermination(1, TimeUnit.HOURS)) {
					log.warn("Producer did not terminate in time, forcing shutdown.");
					executorProducer.shutdownNow();
				}
				if (!executorConsumer.awaitTermination(1, TimeUnit.HOURS)) {
					log.warn("Consumer did not terminate in time, forcing shutdown.");
					executorConsumer.shutdownNow();
				}
			} catch (InterruptedException e) {
				log.error("Executor termination interrupted", e);
				executorConsumer.shutdownNow();
				executorProducer.shutdownNow();
				Thread.currentThread().interrupt(); // Restore interrupted status
			}
		});
		executorServiceMain.shutdown();
	}

	public void addListener(PropertyChangeListener listener) {
		acqRawcomProducer.addListener(listener);
		acqRawcomConsumer.addListener(listener);
	}
}
