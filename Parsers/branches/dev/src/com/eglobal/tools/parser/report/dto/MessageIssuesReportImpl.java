/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.report.dto;

import java.nio.ByteBuffer;
import com.eglobal.tools.parser.pojo.FieldFormat;

public class MessageIssuesReportImpl implements Report {
	private FieldFormat fieldFormat;
	private ByteBuffer currentBuffer;
	private byte[] rawMessage;

	@java.lang.SuppressWarnings("all")
	MessageIssuesReportImpl(final FieldFormat fieldFormat, final ByteBuffer currentBuffer, final byte[] rawMessage) {
		this.fieldFormat = fieldFormat;
		this.currentBuffer = currentBuffer;
		this.rawMessage = rawMessage;
	}


	@java.lang.SuppressWarnings("all")
	public static class MessageIssuesReportImplBuilder {
		@java.lang.SuppressWarnings("all")
		private FieldFormat fieldFormat;
		@java.lang.SuppressWarnings("all")
		private ByteBuffer currentBuffer;
		@java.lang.SuppressWarnings("all")
		private byte[] rawMessage;

		@java.lang.SuppressWarnings("all")
		MessageIssuesReportImplBuilder() {
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public MessageIssuesReportImpl.MessageIssuesReportImplBuilder fieldFormat(final FieldFormat fieldFormat) {
			this.fieldFormat = fieldFormat;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public MessageIssuesReportImpl.MessageIssuesReportImplBuilder currentBuffer(final ByteBuffer currentBuffer) {
			this.currentBuffer = currentBuffer;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public MessageIssuesReportImpl.MessageIssuesReportImplBuilder rawMessage(final byte[] rawMessage) {
			this.rawMessage = rawMessage;
			return this;
		}

		@java.lang.SuppressWarnings("all")
		public MessageIssuesReportImpl build() {
			return new MessageIssuesReportImpl(this.fieldFormat, this.currentBuffer, this.rawMessage);
		}

		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		public java.lang.String toString() {
			return "MessageIssuesReportImpl.MessageIssuesReportImplBuilder(fieldFormat=" + this.fieldFormat + ", currentBuffer=" + this.currentBuffer + ", rawMessage=" + java.util.Arrays.toString(this.rawMessage) + ")";
		}
	}

	@java.lang.SuppressWarnings("all")
	public static MessageIssuesReportImpl.MessageIssuesReportImplBuilder builder() {
		return new MessageIssuesReportImpl.MessageIssuesReportImplBuilder();
	}

	@java.lang.SuppressWarnings("all")
	public FieldFormat getFieldFormat() {
		return this.fieldFormat;
	}

	@java.lang.SuppressWarnings("all")
	public ByteBuffer getCurrentBuffer() {
		return this.currentBuffer;
	}

	@java.lang.SuppressWarnings("all")
	public byte[] getRawMessage() {
		return this.rawMessage;
	}

	@java.lang.SuppressWarnings("all")
	public void setFieldFormat(final FieldFormat fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	@java.lang.SuppressWarnings("all")
	public void setCurrentBuffer(final ByteBuffer currentBuffer) {
		this.currentBuffer = currentBuffer;
	}

	@java.lang.SuppressWarnings("all")
	public void setRawMessage(final byte[] rawMessage) {
		this.rawMessage = rawMessage;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof MessageIssuesReportImpl)) return false;
		final MessageIssuesReportImpl other = (MessageIssuesReportImpl) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$fieldFormat = this.getFieldFormat();
		final java.lang.Object other$fieldFormat = other.getFieldFormat();
		if (this$fieldFormat == null ? other$fieldFormat != null : !this$fieldFormat.equals(other$fieldFormat)) return false;
		final java.lang.Object this$currentBuffer = this.getCurrentBuffer();
		final java.lang.Object other$currentBuffer = other.getCurrentBuffer();
		if (this$currentBuffer == null ? other$currentBuffer != null : !this$currentBuffer.equals(other$currentBuffer)) return false;
		if (!java.util.Arrays.equals(this.getRawMessage(), other.getRawMessage())) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof MessageIssuesReportImpl;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $fieldFormat = this.getFieldFormat();
		result = result * PRIME + ($fieldFormat == null ? 43 : $fieldFormat.hashCode());
		final java.lang.Object $currentBuffer = this.getCurrentBuffer();
		result = result * PRIME + ($currentBuffer == null ? 43 : $currentBuffer.hashCode());
		result = result * PRIME + java.util.Arrays.hashCode(this.getRawMessage());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "MessageIssuesReportImpl(fieldFormat=" + this.getFieldFormat() + ", currentBuffer=" + this.getCurrentBuffer() + ", rawMessage=" + java.util.Arrays.toString(this.getRawMessage()) + ")";
	}
}
