/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation;

import java.awt.*;

public class Alert {
    private final String text;
    private final Color color;

    public Alert(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    @Override
    public String toString() {
        return text;
    }

    @java.lang.SuppressWarnings("all")
    public String getText() {
        return this.text;
    }

    @java.lang.SuppressWarnings("all")
    public Color getColor() {
        return this.color;
    }
}
