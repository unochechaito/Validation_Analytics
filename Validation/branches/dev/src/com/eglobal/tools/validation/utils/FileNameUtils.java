package com.eglobal.tools.validation.utils;

public class FileNameUtils {
    private FileNameUtils(){ throw new UnsupportedOperationException("Accion no soportada por la clase."); }

    public static String getExtension(String nombreArchivo) {
        // Verifica si el nombre del archivo es nulo o vacío
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return "";
        }

        // Encuentra la última ocurrencia del punto '.'
        int ultimoPunto = nombreArchivo.lastIndexOf('.');

        // Si no hay punto o el punto está al final, no hay extensión
        if (ultimoPunto == -1 || ultimoPunto == nombreArchivo.length() - 1) {
            return "";
        }

        // Retorna la subcadena desde el punto hasta el final
        return nombreArchivo.substring(ultimoPunto + 1);
    }
}
