/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

import com.eglobal.tools.parser.pojo.FieldFormat;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author egldt1029
 */
public abstract class FieldLoader {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FieldLoader.class);
	protected List<String> orderedFields;
	/**
	 * format by name
	 */
	protected Map<String, Integer> fieldFormats;
	/**
	 * format by index
	 */
	protected Map<Integer, FieldFormat> _fieldFormats;

	protected void init(String resource) {
		orderedFields = new ArrayList<>();
		fieldFormats = new HashMap<>();
		_fieldFormats = new HashMap<>();
		try (InputStream resourcStream = getClass().getClassLoader().getResourceAsStream(resource)) {
			Optional<InputStream> optionalResource = Optional.ofNullable(resourcStream);
			if (!optionalResource.isPresent()) {
				log.error("No se encontro el recurso " + resource);
				return;
			}
			try (InputStreamReader reader = new InputStreamReader(optionalResource.get())) {
				JSONParser jsonParser = new JSONParser();
				JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
				Gson gson = new Gson();
				for (Object object : jsonArray) {
					JSONObject jsonObject = (JSONObject) object;
					FieldFormat parserFieldFormats = gson.fromJson(jsonObject.toString(), FieldFormat.class);
					orderedFields.add(parserFieldFormats.getName());
					fieldFormats.put(parserFieldFormats.getName(), parserFieldFormats.getIndex());
					_fieldFormats.put(parserFieldFormats.getIndex(), parserFieldFormats);
				}
			}
		} catch (FileNotFoundException e) {
			log.error("Resource is missing " + resource, e);
		} catch (IOException e) {
			log.error("Can\'t read resource " + resource, e);
		} catch (ParseException e) {
			log.error("Can\'t parse " + resource, e);
		}
	}
}
