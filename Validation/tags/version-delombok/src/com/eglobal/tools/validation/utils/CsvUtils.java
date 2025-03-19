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
