/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.analytics.category;

import com.eglobal.tools.analytics.category.interfaces.DiscoverLoader;
import com.eglobal.tools.parser.pojo.Message;

/**
 * @author egldt1029
 */
public class DiscoverCategorizer implements IMessageCategorizer {
	private String[] defaultKey;

	public DiscoverCategorizer() {
		this(new String[] {"MTI", "3.1", "22.1"});
	}

	public DiscoverCategorizer(String... key) {
		this.defaultKey = key;
	}

	@Override
	public String categorize(Message message, String... key) {
		if (key != null && key.length != 0) {
			return categorize(message, DiscoverLoader.getInstance(), key);
		}
		return categorize(message, DiscoverLoader.getInstance(), defaultKey);
	}

	@java.lang.SuppressWarnings("all")
	public String[] getDefaultKey() {
		return this.defaultKey;
	}
}
