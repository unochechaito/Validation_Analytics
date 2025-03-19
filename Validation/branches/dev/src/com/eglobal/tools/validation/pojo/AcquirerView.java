/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.pojo;

import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.validation.db.model.EAcquirerView;
import com.google.gson.Gson;

/**
 * @author egldt1029
 */
public class AcquirerView {
	private Long id;
	private String timestamp;
	private String dispatcher;
	private String bin;
	private String category;
	private String fingerprint;
	private String key;
	private Message requirementMessage;
	private Message responseMessage;

	public static EAcquirerView toEntity(AcquirerView acquirerView) {
		Gson gson = new Gson();
		EAcquirerView entity = new EAcquirerView();
		entity.setBin(acquirerView.getBin());
		entity.setCategory(acquirerView.getCategory());
		entity.setDispatcher(acquirerView.getDispatcher());
		entity.setFingerprint(acquirerView.getFingerprint());
		entity.setKey(acquirerView.getKey());
		entity.setRequirementMessage(gson.toJson(acquirerView.getRequirementMessage()));
		entity.setResponseMessage(gson.toJson(acquirerView.getResponseMessage()));
		entity.setTimestamp(gson.toJson(acquirerView.getTimestamp()));
		return entity;
	}

	public static AcquirerView fromEntity(EAcquirerView entity) {
		Gson gson = new Gson();
		AcquirerView acquirerView = new AcquirerView();
		acquirerView.setBin(entity.getBin());
		acquirerView.setCategory(entity.getCategory());
		acquirerView.setDispatcher(entity.getDispatcher());
		acquirerView.setFingerprint(entity.getFingerprint());
		acquirerView.setKey(entity.getKey());
		acquirerView.setRequirementMessage(gson.fromJson(entity.getRequirementMessage(), Message.class));
		acquirerView.setResponseMessage(gson.fromJson(entity.getResponseMessage(), Message.class));
		acquirerView.setTimestamp(entity.getTimestamp());
		return acquirerView;
	}

	@java.lang.SuppressWarnings("all")
	public AcquirerView() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getId() {
		return this.id;
	}

	@java.lang.SuppressWarnings("all")
	public String getTimestamp() {
		return this.timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public String getDispatcher() {
		return this.dispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public String getBin() {
		return this.bin;
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
	public String getKey() {
		return this.key;
	}

	@java.lang.SuppressWarnings("all")
	public Message getRequirementMessage() {
		return this.requirementMessage;
	}

	@java.lang.SuppressWarnings("all")
	public Message getResponseMessage() {
		return this.responseMessage;
	}

	@java.lang.SuppressWarnings("all")
	public void setId(final Long id) {
		this.id = id;
	}

	@java.lang.SuppressWarnings("all")
	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public void setDispatcher(final String dispatcher) {
		this.dispatcher = dispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public void setBin(final String bin) {
		this.bin = bin;
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
	public void setKey(final String key) {
		this.key = key;
	}

	@java.lang.SuppressWarnings("all")
	public void setRequirementMessage(final Message requirementMessage) {
		this.requirementMessage = requirementMessage;
	}

	@java.lang.SuppressWarnings("all")
	public void setResponseMessage(final Message responseMessage) {
		this.responseMessage = responseMessage;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof AcquirerView)) return false;
		final AcquirerView other = (AcquirerView) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$timestamp = this.getTimestamp();
		final java.lang.Object other$timestamp = other.getTimestamp();
		if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
		final java.lang.Object this$dispatcher = this.getDispatcher();
		final java.lang.Object other$dispatcher = other.getDispatcher();
		if (this$dispatcher == null ? other$dispatcher != null : !this$dispatcher.equals(other$dispatcher)) return false;
		final java.lang.Object this$bin = this.getBin();
		final java.lang.Object other$bin = other.getBin();
		if (this$bin == null ? other$bin != null : !this$bin.equals(other$bin)) return false;
		final java.lang.Object this$category = this.getCategory();
		final java.lang.Object other$category = other.getCategory();
		if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
		final java.lang.Object this$fingerprint = this.getFingerprint();
		final java.lang.Object other$fingerprint = other.getFingerprint();
		if (this$fingerprint == null ? other$fingerprint != null : !this$fingerprint.equals(other$fingerprint)) return false;
		final java.lang.Object this$key = this.getKey();
		final java.lang.Object other$key = other.getKey();
		if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
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
		return other instanceof AcquirerView;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final java.lang.Object $timestamp = this.getTimestamp();
		result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
		final java.lang.Object $dispatcher = this.getDispatcher();
		result = result * PRIME + ($dispatcher == null ? 43 : $dispatcher.hashCode());
		final java.lang.Object $bin = this.getBin();
		result = result * PRIME + ($bin == null ? 43 : $bin.hashCode());
		final java.lang.Object $category = this.getCategory();
		result = result * PRIME + ($category == null ? 43 : $category.hashCode());
		final java.lang.Object $fingerprint = this.getFingerprint();
		result = result * PRIME + ($fingerprint == null ? 43 : $fingerprint.hashCode());
		final java.lang.Object $key = this.getKey();
		result = result * PRIME + ($key == null ? 43 : $key.hashCode());
		final java.lang.Object $requirementMessage = this.getRequirementMessage();
		result = result * PRIME + ($requirementMessage == null ? 43 : $requirementMessage.hashCode());
		final java.lang.Object $responseMessage = this.getResponseMessage();
		result = result * PRIME + ($responseMessage == null ? 43 : $responseMessage.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "AcquirerView(id=" + this.getId() + ", timestamp=" + this.getTimestamp() + ", dispatcher=" + this.getDispatcher() + ", bin=" + this.getBin() + ", category=" + this.getCategory() + ", fingerprint=" + this.getFingerprint() + ", key=" + this.getKey() + ", requirementMessage=" + this.getRequirementMessage() + ", responseMessage=" + this.getResponseMessage() + ")";
	}
}
