/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.statistics;


import java.io.Serializable;


public class FileLocationState implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ultimaUbicacionSeleccionada;

    public String getUltimaUbicacionSeleccionada() {
        return ultimaUbicacionSeleccionada;
    }

    public void setUltimaUbicacionSeleccionada(String ultimaUbicacionSeleccionada) {
        this.ultimaUbicacionSeleccionada = ultimaUbicacionSeleccionada;
    }
}
