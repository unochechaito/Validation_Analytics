package com.eglobal.tools.analytics.category.interfaces;

/**
 * @author egldt1029
 * */
public class JCBLoader extends AInterfaceLoader{
	private static JCBLoader _INSTANCE;
	
	private JCBLoader() {
		init("resources/interfaces/JCB.json");
	}
	
	public static JCBLoader getInstance() {
		if(_INSTANCE == null) {
			_INSTANCE = new JCBLoader();
		}
		return _INSTANCE;
	}
}
