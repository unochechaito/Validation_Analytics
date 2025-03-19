/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation.pojo;

import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.validation.db.model.EDescLineaAdd;
import com.google.gson.Gson;

/**
 * @author egldt1029
 */
public class DescLineaAdd {
	private Long id;
	private String descKey;
	private Message data;

	public static EDescLineaAdd toEntity(DescLineaAdd descLineaAdd) {
		if (!(descLineaAdd instanceof DescLineaAdd)) {
			return null;
		}
		Gson gson = new Gson();
		EDescLineaAdd entity = new EDescLineaAdd();
		entity.setId(descLineaAdd.getId());
		entity.setDescKey(descLineaAdd.getDescKey());
		entity.setData(gson.toJson(descLineaAdd.getData()));
		return entity;
	}

	public static DescLineaAdd fromEntity(EDescLineaAdd entity) {
		if (!(entity instanceof EDescLineaAdd)) {
			return null;
		}
		Gson gson = new Gson();
		DescLineaAdd descLineaAdd = new DescLineaAdd();
		descLineaAdd.setId(entity.getId());
		descLineaAdd.setDescKey(entity.getDescKey());
		descLineaAdd.setData(gson.fromJson(entity.getData(), Message.class));
		return descLineaAdd;
	}

	@java.lang.SuppressWarnings("all")
	public DescLineaAdd() {
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
	public Message getData() {
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
	public void setData(final Message data) {
		this.data = data;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof DescLineaAdd)) return false;
		final DescLineaAdd other = (DescLineaAdd) o;
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
		return other instanceof DescLineaAdd;
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
		return "DescLineaAdd(id=" + this.getId() + ", descKey=" + this.getDescKey() + ", data=" + this.getData() + ")";
	}
}
