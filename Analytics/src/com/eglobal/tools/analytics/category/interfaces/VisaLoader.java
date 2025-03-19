package com.eglobal.tools.analytics.category.interfaces;

/**
 * @author egldt1029
 * */
public class VisaLoader extends AInterfaceLoader{
	private static VisaLoader _INSTANCE;
	
	private VisaLoader() {
		init("resources/interfaces/Visa.json");
	}
	
	public static VisaLoader getInstance() {
		if(_INSTANCE == null) {
			_INSTANCE = new VisaLoader();
		}
		return _INSTANCE;
	}
}
