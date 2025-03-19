/*
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
*/
package com.eglobal.tools.validation.view;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import com.eglobal.tools.validation.files.IssRawcomLoader;
import com.eglobal.tools.validation.statistics.FileLocationState;

public class IssLoaderPnl extends BaseLoaderPanel {

	private static final long serialVersionUID = 6875678162771838845L;

	public IssLoaderPnl(String env, FileLocationState fileLocationState) {
        super(env, fileLocationState, false);
    }

    @Override protected String getFileLabelText() {
		return "     Archivo de entrada";
	}


	@Override protected String getInitialText() {
		return "Seleccione los archivos Rawcom emisor a leer";
	}

    @Override protected boolean isValidFile(File file) {
        return true;
    }

    @Override
    protected void startLoading(String[] inputFiles) {
        IssRawcomLoader loader = new IssRawcomLoader(env, inputFiles, startTime, endTime);
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                loader.addListener(new ProgressBarRepainter());
                loader.start();
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(outputTextArea::append);
            }
        };
        worker.execute();
    }
}
