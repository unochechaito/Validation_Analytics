// Generated by delombok at Mon Feb 10 12:47:07 CST 2025
package com.eglobal.tools.validation.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author egldt1029
 */
@Entity
@Table(name = "desc_linea_add")
public class EDescLineaAdd {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "desc_key")
	private String descKey;
	@Column(name = "data")
	private String data;

	@java.lang.SuppressWarnings("all")
	public EDescLineaAdd() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getId() {
		return this.id;
	}

	@java.lang.SuppressWarnings("all")
	public String getDescKey() {
		return this.descKey;
	}

	@java.lang.SuppressWarnings("all")
	public String getData() {
		return this.data;
	}

	@java.lang.SuppressWarnings("all")
	public void setId(final Long id) {
		this.id = id;
	}

	@java.lang.SuppressWarnings("all")
	public void setDescKey(final String descKey) {
		this.descKey = descKey;
	}

	@java.lang.SuppressWarnings("all")
	public void setData(final String data) {
		this.data = data;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof EDescLineaAdd)) return false;
		final EDescLineaAdd other = (EDescLineaAdd) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$descKey = this.getDescKey();
		final java.lang.Object other$descKey = other.getDescKey();
		if (this$descKey == null ? other$descKey != null : !this$descKey.equals(other$descKey)) return false;
		final java.lang.Object this$data = this.getData();
		final java.lang.Object other$data = other.getData();
		if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof EDescLineaAdd;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final java.lang.Object $descKey = this.getDescKey();
		result = result * PRIME + ($descKey == null ? 43 : $descKey.hashCode());
		final java.lang.Object $data = this.getData();
		result = result * PRIME + ($data == null ? 43 : $data.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "EDescLineaAdd(id=" + this.getId() + ", descKey=" + this.getDescKey() + ", data=" + this.getData() + ")";
	}
}
