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
