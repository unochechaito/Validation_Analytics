/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.pojo;

//import lombok.*;
import java.util.HashMap;

public class FieldExt {
	private String rawData;
	private HashMap<String, Object> subfields = new HashMap<>();

	public void setField(String key, Object value) {
		subfields.put(key, value);
	}

	public Object getField(String key) {
		return subfields.get(key);
	}

	public void removeField(String key) {
		subfields.remove(key);
	}

	@java.lang.SuppressWarnings("all")
	public String getRawData() {
		return this.rawData;
	}

	@java.lang.SuppressWarnings("all")
	public HashMap<String, Object> getSubfields() {
		return this.subfields;
	}

	@java.lang.SuppressWarnings("all")
	public void setRawData(final String rawData) {
		this.rawData = rawData;
	}

	@java.lang.SuppressWarnings("all")
	public void setSubfields(final HashMap<String, Object> subfields) {
		this.subfields = subfields;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof FieldExt)) return false;
		final FieldExt other = (FieldExt) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$rawData = this.getRawData();
		final java.lang.Object other$rawData = other.getRawData();
		if (this$rawData == null ? other$rawData != null : !this$rawData.equals(other$rawData)) return false;
		final java.lang.Object this$subfields = this.getSubfields();
		final java.lang.Object other$subfields = other.getSubfields();
		if (this$subfields == null ? other$subfields != null : !this$subfields.equals(other$subfields)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof FieldExt;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $rawData = this.getRawData();
		result = result * PRIME + ($rawData == null ? 43 : $rawData.hashCode());
		final java.lang.Object $subfields = this.getSubfields();
		result = result * PRIME + ($subfields == null ? 43 : $subfields.hashCode());
		return result;
	}

	@java.lang.SuppressWarnings("all")
	public FieldExt() {
	}

	@java.lang.SuppressWarnings("all")
	public FieldExt(final String rawData, final HashMap<String, Object> subfields) {
		this.rawData = rawData;
		this.subfields = subfields;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "FieldExt(rawData=" + this.getRawData() + ", subfields=" + this.getSubfields() + ")";
	}
}
