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
@Table(name = "iss_view")
public class EIssuerView {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private String bin;
	@Column(nullable = false)
	private String key;
	@Column(nullable = false)
	private String dispatcher;
	@Column(nullable = false)
	private String category;
	@Column(nullable = false)
	private String fingerprint;
	@Column(nullable = false)
	private String timestamp;
	@Column(name = "req_msg", nullable = false)
	private String requirementMessage;
	@Column(name = "res_msg", nullable = false)
	private String responseMessage;

	@java.lang.SuppressWarnings("all")
	public EIssuerView() {
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
	public String getKey() {
		return this.key;
	}

	@java.lang.SuppressWarnings("all")
	public String getDispatcher() {
		return this.dispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public String getCategory() {
		return this.category;
	}

	@java.lang.SuppressWarnings("all")
	public String getFingerprint() {
		return this.fingerprint;
	}

	@java.lang.SuppressWarnings("all")
	public String getTimestamp() {
		return this.timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public String getRequirementMessage() {
		return this.requirementMessage;
	}

	@java.lang.SuppressWarnings("all")
	public String getResponseMessage() {
		return this.responseMessage;
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
	public void setKey(final String key) {
		this.key = key;
	}

	@java.lang.SuppressWarnings("all")
	public void setDispatcher(final String dispatcher) {
		this.dispatcher = dispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public void setCategory(final String category) {
		this.category = category;
	}

	@java.lang.SuppressWarnings("all")
	public void setFingerprint(final String fingerprint) {
		this.fingerprint = fingerprint;
	}

	@java.lang.SuppressWarnings("all")
	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public void setRequirementMessage(final String requirementMessage) {
		this.requirementMessage = requirementMessage;
	}

	@java.lang.SuppressWarnings("all")
	public void setResponseMessage(final String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof EIssuerView)) return false;
		final EIssuerView other = (EIssuerView) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$bin = this.getBin();
		final java.lang.Object other$bin = other.getBin();
		if (this$bin == null ? other$bin != null : !this$bin.equals(other$bin)) return false;
		final java.lang.Object this$key = this.getKey();
		final java.lang.Object other$key = other.getKey();
		if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
		final java.lang.Object this$dispatcher = this.getDispatcher();
		final java.lang.Object other$dispatcher = other.getDispatcher();
		if (this$dispatcher == null ? other$dispatcher != null : !this$dispatcher.equals(other$dispatcher)) return false;
		final java.lang.Object this$category = this.getCategory();
		final java.lang.Object other$category = other.getCategory();
		if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
		final java.lang.Object this$fingerprint = this.getFingerprint();
		final java.lang.Object other$fingerprint = other.getFingerprint();
		if (this$fingerprint == null ? other$fingerprint != null : !this$fingerprint.equals(other$fingerprint)) return false;
		final java.lang.Object this$timestamp = this.getTimestamp();
		final java.lang.Object other$timestamp = other.getTimestamp();
		if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
		final java.lang.Object this$requirementMessage = this.getRequirementMessage();
		final java.lang.Object other$requirementMessage = other.getRequirementMessage();
		if (this$requirementMessage == null ? other$requirementMessage != null : !this$requirementMessage.equals(other$requirementMessage)) return false;
		final java.lang.Object this$responseMessage = this.getResponseMessage();
		final java.lang.Object other$responseMessage = other.getResponseMessage();
		if (this$responseMessage == null ? other$responseMessage != null : !this$responseMessage.equals(other$responseMessage)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof EIssuerView;
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
		final java.lang.Object $key = this.getKey();
		result = result * PRIME + ($key == null ? 43 : $key.hashCode());
		final java.lang.Object $dispatcher = this.getDispatcher();
		result = result * PRIME + ($dispatcher == null ? 43 : $dispatcher.hashCode());
		final java.lang.Object $category = this.getCategory();
		result = result * PRIME + ($category == null ? 43 : $category.hashCode());
		final java.lang.Object $fingerprint = this.getFingerprint();
		result = result * PRIME + ($fingerprint == null ? 43 : $fingerprint.hashCode());
		final java.lang.Object $timestamp = this.getTimestamp();
		result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
		final java.lang.Object $requirementMessage = this.getRequirementMessage();
		result = result * PRIME + ($requirementMessage == null ? 43 : $requirementMessage.hashCode());
		final java.lang.Object $responseMessage = this.getResponseMessage();
		result = result * PRIME + ($responseMessage == null ? 43 : $responseMessage.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "EIssuerView(id=" + this.getId() + ", bin=" + this.getBin() + ", key=" + this.getKey() + ", dispatcher=" + this.getDispatcher() + ", category=" + this.getCategory() + ", fingerprint=" + this.getFingerprint() + ", timestamp=" + this.getTimestamp() + ", requirementMessage=" + this.getRequirementMessage() + ", responseMessage=" + this.getResponseMessage() + ")";
	}
}
