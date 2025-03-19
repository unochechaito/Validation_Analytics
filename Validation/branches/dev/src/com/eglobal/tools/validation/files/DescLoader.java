/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.files;

import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DescLoader {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DescLoader.class);
	private static final int QUEUE_SIZE = 1000;
	private DescConsumer descConsumer;
	private DescProducer descProducer;
	private BlockingQueue<String> queue;

	public DescLoader(String env, String[] inputPaths) {
		queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
		descProducer = new DescProducer(env, inputPaths, queue);
		descConsumer = new DescConsumer(env, queue);
	}

	public DescLoader(String env, String[] inputPaths, String startTime, String endTime) {
		queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
		descProducer = new DescProducer(env, inputPaths, startTime, endTime, queue);
		descConsumer = new DescConsumer(env, queue);
	}

	public void start() {
		ExecutorService executorServiceMain = Executors.newSingleThreadScheduledExecutor();
		executorServiceMain.execute(() -> {

			ExecutorService executorProducer = Executors.newSingleThreadExecutor(r -> new Thread(r, "Desc Producer"));
			log.debug("Executor Producer created!");
			ExecutorService executorConsumer = Executors.newSingleThreadExecutor(r -> new Thread(r, "Desc Consumer"));
			log.debug("Executor Consumer created!");

			executorProducer.submit(descProducer);
			log.trace("tarea descProducer enviada.");
			executorConsumer.submit(descConsumer::execute);
			log.trace("tarea descConsumer enviada.");

			executorProducer.shutdown();
			executorConsumer.shutdown();

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
		descProducer.addListener(listener);
		descConsumer.addListener(listener);
	}
}
