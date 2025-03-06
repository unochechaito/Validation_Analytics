package com.eglobal.tools.analytics.category.interfaces;

/**
 * @author egldt1029
 * */
public class TpvBbvaLoader extends AInterfaceLoader{
	private static TpvBbvaLoader _INSTANCE;

	private TpvBbvaLoader() {
		init("resources/interfaces/TPVBBVA.json");
	}
	
	public static TpvBbvaLoader getInstance() {
		if(_INSTANCE == null) {
			_INSTANCE = new TpvBbvaLoader();
		}
		return _INSTANCE;
	}
	
}
