/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.analytics.category;

import com.eglobal.tools.analytics.category.interfaces.TpvBbvaLoader;
import com.eglobal.tools.parser.pojo.Message;

/**
 * @author egldt1029
 */
public class TpvBbvaCategorizer implements IMessageCategorizer {
	private String[] defaultKey;

	public TpvBbvaCategorizer() {
		this(new String[] {"MTI", "3.1", "22.1", "63.EW", "70"});
	}

	public TpvBbvaCategorizer(String... key) {
		this.defaultKey = key;
	}

	@Override
	public String categorize(Message message, String... key) {
		if (key != null && key.length != 0) {
			return categorize(message, TpvBbvaLoader.getInstance(), key);
		}
		return categorize(message, TpvBbvaLoader.getInstance(), defaultKey);
	}

	@java.lang.SuppressWarnings("all")
	public String[] getDefaultKey() {
		return this.defaultKey;
	}
}
