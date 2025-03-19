/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.report.dto;

import java.util.List;

public class AdicionalesReportImpl implements Report {
    private String proceso;
    private String registro;
    private String seq;
    private List<String> fields;


    @java.lang.SuppressWarnings("all")
    public static class AdicionalesReportImplBuilder {
        @java.lang.SuppressWarnings("all")
        private String proceso;
        @java.lang.SuppressWarnings("all")
        private String registro;
        @java.lang.SuppressWarnings("all")
        private String seq;
        @java.lang.SuppressWarnings("all")
        private List<String> fields;

        @java.lang.SuppressWarnings("all")
        AdicionalesReportImplBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public AdicionalesReportImpl.AdicionalesReportImplBuilder proceso(final String proceso) {
            this.proceso = proceso;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public AdicionalesReportImpl.AdicionalesReportImplBuilder registro(final String registro) {
            this.registro = registro;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public AdicionalesReportImpl.AdicionalesReportImplBuilder seq(final String seq) {
            this.seq = seq;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public AdicionalesReportImpl.AdicionalesReportImplBuilder fields(final List<String> fields) {
            this.fields = fields;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public AdicionalesReportImpl build() {
            return new AdicionalesReportImpl(this.proceso, this.registro, this.seq, this.fields);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "AdicionalesReportImpl.AdicionalesReportImplBuilder(proceso=" + this.proceso + ", registro=" + this.registro + ", seq=" + this.seq + ", fields=" + this.fields + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static AdicionalesReportImpl.AdicionalesReportImplBuilder builder() {
        return new AdicionalesReportImpl.AdicionalesReportImplBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public AdicionalesReportImpl(final String proceso, final String registro, final String seq, final List<String> fields) {
        this.proceso = proceso;
        this.registro = registro;
        this.seq = seq;
        this.fields = fields;
    }

    @java.lang.SuppressWarnings("all")
    public String getProceso() {
        return this.proceso;
    }

    @java.lang.SuppressWarnings("all")
    public String getRegistro() {
        return this.registro;
    }

    @java.lang.SuppressWarnings("all")
    public String getSeq() {
        return this.seq;
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
    public void setRegistro(final String registro) {
        this.registro = registro;
    }

    @java.lang.SuppressWarnings("all")
    public void setSeq(final String seq) {
        this.seq = seq;
    }

    @java.lang.SuppressWarnings("all")
    public void setFields(final List<String> fields) {
        this.fields = fields;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof AdicionalesReportImpl)) return false;
        final AdicionalesReportImpl other = (AdicionalesReportImpl) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$proceso = this.getProceso();
        final java.lang.Object other$proceso = other.getProceso();
        if (this$proceso == null ? other$proceso != null : !this$proceso.equals(other$proceso)) return false;
        final java.lang.Object this$registro = this.getRegistro();
        final java.lang.Object other$registro = other.getRegistro();
        if (this$registro == null ? other$registro != null : !this$registro.equals(other$registro)) return false;
        final java.lang.Object this$seq = this.getSeq();
        final java.lang.Object other$seq = other.getSeq();
        if (this$seq == null ? other$seq != null : !this$seq.equals(other$seq)) return false;
        final java.lang.Object this$fields = this.getFields();
        final java.lang.Object other$fields = other.getFields();
        if (this$fields == null ? other$fields != null : !this$fields.equals(other$fields)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof AdicionalesReportImpl;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $proceso = this.getProceso();
        result = result * PRIME + ($proceso == null ? 43 : $proceso.hashCode());
        final java.lang.Object $registro = this.getRegistro();
        result = result * PRIME + ($registro == null ? 43 : $registro.hashCode());
        final java.lang.Object $seq = this.getSeq();
        result = result * PRIME + ($seq == null ? 43 : $seq.hashCode());
        final java.lang.Object $fields = this.getFields();
        result = result * PRIME + ($fields == null ? 43 : $fields.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "AdicionalesReportImpl(proceso=" + this.getProceso() + ", registro=" + this.getRegistro() + ", seq=" + this.getSeq() + ", fields=" + this.getFields() + ")";
    }
}
