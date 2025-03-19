/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.files;

import java.io.File;
import java.io.IOException;

public interface IFileProccesor<F extends File> {
	void setFile(F File);
	void process() throws InterruptedException, IOException;
	int loadingPercent();
	long bytesRead();
	long linesRead();
}
