/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.pojo;

import java.util.LinkedHashMap;

/**
 * @author egldt1029
 */
public class Message extends AParsedObject {
	private String mti;
	private Integer mli;
	private String timestamp;

	@Override
	public void setField(String key, Object value) {
		if ("mti".equalsIgnoreCase(key)) {
			setMti(mti);
		}
		if (key.contains(".")) {
			String[] keySplitted = key.split("\\.");
			FieldExt fieldExt = (FieldExt) getField(keySplitted[0]);
			fieldExt.setField(keySplitted[1], value);
		} else {
			super.setField(key, value);
		}
	}

	@Override
	public Object getField(String key) {
		if ("mti".equalsIgnoreCase(key)) {
			return getMti();
		}
		if ("mli".equalsIgnoreCase(key)) {
			return getMli();
		}
		if (key.contains(".")) {
			//subfields
			String[] splitedKey = key.split("\\.");
			Object field = fields.get(splitedKey[0]);
			if (field instanceof FieldExt) {
				return ((FieldExt) field).getField(splitedKey[1]);
			}
		}
		return super.getField(key);
	}

	@Override
	public void removeField(String key) {
		if (key.contains(".")) {
			//subfields
			String[] splitedKey = key.split("\\.");
			Object field = fields.get(splitedKey[0]);
			if (field instanceof FieldExt) {
				((FieldExt) field).removeField(splitedKey[1]);
			}
		}
		fields.remove(key);
	}

	public void setMti(String mti) {
		this.mti = mti;
		Object o = fields.get("Header");
		if (o instanceof FieldExt) {
			FieldExt header = (FieldExt) fields.get("Header");
			header.setField("MTI", mti);
		}
	}

	@Override
	public Message clone() {
		Message newMessage = new Message();
		newMessage.fields = new LinkedHashMap<>();
		for (String field : fields.keySet()) {
			Object o = fields.get(field);
			if (o instanceof FieldExt) {
				FieldExt fe = (FieldExt) o;
				FieldExt newFe = new FieldExt();
				for (String subfield : fe.getSubfields().keySet()) {
					newFe.setField(subfield, fe.getField(subfield));
				}
				newMessage.fields.put(field, newFe);
			} else {
				newMessage.fields.put(field, fields.get(field));
			}
		}
		newMessage.type = type;
		newMessage.mti = mti;
		newMessage.mli = mli;
		return newMessage;
	}

	@java.lang.SuppressWarnings("all")
	public Message() {
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof Message)) return false;
		final Message other = (Message) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$mli = this.getMli();
		final java.lang.Object other$mli = other.getMli();
		if (this$mli == null ? other$mli != null : !this$mli.equals(other$mli)) return false;
		final java.lang.Object this$mti = this.getMti();
		final java.lang.Object other$mti = other.getMti();
		if (this$mti == null ? other$mti != null : !this$mti.equals(other$mti)) return false;
		final java.lang.Object this$timestamp = this.getTimestamp();
		final java.lang.Object other$timestamp = other.getTimestamp();
		if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof Message;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $mli = this.getMli();
		result = result * PRIME + ($mli == null ? 43 : $mli.hashCode());
		final java.lang.Object $mti = this.getMti();
		result = result * PRIME + ($mti == null ? 43 : $mti.hashCode());
		final java.lang.Object $timestamp = this.getTimestamp();
		result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "Message(super=" + super.toString() + ", mti=" + this.getMti() + ", mli=" + this.getMli() + ", timestamp=" + this.getTimestamp() + ")";
	}

	@java.lang.SuppressWarnings("all")
	public String getMti() {
		return this.mti;
	}

	@java.lang.SuppressWarnings("all")
	public Integer getMli() {
		return this.mli;
	}

	@java.lang.SuppressWarnings("all")
	public void setMli(final Integer mli) {
		this.mli = mli;
	}

	@java.lang.SuppressWarnings("all")
	public String getTimestamp() {
		return this.timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}
}
