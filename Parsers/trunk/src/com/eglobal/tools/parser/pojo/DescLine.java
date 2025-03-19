/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.pojo;

/**
 * @author jcb6937
 */
public class DescLine {
	private String rawData;

	public DescLine(String rawData) {
		this.rawData = rawData;
	}

	@java.lang.SuppressWarnings("all")
	public String getRawData() {
		return this.rawData;
	}

	@java.lang.SuppressWarnings("all")
	public void setRawData(final String rawData) {
		this.rawData = rawData;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof DescLine)) return false;
		final DescLine other = (DescLine) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$rawData = this.getRawData();
		final java.lang.Object other$rawData = other.getRawData();
		if (this$rawData == null ? other$rawData != null : !this$rawData.equals(other$rawData)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof DescLine;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $rawData = this.getRawData();
		result = result * PRIME + ($rawData == null ? 43 : $rawData.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "DescLine(rawData=" + this.getRawData() + ")";
	}

	@java.lang.SuppressWarnings("all")
	public DescLine() {
	}
}
