/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.analytics.category.interfaces;

import com.eglobal.tools.analytics.category.interfaces.pojo.FieldValue;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author egldt1029
 */
public abstract class AInterfaceLoader {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AInterfaceLoader.class);
    protected Map<String, FieldValue> fields = new HashMap<>();

    protected void init(String resource) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource)) {
            java.util.Optional<java.io.InputStream> optInput = Optional.ofNullable(is);
            if (!optInput.isPresent()) {
                log.error("Recurso no encontrado: {}", resource);
                return;
            }
            try (InputStreamReader reader = new InputStreamReader(is)) {
                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
                Gson gson = new Gson();
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    FieldValue fieldValue = gson.fromJson(jsonObject.toString(), FieldValue.class);
                    fields.put(fieldValue.getIndex(), fieldValue);
                }
            }
        } catch (IOException | ParseException exception) {
            log.error("Error al leer el recurso {}", resource, exception);
        }
    }

    @java.lang.SuppressWarnings("all")
    public Map<String, FieldValue> getFields() {
        return this.fields;
    }
}
