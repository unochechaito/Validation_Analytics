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

public class IssRawcomLoader {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IssRawcomLoader.class);
	private IssRawcomConsumer issRawcomConsumer;
	private IssRawcomProducer issRawcomProducer;
	private BlockingQueue<RawcomLine> queue;

	public IssRawcomLoader(String env, String[] inputPaths) {
		queue = new LinkedBlockingQueue<>();
		issRawcomProducer = new IssRawcomProducer(inputPaths, queue);
		issRawcomConsumer = new IssRawcomConsumer(env, queue);
	}

	public IssRawcomLoader(String env, String[] inputPaths, String startTime, String endTime) {
		queue = new LinkedBlockingQueue<>();
		issRawcomProducer = new IssRawcomProducer(inputPaths, startTime, endTime, queue);
		issRawcomConsumer = new IssRawcomConsumer(env, queue);
	}

	public void start() {
		ExecutorService executorServiceMain = Executors.newSingleThreadScheduledExecutor();
		
		executorServiceMain.execute(() -> {
			ExecutorService executorProducer = Executors
					.newSingleThreadExecutor(r -> new Thread(r, "Iss Rawcom Producer"));
			log.debug("Executor Producer created!");

			ExecutorService executorConsumer = Executors
					.newSingleThreadExecutor(r -> new Thread(r, "Iss Rawcom Consumer"));
			log.debug("Executor Consumer created!");

			executorProducer.submit(issRawcomProducer);
			log.trace("Se envio tarea acqRawcomProducer");
			executorConsumer.submit(issRawcomConsumer::execute);
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
		issRawcomProducer.addListener(listener);
		issRawcomConsumer.addListener(listener);
	}
}
