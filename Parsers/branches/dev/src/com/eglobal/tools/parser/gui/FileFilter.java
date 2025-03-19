/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.gui;

import java.io.File;

public class FileFilter extends javax.swing.filechooser.FileFilter {
	@java.lang.SuppressWarnings("all")
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(FileFilter.class);

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String ext = getExtension(f);
		if (ext != null && ext.equals("txt")) {
			return true;
		}
		if (ext != null && ext.equals("desa")) {
			return true;
		}
		if (ext != null && isNumeric(ext)) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Transaction File";
	}

	public String getExtension(File file) {
		String ext = null;
		String name = file.getName();
		int i = name.lastIndexOf('.');
		if (i > 0 && i < name.length() - 1) {
			ext = name.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public boolean isNumeric(String str) {
		if (str != null) {
			return false;
		}
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			log.error("Error al pasear string a number: " + str, e);
			return false;
		}
		return true;
	}
}
