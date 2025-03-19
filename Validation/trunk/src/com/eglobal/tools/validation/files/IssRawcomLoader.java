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
import com.eglobal.tools.parser.pojo.RawcomLine;

public class IssRawcomLoader {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IssRawcomLoader.class);
	private IssRawcomConsumer issRawcomConsumer;
	private IssRawcomProducer issRawcomProducer;
	private SynchronousQueue<RawcomLine> queue;

	public IssRawcomLoader(String env, String[] inputPaths) {
		queue = new SynchronousQueue<>();
		issRawcomProducer = new IssRawcomProducer(inputPaths, queue);
		issRawcomConsumer = new IssRawcomConsumer(env, queue);
	}

	public IssRawcomLoader(String env, String[] inputPaths, String startTime, String endTime) {
		queue = new SynchronousQueue<>();
		issRawcomProducer = new IssRawcomProducer(inputPaths, startTime, endTime, queue);
		issRawcomConsumer = new IssRawcomConsumer(env, queue);
	}

	public void start() {
		ExecutorService executorProducer = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Iss Rawcom Producer");
			}
		});
		log.debug("Executor Producer created!");
		ExecutorService executorConsumer = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Iss Rawcom Consumer");
			}
		});
		log.debug("Executor Consumer created!");
		executorProducer.submit(() -> issRawcomProducer.run());
		executorConsumer.submit(() -> issRawcomConsumer.execute());
		executorProducer.shutdown();
		executorConsumer.shutdown();
		;
	}

	public void addListener(PropertyChangeListener listener) {
		issRawcomProducer.addListener(listener);
		issRawcomConsumer.addListener(listener);
	}
}
