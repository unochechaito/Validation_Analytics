package com.eglobal.tools.analytics.category.interfaces;

/**
 * @author egldt1029
 * */
public class ISOLoader extends AInterfaceLoader{
	private static ISOLoader _INSTANCE;
	
	private ISOLoader() {
		init("resources/interfaces/ISO.json");
	}
	
	public static ISOLoader getInstance() {
		if(_INSTANCE == null) {
			_INSTANCE = new ISOLoader();
		}
		return _INSTANCE;
	}
}
