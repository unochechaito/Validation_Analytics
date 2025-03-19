/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation;

public class Photo {
	private String id;
	private String name;
	private String query;
	private String output_file;
	private boolean isComparable;

	@java.lang.SuppressWarnings("all")
	public Photo() {
	}

	@java.lang.SuppressWarnings("all")
	public String getId() {
		return this.id;
	}

	@java.lang.SuppressWarnings("all")
	public String getName() {
		return this.name;
	}

	@java.lang.SuppressWarnings("all")
	public String getQuery() {
		return this.query;
	}

	@java.lang.SuppressWarnings("all")
	public String getOutput_file() {
		return this.output_file;
	}

	@java.lang.SuppressWarnings("all")
	public boolean isComparable() {
		return this.isComparable;
	}

	@java.lang.SuppressWarnings("all")
	public void setId(final String id) {
		this.id = id;
	}

	@java.lang.SuppressWarnings("all")
	public void setName(final String name) {
		this.name = name;
	}

	@java.lang.SuppressWarnings("all")
	public void setQuery(final String query) {
		this.query = query;
	}

	@java.lang.SuppressWarnings("all")
	public void setOutput_file(final String output_file) {
		this.output_file = output_file;
	}

	@java.lang.SuppressWarnings("all")
	public void setComparable(final boolean isComparable) {
		this.isComparable = isComparable;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof Photo)) return false;
		final Photo other = (Photo) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		if (this.isComparable() != other.isComparable()) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$query = this.getQuery();
		final java.lang.Object other$query = other.getQuery();
		if (this$query == null ? other$query != null : !this$query.equals(other$query)) return false;
		final java.lang.Object this$output_file = this.getOutput_file();
		final java.lang.Object other$output_file = other.getOutput_file();
		if (this$output_file == null ? other$output_file != null : !this$output_file.equals(other$output_file)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof Photo;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + (this.isComparable() ? 79 : 97);
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $query = this.getQuery();
		result = result * PRIME + ($query == null ? 43 : $query.hashCode());
		final java.lang.Object $output_file = this.getOutput_file();
		result = result * PRIME + ($output_file == null ? 43 : $output_file.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "Photo(id=" + this.getId() + ", name=" + this.getName() + ", query=" + this.getQuery() + ", output_file=" + this.getOutput_file() + ", isComparable=" + this.isComparable() + ")";
	}
}
