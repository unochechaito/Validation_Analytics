/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation.pojo;

import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.validation.db.model.EDescLinea;
import com.google.gson.Gson;

/**
 * @author egldt1029
 */
public class DescLinea {
	private Long id;
	private String bin;
	private String acquirerDispatcher;
	private String issuerDispatcher;
	private String timestamp;
	private String key;
	private String keyAcq;
	private String keyIss;
	private Message data;

	public static EDescLinea toEntity(DescLinea descLinea) {
		if (!(descLinea instanceof DescLinea)) {
			return null;
		}
		Gson gson = new Gson();
		EDescLinea entity = new EDescLinea();
		entity.setId(descLinea.getId());
		entity.setBin(descLinea.getBin());
		entity.setAcquirerDispatcher(descLinea.getAcquirerDispatcher());
		entity.setIssuerDispatcher(descLinea.getIssuerDispatcher());
		entity.setTimestamp(descLinea.getTimestamp());
		entity.setKey(descLinea.getKey());
		entity.setKeyAcq(descLinea.getKeyAcq());
		entity.setKeyIss(descLinea.getKeyIss());
		entity.setData(gson.toJson(descLinea.getData()));
		return entity;
	}

	public static DescLinea fromEntity(EDescLinea entity) {
		if (!(entity instanceof EDescLinea)) {
			return null;
		}
		Gson gson = new Gson();
		DescLinea descLinea = new DescLinea();
		descLinea.setId(entity.getId());
		descLinea.setBin(entity.getBin());
		descLinea.setAcquirerDispatcher(entity.getAcquirerDispatcher());
		descLinea.setIssuerDispatcher(entity.getIssuerDispatcher());
		descLinea.setTimestamp(entity.getTimestamp());
		descLinea.setKey(entity.getKey());
		descLinea.setKeyAcq(entity.getKeyAcq());
		descLinea.setKeyIss(entity.getKeyIss());
		descLinea.setData(gson.fromJson(entity.getData(), Message.class));
		return descLinea;
	}

	@java.lang.SuppressWarnings("all")
	public DescLinea() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getId() {
		return this.id;
	}

	@java.lang.SuppressWarnings("all")
	public String getBin() {
		return this.bin;
	}

	@java.lang.SuppressWarnings("all")
	public String getAcquirerDispatcher() {
		return this.acquirerDispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public String getIssuerDispatcher() {
		return this.issuerDispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public String getTimestamp() {
		return this.timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public String getKey() {
		return this.key;
	}

	@java.lang.SuppressWarnings("all")
	public String getKeyAcq() {
		return this.keyAcq;
	}

	@java.lang.SuppressWarnings("all")
	public String getKeyIss() {
		return this.keyIss;
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
	public void setBin(final String bin) {
		this.bin = bin;
	}

	@java.lang.SuppressWarnings("all")
	public void setAcquirerDispatcher(final String acquirerDispatcher) {
		this.acquirerDispatcher = acquirerDispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public void setIssuerDispatcher(final String issuerDispatcher) {
		this.issuerDispatcher = issuerDispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public void setKey(final String key) {
		this.key = key;
	}

	@java.lang.SuppressWarnings("all")
	public void setKeyAcq(final String keyAcq) {
		this.keyAcq = keyAcq;
	}

	@java.lang.SuppressWarnings("all")
	public void setKeyIss(final String keyIss) {
		this.keyIss = keyIss;
	}

	@java.lang.SuppressWarnings("all")
	public void setData(final Message data) {
		this.data = data;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof DescLinea)) return false;
		final DescLinea other = (DescLinea) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$bin = this.getBin();
		final java.lang.Object other$bin = other.getBin();
		if (this$bin == null ? other$bin != null : !this$bin.equals(other$bin)) return false;
		final java.lang.Object this$acquirerDispatcher = this.getAcquirerDispatcher();
		final java.lang.Object other$acquirerDispatcher = other.getAcquirerDispatcher();
		if (this$acquirerDispatcher == null ? other$acquirerDispatcher != null : !this$acquirerDispatcher.equals(other$acquirerDispatcher)) return false;
		final java.lang.Object this$issuerDispatcher = this.getIssuerDispatcher();
		final java.lang.Object other$issuerDispatcher = other.getIssuerDispatcher();
		if (this$issuerDispatcher == null ? other$issuerDispatcher != null : !this$issuerDispatcher.equals(other$issuerDispatcher)) return false;
		final java.lang.Object this$timestamp = this.getTimestamp();
		final java.lang.Object other$timestamp = other.getTimestamp();
		if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
		final java.lang.Object this$key = this.getKey();
		final java.lang.Object other$key = other.getKey();
		if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
		final java.lang.Object this$keyAcq = this.getKeyAcq();
		final java.lang.Object other$keyAcq = other.getKeyAcq();
		if (this$keyAcq == null ? other$keyAcq != null : !this$keyAcq.equals(other$keyAcq)) return false;
		final java.lang.Object this$keyIss = this.getKeyIss();
		final java.lang.Object other$keyIss = other.getKeyIss();
		if (this$keyIss == null ? other$keyIss != null : !this$keyIss.equals(other$keyIss)) return false;
		final java.lang.Object this$data = this.getData();
		final java.lang.Object other$data = other.getData();
		if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof DescLinea;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final java.lang.Object $bin = this.getBin();
		result = result * PRIME + ($bin == null ? 43 : $bin.hashCode());
		final java.lang.Object $acquirerDispatcher = this.getAcquirerDispatcher();
		result = result * PRIME + ($acquirerDispatcher == null ? 43 : $acquirerDispatcher.hashCode());
		final java.lang.Object $issuerDispatcher = this.getIssuerDispatcher();
		result = result * PRIME + ($issuerDispatcher == null ? 43 : $issuerDispatcher.hashCode());
		final java.lang.Object $timestamp = this.getTimestamp();
		result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
		final java.lang.Object $key = this.getKey();
		result = result * PRIME + ($key == null ? 43 : $key.hashCode());
		final java.lang.Object $keyAcq = this.getKeyAcq();
		result = result * PRIME + ($keyAcq == null ? 43 : $keyAcq.hashCode());
		final java.lang.Object $keyIss = this.getKeyIss();
		result = result * PRIME + ($keyIss == null ? 43 : $keyIss.hashCode());
		final java.lang.Object $data = this.getData();
		result = result * PRIME + ($data == null ? 43 : $data.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "DescLinea(id=" + this.getId() + ", bin=" + this.getBin() + ", acquirerDispatcher=" + this.getAcquirerDispatcher() + ", issuerDispatcher=" + this.getIssuerDispatcher() + ", timestamp=" + this.getTimestamp() + ", key=" + this.getKey() + ", keyAcq=" + this.getKeyAcq() + ", keyIss=" + this.getKeyIss() + ", data=" + this.getData() + ")";
	}
}
