/**
 *   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
 *   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
 *   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
 *   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
 *   de correo seginfo@eglobal.com.mx                                                         *
 **/

package com.eglobal.tools.validation.utils;


import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsvUtils {
    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

    public static List<String[]> readCsv(String filePath) {
        File file = new File(filePath + ".txt");

        if (!file.exists() || !file.isFile()) {
            log.warn("Archivo no encontrado o invalido: {}", filePath);
            return Collections.emptyList();
        }

        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                data.add(row);
            }
        } catch (IOException e) {
            log.error("Error al leer el archivo CSV: {}", filePath, e);
        }

        return data;
    }
}
