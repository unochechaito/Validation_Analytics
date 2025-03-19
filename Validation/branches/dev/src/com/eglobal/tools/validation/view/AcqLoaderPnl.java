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

import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.files.AcqRawcomLoader;
import com.eglobal.tools.validation.statistics.FileLocationState;

public class AcqLoaderPnl extends BaseLoaderPanel {

	private static final long serialVersionUID = 8850669823365220366L;

	public AcqLoaderPnl(String env, FileLocationState fileLocationState) {
        super(env, fileLocationState, true);
    }

    @Override
    protected String getFileLabelText() {
        return "     Archivo de entrada";
    }

    @Override
    protected String getInitialText() {
        return "Seleccione los archivos Rawcom adquirentes a leer";
    }

    @Override
    protected boolean isValidFile(File file) {
        return true;
    }

    @Override
    protected void startLoading(String[] inputFiles) {
        AcqRawcomLoader loader = new AcqRawcomLoader(env, inputFiles, startTime, endTime);
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (firstLoadCb.isSelected()) {
                    publish("*********Generando tablas nuevas*********\n");
                    DBManager.getInstance().setUpDB();
                }
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
