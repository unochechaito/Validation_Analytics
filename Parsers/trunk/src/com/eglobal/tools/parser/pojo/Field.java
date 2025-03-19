/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.pojo;

//import lombok.*;
import java.nio.ByteBuffer;

public class Field<V> {
    private int index;
    private String name;
    private ByteBuffer raw;
    private V value;

    @java.lang.SuppressWarnings("all")
    public int getIndex() {
        return this.index;
    }

    @java.lang.SuppressWarnings("all")
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    public ByteBuffer getRaw() {
        return this.raw;
    }

    @java.lang.SuppressWarnings("all")
    public V getValue() {
        return this.value;
    }

    @java.lang.SuppressWarnings("all")
    public void setIndex(final int index) {
        this.index = index;
    }

    @java.lang.SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    public void setRaw(final ByteBuffer raw) {
        this.raw = raw;
    }

    @java.lang.SuppressWarnings("all")
    public void setValue(final V value) {
        this.value = value;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Field)) return false;
        final Field<?> other = (Field<?>) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (this.getIndex() != other.getIndex()) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$raw = this.getRaw();
        final java.lang.Object other$raw = other.getRaw();
        if (this$raw == null ? other$raw != null : !this$raw.equals(other$raw)) return false;
        final java.lang.Object this$value = this.getValue();
        final java.lang.Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Field;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getIndex();
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $raw = this.getRaw();
        result = result * PRIME + ($raw == null ? 43 : $raw.hashCode());
        final java.lang.Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        return result;
    }

    @java.lang.SuppressWarnings("all")
    public Field() {
    }

    @java.lang.SuppressWarnings("all")
    public Field(final int index, final String name, final ByteBuffer raw, final V value) {
        this.index = index;
        this.name = name;
        this.raw = raw;
        this.value = value;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Field(index=" + this.getIndex() + ", name=" + this.getName() + ", raw=" + this.getRaw() + ", value=" + this.getValue() + ")";
    }
}
