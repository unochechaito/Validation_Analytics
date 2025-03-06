package com.eglobal.tools.analytics.category.interfaces;

/**
 * @author egldt1029
 * */
public class MastercardLoader extends AInterfaceLoader{
	private static MastercardLoader _INSTANCE;
	
	private MastercardLoader() {
		init("resources/interfaces/Mastercard.json");
	}
	
	public static MastercardLoader getInstance() {
		if(_INSTANCE == null) {
			_INSTANCE = new MastercardLoader();
		}
		return _INSTANCE;
	}
}
