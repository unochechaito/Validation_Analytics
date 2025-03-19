/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.pojo;

/**
 * @author egldt1029
 */
public class RawcomLine {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RawcomLine.class);
	private String timestamp;
	private int dispatcher;
	private String pan;
	private String iap;
	private String listProcessor;
	private char readWrite;
	private int length;
	private String rawData;
	private byte[] rawByteData;


	@java.lang.SuppressWarnings("all")
	public static class RawcomLineBuilder {
		@java.lang.SuppressWarnings("all")
		private String timestamp;
		@java.lang.SuppressWarnings("all")
		private int dispatcher;
		@java.lang.SuppressWarnings("all")
		private String pan;
		@java.lang.SuppressWarnings("all")
		private String iap;
		@java.lang.SuppressWarnings("all")
		private String listProcessor;
		@java.lang.SuppressWarnings("all")
		private char readWrite;
		@java.lang.SuppressWarnings("all")
		private int length;
		@java.lang.SuppressWarnings("all")
		private String rawData;
		@java.lang.SuppressWarnings("all")
		private byte[] rawByteData;

		@java.lang.SuppressWarnings("all")
		RawcomLineBuilder() {
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder timestamp(final String timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder dispatcher(final int dispatcher) {
			this.dispatcher = dispatcher;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder pan(final String pan) {
			this.pan = pan;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder iap(final String iap) {
			this.iap = iap;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder listProcessor(final String listProcessor) {
			this.listProcessor = listProcessor;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder readWrite(final char readWrite) {
			this.readWrite = readWrite;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder length(final int length) {
			this.length = length;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder rawData(final String rawData) {
			this.rawData = rawData;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		public RawcomLine.RawcomLineBuilder rawByteData(final byte[] rawByteData) {
			this.rawByteData = rawByteData;
			return this;
		}

		@java.lang.SuppressWarnings("all")
		public RawcomLine build() {
			return new RawcomLine(this.timestamp, this.dispatcher, this.pan, this.iap, this.listProcessor, this.readWrite, this.length, this.rawData, this.rawByteData);
		}

		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		public java.lang.String toString() {
			return "RawcomLine.RawcomLineBuilder(timestamp=" + this.timestamp + ", dispatcher=" + this.dispatcher + ", pan=" + this.pan + ", iap=" + this.iap + ", listProcessor=" + this.listProcessor + ", readWrite=" + this.readWrite + ", length=" + this.length + ", rawData=" + this.rawData + ", rawByteData=" + java.util.Arrays.toString(this.rawByteData) + ")";
		}
	}

	@java.lang.SuppressWarnings("all")
	public static RawcomLine.RawcomLineBuilder builder() {
		return new RawcomLine.RawcomLineBuilder();
	}

	@java.lang.SuppressWarnings("all")
	public String getTimestamp() {
		return this.timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public int getDispatcher() {
		return this.dispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public String getPan() {
		return this.pan;
	}

	@java.lang.SuppressWarnings("all")
	public String getIap() {
		return this.iap;
	}

	@java.lang.SuppressWarnings("all")
	public String getListProcessor() {
		return this.listProcessor;
	}

	@java.lang.SuppressWarnings("all")
	public char getReadWrite() {
		return this.readWrite;
	}

	@java.lang.SuppressWarnings("all")
	public int getLength() {
		return this.length;
	}

	@java.lang.SuppressWarnings("all")
	public String getRawData() {
		return this.rawData;
	}

	@java.lang.SuppressWarnings("all")
	public byte[] getRawByteData() {
		return this.rawByteData;
	}

	@java.lang.SuppressWarnings("all")
	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	@java.lang.SuppressWarnings("all")
	public void setDispatcher(final int dispatcher) {
		this.dispatcher = dispatcher;
	}

	@java.lang.SuppressWarnings("all")
	public void setPan(final String pan) {
		this.pan = pan;
	}

	@java.lang.SuppressWarnings("all")
	public void setIap(final String iap) {
		this.iap = iap;
	}

	@java.lang.SuppressWarnings("all")
	public void setListProcessor(final String listProcessor) {
		this.listProcessor = listProcessor;
	}

	@java.lang.SuppressWarnings("all")
	public void setReadWrite(final char readWrite) {
		this.readWrite = readWrite;
	}

	@java.lang.SuppressWarnings("all")
	public void setLength(final int length) {
		this.length = length;
	}

	@java.lang.SuppressWarnings("all")
	public void setRawData(final String rawData) {
		this.rawData = rawData;
	}

	@java.lang.SuppressWarnings("all")
	public void setRawByteData(final byte[] rawByteData) {
		this.rawByteData = rawByteData;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof RawcomLine)) return false;
		final RawcomLine other = (RawcomLine) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		if (this.getDispatcher() != other.getDispatcher()) return false;
		if (this.getReadWrite() != other.getReadWrite()) return false;
		if (this.getLength() != other.getLength()) return false;
		final java.lang.Object this$timestamp = this.getTimestamp();
		final java.lang.Object other$timestamp = other.getTimestamp();
		if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
		final java.lang.Object this$pan = this.getPan();
		final java.lang.Object other$pan = other.getPan();
		if (this$pan == null ? other$pan != null : !this$pan.equals(other$pan)) return false;
		final java.lang.Object this$iap = this.getIap();
		final java.lang.Object other$iap = other.getIap();
		if (this$iap == null ? other$iap != null : !this$iap.equals(other$iap)) return false;
		final java.lang.Object this$listProcessor = this.getListProcessor();
		final java.lang.Object other$listProcessor = other.getListProcessor();
		if (this$listProcessor == null ? other$listProcessor != null : !this$listProcessor.equals(other$listProcessor)) return false;
		final java.lang.Object this$rawData = this.getRawData();
		final java.lang.Object other$rawData = other.getRawData();
		if (this$rawData == null ? other$rawData != null : !this$rawData.equals(other$rawData)) return false;
		if (!java.util.Arrays.equals(this.getRawByteData(), other.getRawByteData())) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof RawcomLine;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + this.getDispatcher();
		result = result * PRIME + this.getReadWrite();
		result = result * PRIME + this.getLength();
		final java.lang.Object $timestamp = this.getTimestamp();
		result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
		final java.lang.Object $pan = this.getPan();
		result = result * PRIME + ($pan == null ? 43 : $pan.hashCode());
		final java.lang.Object $iap = this.getIap();
		result = result * PRIME + ($iap == null ? 43 : $iap.hashCode());
		final java.lang.Object $listProcessor = this.getListProcessor();
		result = result * PRIME + ($listProcessor == null ? 43 : $listProcessor.hashCode());
		final java.lang.Object $rawData = this.getRawData();
		result = result * PRIME + ($rawData == null ? 43 : $rawData.hashCode());
		result = result * PRIME + java.util.Arrays.hashCode(this.getRawByteData());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "RawcomLine(timestamp=" + this.getTimestamp() + ", dispatcher=" + this.getDispatcher() + ", pan=" + this.getPan() + ", iap=" + this.getIap() + ", listProcessor=" + this.getListProcessor() + ", readWrite=" + this.getReadWrite() + ", length=" + this.getLength() + ", rawData=" + this.getRawData() + ", rawByteData=" + java.util.Arrays.toString(this.getRawByteData()) + ")";
	}

	@java.lang.SuppressWarnings("all")
	public RawcomLine() {
	}

	@java.lang.SuppressWarnings("all")
	public RawcomLine(final String timestamp, final int dispatcher, final String pan, final String iap, final String listProcessor, final char readWrite, final int length, final String rawData, final byte[] rawByteData) {
		this.timestamp = timestamp;
		this.dispatcher = dispatcher;
		this.pan = pan;
		this.iap = iap;
		this.listProcessor = listProcessor;
		this.readWrite = readWrite;
		this.length = length;
		this.rawData = rawData;
		this.rawByteData = rawByteData;
	}
}
