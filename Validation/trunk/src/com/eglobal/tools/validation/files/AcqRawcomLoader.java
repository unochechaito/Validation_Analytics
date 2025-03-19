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

public class AcqRawcomLoader {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AcqRawcomLoader.class);
	private AcqRawcomConsumer acqRawcomConsumer;
	private AcqRawcomProducer acqRawcomProducer;
	private SynchronousQueue<RawcomLine> queue;
	private String env;

	public AcqRawcomLoader(String env, String[] inputPaths, String startTime, String endTime) {
		this.env = env;
		queue = new SynchronousQueue<>();
		acqRawcomProducer = new AcqRawcomProducer(inputPaths, startTime, endTime, queue);
		acqRawcomConsumer = new AcqRawcomConsumer(env, queue);
	}

	public void start() {
		ExecutorService executorProducer = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Acq Rawcom Producer");
			}
		});
		log.debug("Executor Producer created!");
		ExecutorService executorConsumer = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Acq Rawcom Consumer");
			}
		});
		log.debug("Executor Consumer created!");
		executorProducer.submit(() -> acqRawcomProducer.run());
		executorConsumer.submit(() -> acqRawcomConsumer.execute());
		executorProducer.shutdown();
		executorConsumer.shutdown();
		;
	}

	public void addListener(PropertyChangeListener listener) {
		acqRawcomProducer.addListener(listener);
		acqRawcomConsumer.addListener(listener);
	}
}
