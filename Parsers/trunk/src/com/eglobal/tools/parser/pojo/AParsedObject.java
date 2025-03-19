/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.pojo;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author egldt1029
 */
public abstract class AParsedObject {
	HashMap<String, Object> fields = new LinkedHashMap<>();
	String type;

	public void setField(String key, Object value) {
		fields.put(key, value);
	}

	public Object getField(String key) {
		return fields.get(key);
	}

	public void removeField(String key) {
		fields.remove(key);
	}

	@java.lang.SuppressWarnings("all")
	public AParsedObject() {
	}

	@java.lang.SuppressWarnings("all")
	public AParsedObject(final HashMap<String, Object> fields, final String type) {
		this.fields = fields;
		this.type = type;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "AParsedObject(fields=" + this.getFields() + ", type=" + this.getType() + ")";
	}

	@java.lang.SuppressWarnings("all")
	public HashMap<String, Object> getFields() {
		return this.fields;
	}

	@java.lang.SuppressWarnings("all")
	public String getType() {
		return this.type;
	}

	@java.lang.SuppressWarnings("all")
	public void setType(final String type) {
		this.type = type;
	}
}
