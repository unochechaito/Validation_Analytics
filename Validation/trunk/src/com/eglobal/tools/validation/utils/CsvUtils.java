/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.utils;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class CsvUtils {

    public static List<String[]> readCsv(String filePath) throws IOException {
        File file = new File(filePath + ".txt");
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Archivo no válido: " + filePath);
        }
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                data.add(row);
            }
        }
        return data;
    }
}
