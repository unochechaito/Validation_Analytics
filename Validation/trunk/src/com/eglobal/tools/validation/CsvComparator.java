/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation;

import com.eglobal.tools.validation.utils.CsvUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

public class CsvComparator {
    @java.lang.SuppressWarnings("all")
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(CsvComparator.class);
    
    private JTable leftTable;
    private JTable rightTable;
    
    private Set<Integer> matchingRows = new HashSet<>();
    private Set<Integer> uniqueLeftRows = new HashSet<>();
    private Set<Integer> uniqueRightRows = new HashSet<>();
    private Set<Integer> diffRows = new HashSet<>();
    private List<Integer> diffColumns = new ArrayList<>();

    public CsvComparator(JTable leftTable, JTable rightTable) {
        this.leftTable = leftTable;
        this.rightTable = rightTable;
        //applyRowColors();
    }

    public void compareCsvFiles(String leftDir, String rightDir, String photoFileName) throws IOException {
        String leftFullFilePath = new File(leftDir, photoFileName).getCanonicalPath();
        String rightFullFilePath = new File(rightDir, photoFileName).getCanonicalPath();
        
        DefaultTableModel leftModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        DefaultTableModel rightModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        leftTable.setModel(leftModel);
        rightTable.setModel(rightModel);
        diffColumns.clear();
        diffRows.clear();
        
        List<String[]> leftCsvData = CsvUtils.readCsv(leftFullFilePath);
        List<String[]> rightCsvData = CsvUtils.readCsv(rightFullFilePath);
        List<String> headersLeft = Arrays.asList(leftCsvData.get(0));
        List<String> headersRight = Arrays.asList(rightCsvData.get(0));
        
        for (String header : headersLeft) {
            leftModel.addColumn(header + " (Base)");
        }
        for (String header : headersRight) {
            rightModel.addColumn(header + " (A comparar)");
        }
        
        processAlignment(photoFileName, leftModel, rightModel, leftCsvData, rightCsvData, headersLeft, headersRight);
        
        identifyRowTypes((DefaultTableModel) leftTable.getModel(), (DefaultTableModel) rightTable.getModel());
        
        applyRowColors();
        leftTable.repaint();
        rightTable.repaint();
    }

