/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.analytics.category.interfaces.pojo;

import java.util.Map;

/**
 * @author egldt1029
 */
public class FieldValue {
	private String name;
	private String index;
	private Map<String, String> values;
	private boolean onlyPresent;

	@java.lang.SuppressWarnings("all")
	public String getName() {
		return this.name;
	}

	@java.lang.SuppressWarnings("all")
	public String getIndex() {
		return this.index;
	}

	@java.lang.SuppressWarnings("all")
	public Map<String, String> getValues() {
		return this.values;
	}

	@java.lang.SuppressWarnings("all")
	public boolean isOnlyPresent() {
		return this.onlyPresent;
	}

	@java.lang.SuppressWarnings("all")
	public void setName(final String name) {
		this.name = name;
	}

	@java.lang.SuppressWarnings("all")
	public void setIndex(final String index) {
		this.index = index;
	}

	@java.lang.SuppressWarnings("all")
	public void setValues(final Map<String, String> values) {
		this.values = values;
	}

	@java.lang.SuppressWarnings("all")
	public void setOnlyPresent(final boolean onlyPresent) {
		this.onlyPresent = onlyPresent;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof FieldValue)) return false;
		final FieldValue other = (FieldValue) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		if (this.isOnlyPresent() != other.isOnlyPresent()) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$index = this.getIndex();
		final java.lang.Object other$index = other.getIndex();
		if (this$index == null ? other$index != null : !this$index.equals(other$index)) return false;
		final java.lang.Object this$values = this.getValues();
		final java.lang.Object other$values = other.getValues();
		if (this$values == null ? other$values != null : !this$values.equals(other$values)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof FieldValue;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + (this.isOnlyPresent() ? 79 : 97);
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $index = this.getIndex();
		result = result * PRIME + ($index == null ? 43 : $index.hashCode());
		final java.lang.Object $values = this.getValues();
		result = result * PRIME + ($values == null ? 43 : $values.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "FieldValue(name=" + this.getName() + ", index=" + this.getIndex() + ", values=" + this.getValues() + ", onlyPresent=" + this.isOnlyPresent() + ")";
	}

	@java.lang.SuppressWarnings("all")
	public FieldValue() {
	}
}
