/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

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
@Table(name = "desc_linea")
public class EDescLinea {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private String bin;
	@Column(nullable = false)
	private String acquirerDispatcher;
	@Column(nullable = false)
	private String issuerDispatcher;
	@Column(nullable = false)
	private String timestamp;
	@Column(name = "key", nullable = false)
	private String key;
	@Column(name = "key_acq", nullable = false)
	private String keyAcq;
	@Column(name = "key_iss", nullable = false)
	private String keyIss;
	@Column(nullable = false)
	private String data;

	@java.lang.SuppressWarnings("all")
	public EDescLinea() {
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
	public String getData() {
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
	public void setData(final String data) {
		this.data = data;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof EDescLinea)) return false;
		final EDescLinea other = (EDescLinea) o;
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
		return other instanceof EDescLinea;
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
		return "EDescLinea(id=" + this.getId() + ", bin=" + this.getBin() + ", acquirerDispatcher=" + this.getAcquirerDispatcher() + ", issuerDispatcher=" + this.getIssuerDispatcher() + ", timestamp=" + this.getTimestamp() + ", key=" + this.getKey() + ", keyAcq=" + this.getKeyAcq() + ", keyIss=" + this.getKeyIss() + ", data=" + this.getData() + ")";
	}
}
