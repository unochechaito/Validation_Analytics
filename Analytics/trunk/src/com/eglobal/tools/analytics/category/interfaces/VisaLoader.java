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
public class VisaLoader extends AInterfaceLoader{
	private static VisaLoader _INSTANCE;
	
	private VisaLoader() {
		init("interfaces/Visa.json");
	}
	
	public static VisaLoader getInstance() {
		if(_INSTANCE == null) {
			_INSTANCE = new VisaLoader();
		}
		return _INSTANCE;
	}
}
