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
public class DiscoverLoader extends AInterfaceLoader{
	private static DiscoverLoader _INSTANCE;
	
	private DiscoverLoader() {
		init("resources/interfaces/Discover.json");
	}
	
	public static DiscoverLoader getInstance() {
		if(_INSTANCE == null) {
			_INSTANCE = new DiscoverLoader();
		}
		return _INSTANCE;
	}
}