    private void processAlignment(String photoFileName, DefaultTableModel leftModel, DefaultTableModel rightModel, List<String[]> leftCsvData, List<String[]> rightCsvData, List<String> headersLeft, List<String> headersRight) {
        
    	
        if (!leftCsvData.isEmpty()) {
            leftCsvData.remove(0);
        }
        if (!rightCsvData.isEmpty()) {
            rightCsvData.remove(0);
        }
    	
        Comparator<String[]> comparator = Comparator.comparing(row -> row.length > 0 ? row[0] : "");
        leftCsvData.sort(comparator);
        rightCsvData.sort(comparator);
    	
    	
        Map<String, Runnable> alignmentStrategies = new HashMap<>();

        List<String> oneColumnsKeys = Arrays.asList(
                "Porcentaje de ON2 DESC",
                "Campos presentes por tipo de transacción recibidos de adquirente Visa",
                "Campos presentes por tipo de transacción recibidos de adquirente MasterCard",
                "Campos presentes por tipo de transacción enviados al emisor Visa",
                "Campos presentes por tipo de transacción enviados al emisor MasterCard",
                "Campos presentes respondidos por tipo de transaccion al adquirente Visa",
                "Campos presentes respondidos por tipo de transaccion al adquirente Mastercard",
                "Campos presentes por tipo de transaccion respondidos por el emisor Visa",
                "Campos presentes por tipo de transaccion respondidos por el emisor MasterCard",
                "Tags emv por tipo de transaccon recibidos de adquirente Visa",
                "Tags emv por tipo de transaccon recibidos de adquirente MasterCard",
                "Tags emv por tipo de transaccon enviados al emisor Visa",
                "Tags emv por tipo de transaccon enviados al emisor MasterCard"
        );
        for (String key : oneColumnsKeys) {
            alignmentStrategies.put(key,
                    () -> alignRowsByFirstColumn(leftModel, rightModel, leftCsvData, rightCsvData));
        }
        
        
        List<String> twoColumnsKeys = Arrays.asList(
                "Porcentaje de ON2 por BIN",
                "Porcentaje de ON2 por Dispatcher Adquirente",
                "Mapeo MTI-ProcCode a TranCode",
                "Mapeo de codigos de error C39 emisor, on2 y C39 adquierente",
                "Codigos de C39 por Dispatcher emisor",
                "C44 respondido por el emisor Visa por operativa",
                "Mapeo MTI-ProcCode a TranCode",
                "Campos presentes por tipo de transacción recibidos de adquirentes Nacional",
                "Campos presentes por tipo de transacción enviados a emisores Nacional",
                "Campos presentes por tipo de transacción respondidos por emisores Nacional",
                "Campos presentes por tipo de transacción respondidos a adquirentes Nacional",
                "Tokens presentes por tipo de transaccion recibidos de adquirentes Nacional",
                "Tokens presentes por tipo de transaccion enviados a emisores Nacional",
                "Tokens presentes por tipo de transacción respondidos a adquirentes Nacional",
                "Tokens presentes por tipo de transaccion respondiso de emisores Nacional"
        );
        for (String key : twoColumnsKeys) {
            alignmentStrategies.put(key,
                    () -> alignRowsByTwoColumns(leftModel, rightModel, leftCsvData, rightCsvData));
        }
        
        List<String> threeColumnsKeys = Arrays.asList(
                "Tipo de transacciones declinadas por el emisor y aprobadas al adquirente",
                "C44 respondido al adquirente Visa por operativa"
        );
        for (String key : threeColumnsKeys) {
            alignmentStrategies.put(key,
                    () -> alignRowsByThreeColumns(leftModel, rightModel, leftCsvData, rightCsvData));
        }

        List<String> fourColumnsKeys = Arrays.asList(
                "Indicadores en el desc de Tipo de Cambio por flujo adquirente internacional a emisor",
                "4"
        );
        for (String key : fourColumnsKeys) {
            alignmentStrategies.put(key,
                    () -> alignRowsByFourColumns(leftModel, rightModel, leftCsvData, rightCsvData));
        }

        List<String> sixColumnsKeys = Arrays.asList(
                "Adicionales por tipo de transaccion plataforma de adquirente y emisor",
                "6"
        );
        for (String key : sixColumnsKeys) {
            alignmentStrategies.put(key,
                    () -> alignRowsBySixColumns(leftModel, rightModel, leftCsvData, rightCsvData));
        }

        alignmentStrategies.getOrDefault(photoFileName,
                () -> defaultComparison(leftModel, rightModel, leftCsvData, rightCsvData, headersLeft, headersRight)
        ).run();
    }    
        
        
    AlignmentResult alignRowsByFirstColumn(DefaultTableModel leftModel, DefaultTableModel rightModel, List<String[]> leftCsvData, List<String[]> rightCsvData) {
        
    	Map<String, List<String[]>> leftMap = new LinkedHashMap<>();
        Map<String, List<String[]>> rightMap = new LinkedHashMap<>();
        
        populateMap(leftCsvData, leftMap, "LEFT");
        populateMap(rightCsvData, rightMap, "RIGHT");
                        
        Set<String> allKeys = new LinkedHashSet<>(leftMap.keySet());
        allKeys.addAll(rightMap.keySet());
        
        int totalAligned = 0;
        int greenCount = 0;
        int nonGreenCount = 0;
        
        for (String key : allKeys) {
            List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
            List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());
            
            leftRows.sort(Comparator.comparing(row -> row.length > 1 ? row[1] : ""));
            rightRows.sort(Comparator.comparing(row -> row.length > 1 ? row[1] : ""));

            int maxRows = Math.max(leftRows.size(), rightRows.size());
            boolean[] matchedRightRows = new boolean[rightRows.size()];

            log.info("Procesando clave: {}, Filas en izquierda: {}, Filas en derecha: {}", key, leftRows.size(), rightRows.size());

            for (int i = 0; i < leftRows.size(); i++) {
                String[] leftRow = leftRows.get(i);
                boolean matchFound = false;

                for (int j = 0; j < rightRows.size(); j++) {
                    if (!matchedRightRows[j]) { // Solo buscar en filas que aún no han sido emparejadas
                        String[] rightRow = rightRows.get(j);

                        if (Arrays.equals(leftRow, rightRow)) { // Comparación exacta
                            matchedRightRows[j] = true; // Marcar como emparejada
                            totalAligned++;
                            greenCount++;
                            matchFound = true;
                            addRowWithDiffHighlight(leftModel, leftRow, rightModel, rightRow, leftRow.length);
                            break;
                        }
                    }
                }
                if (!matchFound) {
                    nonGreenCount++;
                    addRowWithDiffHighlight(leftModel, leftRow, rightModel, new String[leftRow.length], leftRow.length);
                }
            }

            for (int j = 0; j < rightRows.size(); j++) {
                if (!matchedRightRows[j]) {
                    addRowWithDiffHighlight(leftModel, new String[rightRows.get(j).length], rightModel, rightRows.get(j), rightRows.get(j).length);
                }
            }            
  
        }
        
