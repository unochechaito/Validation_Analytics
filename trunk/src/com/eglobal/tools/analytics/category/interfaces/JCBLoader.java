/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
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
