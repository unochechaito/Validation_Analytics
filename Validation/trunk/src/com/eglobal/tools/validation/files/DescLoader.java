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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;

public class DescLoader {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DescLoader.class);
	private DescConsumer descConsumer;
	private DescProducer descProducer;
	private SynchronousQueue<String> queue;

	public DescLoader(String env, String[] inputPaths) {
		queue = new SynchronousQueue<>();
		descProducer = new DescProducer(env, inputPaths, queue);
		descConsumer = new DescConsumer(env, queue);
	}

	public DescLoader(String env, String[] inputPaths, String startTime, String endTime) {
		queue = new SynchronousQueue<>();
		descProducer = new DescProducer(env, inputPaths, startTime, endTime, queue);
		descConsumer = new DescConsumer(env, queue);
	}

	public void start() {
		ExecutorService executorProducer = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Desc Producer");
			}
		});
		log.debug("Executor Producer created!");
		ExecutorService executorConsumer = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Desc Consumer");
			}
		});
		log.debug("Executor Consumer created!");
		executorProducer.submit(() -> descProducer.execute());
		executorConsumer.submit(() -> descConsumer.execute());
		executorProducer.shutdown();
		executorConsumer.shutdown();
		;
	}

	public void addListener(PropertyChangeListener listener) {
		descProducer.addListener(listener);
		descConsumer.addListener(listener);
	}
}
