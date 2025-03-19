/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.analytics.category;

/**
 * @author egldt1029
 */
public class MessageCategorizerFactory {
	private MessageCategorizerFactory() {
		
	}
	
	public static IMessageCategorizer getCategorizer(String type) {
		if("ISO_POS".equalsIgnoreCase(type) || "ISO_ATM".equalsIgnoreCase(type)) {
			return new ISOCategorizer();
		} else if("Mastercard".equalsIgnoreCase(type)) {
			return new MastercardCategorizer();
		} else if("Visa".equalsIgnoreCase(type)) {
			return new VisaCategorizer();
		} else if("TPV_BBVA".equalsIgnoreCase(type)) {
			return new TpvBbvaCategorizer();
		} else if("JCB".equalsIgnoreCase(type)) {
			return new JCBCategorizer();
		} 
		return null;
	}
	
	public static IMessageCategorizer getCategorizer(String type, String[] key) {
		if("ISO_POS".equalsIgnoreCase(type) || "ISO_ATM".equalsIgnoreCase(type)) {
			return new ISOCategorizer(key);
		} else if("Mastercard".equalsIgnoreCase(type)) {
			return new MastercardCategorizer(key);
		} else if("Visa".equalsIgnoreCase(type)) {
			return new VisaCategorizer(key);
		} else if("TPV_BBVA".equalsIgnoreCase(type)) {
			return new TpvBbvaCategorizer(key);
		}else if("JCB".equalsIgnoreCase(type)) {
			return new JCBCategorizer(key);
		} 
		return null;
	}
}
