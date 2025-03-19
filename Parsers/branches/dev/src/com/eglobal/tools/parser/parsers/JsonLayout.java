/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author jcb6937
 */
public abstract class JsonLayout {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JsonLayout.class);
	protected Map<String, Map<String, String>> layout;

	protected void init(String rc) {
		try (InputStream resource = getClass().getClassLoader().getResourceAsStream(rc)) {
			Optional<InputStream> optionalResource = Optional.ofNullable(resource);
			if (optionalResource.isPresent()) {
				try (InputStreamReader reader = new InputStreamReader(optionalResource.get())) {
					Gson gson = new Gson();
					Type type = new TypeToken<Map<String, Map<String, String>>>() {
					}.getType();
					layout = gson.fromJson(reader, type);
				}
			} else {
				log.debug("Recurso no encontrado.");
			}
		} catch (IOException e) {
			log.error(String.format("Error al leer el recurso: %s", rc), e);
		}
	}
}
