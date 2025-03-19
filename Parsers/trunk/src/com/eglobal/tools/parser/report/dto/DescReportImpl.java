/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.report.dto;

import java.util.List;

public class DescReportImpl implements Report {
    private String proceso;
    private String bin;
    private String[] dispatchers;
    private String processingCode;
    private String pem;
    private List<String> fields;


    @java.lang.SuppressWarnings("all")
    public static class DescReportImplBuilder {
        @java.lang.SuppressWarnings("all")
        private String proceso;
        @java.lang.SuppressWarnings("all")
        private String bin;
        @java.lang.SuppressWarnings("all")
        private String[] dispatchers;
        @java.lang.SuppressWarnings("all")
        private String processingCode;
        @java.lang.SuppressWarnings("all")
        private String pem;
        @java.lang.SuppressWarnings("all")
        private List<String> fields;

        @java.lang.SuppressWarnings("all")
        DescReportImplBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public DescReportImpl.DescReportImplBuilder proceso(final String proceso) {
            this.proceso = proceso;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public DescReportImpl.DescReportImplBuilder bin(final String bin) {
            this.bin = bin;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public DescReportImpl.DescReportImplBuilder dispatchers(final String[] dispatchers) {
            this.dispatchers = dispatchers;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public DescReportImpl.DescReportImplBuilder processingCode(final String processingCode) {
            this.processingCode = processingCode;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public DescReportImpl.DescReportImplBuilder pem(final String pem) {
            this.pem = pem;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public DescReportImpl.DescReportImplBuilder fields(final List<String> fields) {
            this.fields = fields;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public DescReportImpl build() {
            return new DescReportImpl(this.proceso, this.bin, this.dispatchers, this.processingCode, this.pem, this.fields);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "DescReportImpl.DescReportImplBuilder(proceso=" + this.proceso + ", bin=" + this.bin + ", dispatchers=" + java.util.Arrays.deepToString(this.dispatchers) + ", processingCode=" + this.processingCode + ", pem=" + this.pem + ", fields=" + this.fields + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static DescReportImpl.DescReportImplBuilder builder() {
        return new DescReportImpl.DescReportImplBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public DescReportImpl(final String proceso, final String bin, final String[] dispatchers, final String processingCode, final String pem, final List<String> fields) {
        this.proceso = proceso;
        this.bin = bin;
        this.dispatchers = dispatchers;
        this.processingCode = processingCode;
        this.pem = pem;
        this.fields = fields;
    }

    @java.lang.SuppressWarnings("all")
    public String getProceso() {
        return this.proceso;
    }

    @java.lang.SuppressWarnings("all")
    public String getBin() {
        return this.bin;
    }

    @java.lang.SuppressWarnings("all")
    public String[] getDispatchers() {
        return this.dispatchers;
    }

    @java.lang.SuppressWarnings("all")
    public String getProcessingCode() {
        return this.processingCode;
    }

    @java.lang.SuppressWarnings("all")
    public String getPem() {
        return this.pem;
    }

    @java.lang.SuppressWarnings("all")
    public List<String> getFields() {
        return this.fields;
    }

    @java.lang.SuppressWarnings("all")
    public void setProceso(final String proceso) {
        this.proceso = proceso;
    }

    @java.lang.SuppressWarnings("all")
    public void setBin(final String bin) {
        this.bin = bin;
    }

    @java.lang.SuppressWarnings("all")
    public void setDispatchers(final String[] dispatchers) {
        this.dispatchers = dispatchers;
    }

    @java.lang.SuppressWarnings("all")
    public void setProcessingCode(final String processingCode) {
        this.processingCode = processingCode;
    }

    @java.lang.SuppressWarnings("all")
    public void setPem(final String pem) {
        this.pem = pem;
    }

    @java.lang.SuppressWarnings("all")
    public void setFields(final List<String> fields) {
        this.fields = fields;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof DescReportImpl)) return false;
        final DescReportImpl other = (DescReportImpl) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$proceso = this.getProceso();
        final java.lang.Object other$proceso = other.getProceso();
        if (this$proceso == null ? other$proceso != null : !this$proceso.equals(other$proceso)) return false;
        final java.lang.Object this$bin = this.getBin();
        final java.lang.Object other$bin = other.getBin();
        if (this$bin == null ? other$bin != null : !this$bin.equals(other$bin)) return false;
        if (!java.util.Arrays.deepEquals(this.getDispatchers(), other.getDispatchers())) return false;
        final java.lang.Object this$processingCode = this.getProcessingCode();
        final java.lang.Object other$processingCode = other.getProcessingCode();
        if (this$processingCode == null ? other$processingCode != null : !this$processingCode.equals(other$processingCode)) return false;
        final java.lang.Object this$pem = this.getPem();
        final java.lang.Object other$pem = other.getPem();
        if (this$pem == null ? other$pem != null : !this$pem.equals(other$pem)) return false;
        final java.lang.Object this$fields = this.getFields();
        final java.lang.Object other$fields = other.getFields();
        if (this$fields == null ? other$fields != null : !this$fields.equals(other$fields)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof DescReportImpl;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $proceso = this.getProceso();
        result = result * PRIME + ($proceso == null ? 43 : $proceso.hashCode());
        final java.lang.Object $bin = this.getBin();
        result = result * PRIME + ($bin == null ? 43 : $bin.hashCode());
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getDispatchers());
        final java.lang.Object $processingCode = this.getProcessingCode();
        result = result * PRIME + ($processingCode == null ? 43 : $processingCode.hashCode());
        final java.lang.Object $pem = this.getPem();
        result = result * PRIME + ($pem == null ? 43 : $pem.hashCode());
        final java.lang.Object $fields = this.getFields();
        result = result * PRIME + ($fields == null ? 43 : $fields.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "DescReportImpl(proceso=" + this.getProceso() + ", bin=" + this.getBin() + ", dispatchers=" + java.util.Arrays.deepToString(this.getDispatchers()) + ", processingCode=" + this.getProcessingCode() + ", pem=" + this.getPem() + ", fields=" + this.getFields() + ")";
    }
}