        return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
    }

    AlignmentResult alignRowsByTwoColumns(DefaultTableModel leftModel, DefaultTableModel rightModel, List<String[]> leftCsvData, List<String[]> rightCsvData) {
        Map<String, List<String[]>> leftMap = new LinkedHashMap<>();
        Map<String, List<String[]>> rightMap = new LinkedHashMap<>();
        for (int i = 1; i < leftCsvData.size(); i++) {
            String[] row = leftCsvData.get(i);
            String key = getTwoColumnKey(row);
            leftMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (int i = 1; i < rightCsvData.size(); i++) {
            String[] row = rightCsvData.get(i);
            String key = getTwoColumnKey(row);
            rightMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        Set<String> allKeys = new LinkedHashSet<>(leftMap.keySet());
        allKeys.addAll(rightMap.keySet());
        int totalAligned = 0;
        int greenCount = 0;
        int nonGreenCount = 0;
        for (String key : allKeys) {
            List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
            List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());
            int maxRows = Math.max(leftRows.size(), rightRows.size());
            for (int i = 0; i < maxRows; i++) {
                String[] leftRow = i < leftRows.size() ? leftRows.get(i) : new String[0];
                String[] rightRow = i < rightRows.size() ? rightRows.get(i) : new String[0];
                boolean areFirstTwoColumnsAligned = leftRow.length >= 2 && rightRow.length >= 2 && leftRow[0].equals(rightRow[0]) && leftRow[1].equals(rightRow[1]);
                if (areFirstTwoColumnsAligned) {
                    totalAligned++;
                }
                boolean isGreen = areFirstTwoColumnsAligned && leftRow.length > 2 && rightRow.length > 2 && leftRow[2].equals(rightRow[2]);
                if (isGreen) {
                    greenCount++;
                } else if (areFirstTwoColumnsAligned) {
                    nonGreenCount++;
                }
                if (leftModel != null && rightModel != null) {
                    int columnCount = Math.max(Math.max(leftRow.length, rightRow.length), Math.max(leftModel.getColumnCount(), rightModel.getColumnCount()));
                    addRowWithDiffHighlight(leftModel, leftRow, rightModel, rightRow, columnCount);
                }
            }
        }
        return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
    }

    AlignmentResult alignRowsByThreeColumns(DefaultTableModel leftModel, DefaultTableModel rightModel, List<String[]> leftCsvData, List<String[]> rightCsvData) {
        Map<String, List<String[]>> leftMap = new LinkedHashMap<>();
        Map<String, List<String[]>> rightMap = new LinkedHashMap<>();
        for (int i = 1; i < leftCsvData.size(); i++) {
            String[] row = leftCsvData.get(i);
            String key = getThreeColumnKey(row);
            leftMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (int i = 1; i < rightCsvData.size(); i++) {
            String[] row = rightCsvData.get(i);
            String key = getThreeColumnKey(row);
            rightMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        Set<String> allKeys = new LinkedHashSet<>(leftMap.keySet());
        allKeys.addAll(rightMap.keySet());
        int totalAligned = 0;
        int greenCount = 0;
        int nonGreenCount = 0;
        for (String key : allKeys) {
            List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
            List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());
            int maxRows = Math.max(leftRows.size(), rightRows.size());
            for (int i = 0; i < maxRows; i++) {
                String[] leftRow = i < leftRows.size() ? leftRows.get(i) : new String[0];
                String[] rightRow = i < rightRows.size() ? rightRows.get(i) : new String[0];
                boolean leftHasColumns = leftRow.length >= 3;
                boolean rightHasColumns = rightRow.length >= 3;
                boolean areThreeColumnsAligned = leftHasColumns && rightHasColumns && leftRow[0].equals(rightRow[0]) && leftRow[1].equals(rightRow[1]) && leftRow[2].equals(rightRow[2]);
                if (areThreeColumnsAligned) {
                    totalAligned++;
                }
                boolean isGreen = areThreeColumnsAligned && Arrays.equals(leftRow, rightRow);
                if (isGreen) {
                    greenCount++;
                } else if (areThreeColumnsAligned) {
                    nonGreenCount++;
                }
                if (leftModel != null && rightModel != null) {
                    int columnCount = Math.max(Math.max(leftRow.length, rightRow.length), Math.max(leftModel.getColumnCount(), rightModel.getColumnCount()));
                    addRowWithDiffHighlight(leftModel, leftRow, rightModel, rightRow, columnCount);
                }
            }
        }
        return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
    }

    private void alignRowsByFourColumns(DefaultTableModel leftModel, DefaultTableModel rightModel, List<String[]> leftCsvData, List<String[]> rightCsvData) {
        Map<String, List<String[]>> leftMap = new LinkedHashMap<>();
        Map<String, List<String[]>> rightMap = new LinkedHashMap<>();
        for (int i = 1; i < leftCsvData.size(); i++) {
            String[] row = leftCsvData.get(i);
            String key = getFourColumnKey(row);
            leftMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (int i = 1; i < rightCsvData.size(); i++) {
            String[] row = rightCsvData.get(i);
            String key = getFourColumnKey(row);
            rightMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        Set<String> allKeys = new LinkedHashSet<>(leftMap.keySet());
        allKeys.addAll(rightMap.keySet());
        for (String key : allKeys) {
            List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
            List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());
            int maxRows = Math.max(leftRows.size(), rightRows.size());
            for (int i = 0; i < maxRows; i++) {
                String[] leftRow = i < leftRows.size() ? leftRows.get(i) : new String[0];
                String[] rightRow = i < rightRows.size() ? rightRows.get(i) : new String[0];
                int columnCount = Math.max(Math.max(leftRow.length, rightRow.length), Math.max(leftModel.getColumnCount(), rightModel.getColumnCount()));
                addRowWithDiffHighlight(leftModel, leftRow, rightModel, rightRow, columnCount);
            }
        }
    }

    AlignmentResult alignRowsBySixColumns(DefaultTableModel leftModel, DefaultTableModel rightModel, List<String[]> leftCsvData, List<String[]> rightCsvData) {
        Map<String, List<String[]>> leftMap = new LinkedHashMap<>();
        Map<String, List<String[]>> rightMap = new LinkedHashMap<>();
        for (int i = 1; i < leftCsvData.size(); i++) {
            String[] row = leftCsvData.get(i);
            String key = getSixColumnKey(row);
            leftMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (int i = 1; i < rightCsvData.size(); i++) {
            String[] row = rightCsvData.get(i);
            String key = getSixColumnKey(row);
            rightMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        Set<String> allKeys = new LinkedHashSet<>(leftMap.keySet());
        allKeys.addAll(rightMap.keySet());
        int totalAligned = 0;
        int greenCount = 0;
        int nonGreenCount = 0;
        for (String key : allKeys) {
            List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
            List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());
            int maxRows = Math.max(leftRows.size(), rightRows.size());
            for (int i = 0; i < maxRows; i++) {
                String[] leftRow = i < leftRows.size() ? leftRows.get(i) : new String[0];
                String[] rightRow = i < rightRows.size() ? rightRows.get(i) : new String[0];
                boolean areAdicionalesEqual = compareAdicionales(leftRow, rightRow);
                boolean isGreen = areAdicionalesEqual && Arrays.equals(leftRow, rightRow);
                if (isGreen) {
                    greenCount++;
                } else if (areAdicionalesEqual) {
                    nonGreenCount++;
                }
                if (areAdicionalesEqual) {
                    totalAligned++;
                }
                int columnCount = Math.max(Math.max(leftRow.length, rightRow.length), Math.max(leftModel.getColumnCount(), rightModel.getColumnCount()));
                addRowWithDiffHighlight(leftModel, leftRow, rightModel, rightRow, columnCount);
            }
        }
        return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
    }

    private void defaultComparison(DefaultTableModel leftModel, DefaultTableModel rightModel, List<String[]> leftCsvData, List<String[]> rightCsvData, List<String> headersLeft, List<String> headersRight) {
        Set<String> processedKeys = new HashSet<>();
        for (int i = 1; i < leftCsvData.size(); i++) {
            String[] leftRow = leftCsvData.get(i);
            String leftKey = getComparisonKey(leftRow, leftRow.length);
            boolean matchFound = false;
            for (int j = 1; j < rightCsvData.size(); j++) {
                String[] rightRow = rightCsvData.get(j);
                String rightKey = getComparisonKey(rightRow, rightRow.length);
                if (leftKey.equals(rightKey) && !processedKeys.contains(rightKey)) {
                    processedKeys.add(rightKey);
                    addRowWithDiffHighlight(leftModel, leftRow, rightModel, rightRow, headersLeft.size());
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                addRowWithDiffHighlight(leftModel, leftRow, rightModel, new String[headersRight.size()], headersLeft.size());
            }
        }
        for (int j = 1; j < rightCsvData.size(); j++) {
            String[] rightRow = rightCsvData.get(j);
            String rightKey = getComparisonKey(rightRow, rightRow.length);
            if (!processedKeys.contains(rightKey)) {
                addRowWithDiffHighlight(leftModel, new String[headersLeft.size()], rightModel, rightRow, headersRight.size());
            }
        }
    }

    private String getTwoColumnKey(String[] row) {
        if (row.length < 2) {
            return "";
        }
        String firstColumn = row[0] != null ? row[0] : "";
        String secondColumn = row[1] != null ? row[1] : "";
        return firstColumn + "," + secondColumn;
    }

    private String getThreeColumnKey(String[] row) {
        if (row.length < 3) {
            return "";
        }
        String firstColumn = row[0] != null ? row[0] : "";
        String secondColumn = row[1] != null ? row[1] : "";
        String thirdColumn = row[2] != null ? row[2] : "";
        return firstColumn + "," + secondColumn + "," + thirdColumn;
    }

    private String getFourColumnKey(String[] row) {
        if (row.length < 4) {
            return "";
        }
        String firstColumn = row[0] != null ? row[0] : "";
        String secondColumn = row[1] != null ? row[1] : "";
        String thirdColumn = row[2] != null ? row[2] : "";
        String fourthColumn = row[3] != null ? row[3] : "";
        return firstColumn + "," + secondColumn + "," + thirdColumn + "," + fourthColumn;
    }

    private String getSixColumnKey(String[] row) {
        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (i < row.length) {
                if (i == 5) {
                    String[] adicionales = row[i].split("/");
                    Arrays.sort(adicionales);
                    keyBuilder.append(String.join("/", adicionales));
                } else {
                    keyBuilder.append(row[i] != null ? row[i] : "");
                }
            }
            keyBuilder.append("|");
        }
        return keyBuilder.toString();
    }

    private boolean compareAdicionales(String[] leftRow, String[] rightRow) {
        if (leftRow.length <= 5 || rightRow.length <= 5) {
            return false;
        }
        String[] leftAdicionales = leftRow[5].split("/");
        String[] rightAdicionales = rightRow[5].split("/");
        Arrays.sort(leftAdicionales);
        Arrays.sort(rightAdicionales);
        return Arrays.equals(leftAdicionales, rightAdicionales);
    }

    private static String getString(String dir, String fileName, Path dirPath) throws IOException {
        if (!dirPath.toFile().isDirectory()) {
            throw new IOException("Directorio no valido: " + dir);
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IOException("Nombre de archivo no valido: " + fileName);
        }
        Path filePath = dirPath.resolve(fileName).normalize().toAbsolutePath();
        if (!filePath.startsWith(dirPath)) {
            throw new IOException("Ruta no valida: " + filePath);
        }
        return filePath.toFile().getCanonicalPath();
    }

    private String getComparisonKey(String[] fields, int length) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                key.append(",");
            }
            key.append(fields[i]);
        }
        return key.toString();
    }

    private void addRowWithDiffHighlight(DefaultTableModel leftModel, String[] leftRow, DefaultTableModel rightModel, String[] rightRow, int columnCount) {
        if (isRowEmpty(leftRow) && isRowEmpty(rightRow)) {
            return;
        }

        columnCount = Math.max(leftRow.length, rightRow.length);
        String[] leftData = new String[columnCount];
        String[] rightData = new String[columnCount];
        boolean rowDiff = false;

        for (int i = 0; i < columnCount; i++) {
            leftData[i] = i < leftRow.length && leftRow[i] != null ? leftRow[i] : "";
            rightData[i] = i < rightRow.length && rightRow[i] != null ? rightRow[i] : "";

            if (!Objects.equals(leftData[i].trim(), rightData[i].trim())) {
                diffColumns.add(i);
                rowDiff = true;
            }

        }
        leftModel.addRow(leftData);
        rightModel.addRow(rightData);

        if (rowDiff) {
            diffRows.add(leftModel.getRowCount() - 1);
        }
    }

    private void identifyRowTypes(DefaultTableModel leftModel, DefaultTableModel rightModel) {
        matchingRows.clear();
        uniqueLeftRows.clear();
        uniqueRightRows.clear();
        int rowCountLeft = leftModel.getRowCount();
        int rowCountRight = rightModel.getRowCount();
        boolean[] matchedRightRows = new boolean[rowCountRight];
        Arrays.fill(matchedRightRows, false);
        for (int i = 0; i < rowCountLeft; i++) {
            boolean matchFound = false;
            Object[] leftRow = ((Vector<?>) leftModel.getDataVector().get(i)).toArray();
            if (isRowEmpty(leftRow)) {
                continue;
            }
            for (int j = 0; j < rowCountRight; j++) {
                Object[] rightRow = ((Vector<?>) rightModel.getDataVector().get(j)).toArray();
                if (isRowEmpty(rightRow)) {
                    continue;
                }
                if (areRowsEqual(leftRow, rightRow)) {
                    matchFound = true;
                    matchingRows.add(i);
                    matchedRightRows[j] = true;
                    break;
                }
            }
            if (!matchFound) {
                uniqueLeftRows.add(i);
            }
        }
        for (int j = 0; j < rowCountRight; j++) {
            if (!matchedRightRows[j]) {
                Object[] rightRow = ((Vector<?>) rightModel.getDataVector().get(j)).toArray();
                if (!isRowEmpty(rightRow)) {
                    uniqueRightRows.add(j);
                }
            }
        }
    }

    private List<String[]> sortCsvByFirstColumn(List<String[]> csvData) {
        List<String[]> sortedData = new ArrayList<>(csvData);
        String[] headers = sortedData.remove(0);
        sortedData.sort(Comparator.comparing(row -> row[0] != null ? row[0] : ""));
        sortedData.add(0, headers);
        return sortedData;
    }

    private boolean isRowEmpty(Object[] row) {
        for (Object cell : row) {
            if (cell != null && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean areRowsEqual(Object[] leftRow, Object[] rightRow) {
        if (leftRow.length != rightRow.length) return false;
        for (int i = 0; i < leftRow.length; i++) {
            String leftValue = leftRow[i] != null ? leftRow[i].toString().trim() : "";
            String rightValue = rightRow[i] != null ? rightRow[i].toString().trim() : "";
            if (!leftValue.equals(rightValue)) {
                return false;
            }
        }
        return true;
    }

    private void applyRowColors() {
        leftTable.setDefaultRenderer(Object.class, new RowColorRenderer(true));
        rightTable.setDefaultRenderer(Object.class, new RowColorRenderer(false));
    }
    
    
    private String[] normalizeRow(String[] row) {
        return Arrays.stream(row)
                .map(value -> value != null ? value.trim().replaceAll("\\s+", " ") : "")
                .toArray(String[]::new);
    }   
    
    
    private void populateMap(List<String[]> csvData, Map<String, List<String[]>> map, String sourceName) {
        for (int i = 0; i < csvData.size(); i++) {
            String[] row = normalizeRow(csvData.get(i));

            if (row.length == 0 || row[0].isEmpty()) {
                log.warn("{}: Fila vacía detectada en índice {} y se omitió", sourceName, i);
                continue;
            }

            String key = row[0].trim();
            if (key.isEmpty()) {
                log.warn("{}: Fila en índice {} tiene una clave vacía y podría no emparejarse correctamente: {}",
                        sourceName, i, Arrays.toString(row));
            }

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
    }
    
    
    private class RowColorRenderer extends DefaultTableCellRenderer {
        private boolean isLeftTable;

        public RowColorRenderer(boolean isLeftTable) {
            this.isLeftTable = isLeftTable;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            } else {
                if (isLeftTable) {
                    if (matchingRows.contains(row)) {
                        c.setBackground(new Color(204, 255, 204, 192));
                    } else if (uniqueLeftRows.contains(row)) {
                        c.setBackground(new Color(255, 255, 153, 197));
                    } else {
                        c.setBackground(new Color(52, 51, 51, 73));
                    }
                } else {
                    if (matchingRows.contains(row)) {
                        c.setBackground(new Color(204, 255, 204, 192));
                    } else if (uniqueRightRows.contains(row)) {
                        c.setBackground(new Color(173, 216, 230, 189));
                    } else {
                        c.setBackground(new Color(52, 51, 51, 73));
                    }
                }
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
}
