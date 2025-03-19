package com.eglobal.tools.validation;


import com.eglobal.tools.validation.statistics.FileLocationState;


import java.io.File;


public class DescLoaderPnl extends AbstractDescLoaderPnl {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public DescLoaderPnl(String env, FileLocationState fileLocationState) {
		super(env, fileLocationState);
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
		return true;
	}
}
