/*
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
*/
package com.eglobal.tools.validation.view;

import java.io.File;

import javax.swing.SwingWorker;

import com.eglobal.tools.validation.files.DescLoader;
import com.eglobal.tools.validation.statistics.FileLocationState;

public class DescLoaderPnl extends BaseLoaderPanel {
    private static final long serialVersionUID = 1L;

    public DescLoaderPnl(String env, FileLocationState fileLocationState) {
        super(env, fileLocationState, false);
    }

    @Override
    protected String getFileLabelText() {
        return "     Archivo de entrada";
    }

    @Override
    protected String getInitialText() {
        return "Seleccione los Desc a leer";
    }

    @Override
    protected boolean isValidFile(File file) {
        return true; // Implementar lógica de validación si es necesario
    }

    @Override
    protected void startLoading(String[] inputFiles) {
    	DescLoader loader = new DescLoader(env, inputFiles, startTime, endTime);

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                loader.addListener(new ProgressBarRepainter());
                loader.start();
                return null;
            }
        };
        worker.execute();
    }
}
