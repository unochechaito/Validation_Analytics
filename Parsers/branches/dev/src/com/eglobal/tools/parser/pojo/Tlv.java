/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.pojo;

public class Tlv {
    private String tag;
    private int length;
    private String value;

    @java.lang.SuppressWarnings("all")
    public String getTag() {
        return this.tag;
    }

    @java.lang.SuppressWarnings("all")
    public int getLength() {
        return this.length;
    }

    @java.lang.SuppressWarnings("all")
    public String getValue() {
        return this.value;
    }

    @java.lang.SuppressWarnings("all")
    public void setTag(final String tag) {
        this.tag = tag;
    }

    @java.lang.SuppressWarnings("all")
    public void setLength(final int length) {
        this.length = length;
    }

    @java.lang.SuppressWarnings("all")
    public void setValue(final String value) {
        this.value = value;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Tlv)) return false;
        final Tlv other = (Tlv) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.getLength() != other.getLength()) return false;
        final java.lang.Object this$tag = this.getTag();
        final java.lang.Object other$tag = other.getTag();
        if (this$tag == null ? other$tag != null : !this$tag.equals(other$tag)) return false;
        final java.lang.Object this$value = this.getValue();
        final java.lang.Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Tlv;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getLength();
        final java.lang.Object $tag = this.getTag();
        result = result * PRIME + ($tag == null ? 43 : $tag.hashCode());
        final java.lang.Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Tlv(tag=" + this.getTag() + ", length=" + this.getLength() + ", value=" + this.getValue() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public Tlv(final String tag, final int length, final String value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }

    @java.lang.SuppressWarnings("all")
    public Tlv() {
    }
}
