/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.pojo;

import java.util.List;

public class FieldFormat {
	private Integer index;
	private String name;
	private String lengthIndicator;
	private Integer length;
	private String format;
	private List<FieldFormat> subfields;
	private String subfieldsName;
	private Boolean divideLengthForBCD;

	@java.lang.SuppressWarnings("all")
	public Integer getIndex() {
		return this.index;
	}

	@java.lang.SuppressWarnings("all")
	public String getName() {
		return this.name;
	}

	@java.lang.SuppressWarnings("all")
	public String getLengthIndicator() {
		return this.lengthIndicator;
	}

	@java.lang.SuppressWarnings("all")
	public Integer getLength() {
		return this.length;
	}

	@java.lang.SuppressWarnings("all")
	public String getFormat() {
		return this.format;
	}

	@java.lang.SuppressWarnings("all")
	public List<FieldFormat> getSubfields() {
		return this.subfields;
	}

	@java.lang.SuppressWarnings("all")
	public String getSubfieldsName() {
		return this.subfieldsName;
	}

	@java.lang.SuppressWarnings("all")
	public Boolean getDivideLengthForBCD() {
		return this.divideLengthForBCD;
	}

	@java.lang.SuppressWarnings("all")
	public void setIndex(final Integer index) {
		this.index = index;
	}

	@java.lang.SuppressWarnings("all")
	public void setName(final String name) {
		this.name = name;
	}

	@java.lang.SuppressWarnings("all")
	public void setLengthIndicator(final String lengthIndicator) {
		this.lengthIndicator = lengthIndicator;
	}

	@java.lang.SuppressWarnings("all")
	public void setLength(final Integer length) {
		this.length = length;
	}

	@java.lang.SuppressWarnings("all")
	public void setFormat(final String format) {
		this.format = format;
	}

	@java.lang.SuppressWarnings("all")
	public void setSubfields(final List<FieldFormat> subfields) {
		this.subfields = subfields;
	}

	@java.lang.SuppressWarnings("all")
	public void setSubfieldsName(final String subfieldsName) {
		this.subfieldsName = subfieldsName;
	}

	@java.lang.SuppressWarnings("all")
	public void setDivideLengthForBCD(final Boolean divideLengthForBCD) {
		this.divideLengthForBCD = divideLengthForBCD;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof FieldFormat)) return false;
		final FieldFormat other = (FieldFormat) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$index = this.getIndex();
		final java.lang.Object other$index = other.getIndex();
		if (this$index == null ? other$index != null : !this$index.equals(other$index)) return false;
		final java.lang.Object this$length = this.getLength();
		final java.lang.Object other$length = other.getLength();
		if (this$length == null ? other$length != null : !this$length.equals(other$length)) return false;
		final java.lang.Object this$divideLengthForBCD = this.getDivideLengthForBCD();
		final java.lang.Object other$divideLengthForBCD = other.getDivideLengthForBCD();
		if (this$divideLengthForBCD == null ? other$divideLengthForBCD != null : !this$divideLengthForBCD.equals(other$divideLengthForBCD)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$lengthIndicator = this.getLengthIndicator();
		final java.lang.Object other$lengthIndicator = other.getLengthIndicator();
		if (this$lengthIndicator == null ? other$lengthIndicator != null : !this$lengthIndicator.equals(other$lengthIndicator)) return false;
		final java.lang.Object this$format = this.getFormat();
		final java.lang.Object other$format = other.getFormat();
		if (this$format == null ? other$format != null : !this$format.equals(other$format)) return false;
		final java.lang.Object this$subfields = this.getSubfields();
		final java.lang.Object other$subfields = other.getSubfields();
		if (this$subfields == null ? other$subfields != null : !this$subfields.equals(other$subfields)) return false;
		final java.lang.Object this$subfieldsName = this.getSubfieldsName();
		final java.lang.Object other$subfieldsName = other.getSubfieldsName();
		if (this$subfieldsName == null ? other$subfieldsName != null : !this$subfieldsName.equals(other$subfieldsName)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof FieldFormat;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $index = this.getIndex();
		result = result * PRIME + ($index == null ? 43 : $index.hashCode());
		final java.lang.Object $length = this.getLength();
		result = result * PRIME + ($length == null ? 43 : $length.hashCode());
		final java.lang.Object $divideLengthForBCD = this.getDivideLengthForBCD();
		result = result * PRIME + ($divideLengthForBCD == null ? 43 : $divideLengthForBCD.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $lengthIndicator = this.getLengthIndicator();
		result = result * PRIME + ($lengthIndicator == null ? 43 : $lengthIndicator.hashCode());
		final java.lang.Object $format = this.getFormat();
		result = result * PRIME + ($format == null ? 43 : $format.hashCode());
		final java.lang.Object $subfields = this.getSubfields();
		result = result * PRIME + ($subfields == null ? 43 : $subfields.hashCode());
		final java.lang.Object $subfieldsName = this.getSubfieldsName();
		result = result * PRIME + ($subfieldsName == null ? 43 : $subfieldsName.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "FieldFormat(index=" + this.getIndex() + ", name=" + this.getName() + ", lengthIndicator=" + this.getLengthIndicator() + ", length=" + this.getLength() + ", format=" + this.getFormat() + ", subfields=" + this.getSubfields() + ", subfieldsName=" + this.getSubfieldsName() + ", divideLengthForBCD=" + this.getDivideLengthForBCD() + ")";
	}

	@java.lang.SuppressWarnings("all")
	public FieldFormat() {
	}
}
