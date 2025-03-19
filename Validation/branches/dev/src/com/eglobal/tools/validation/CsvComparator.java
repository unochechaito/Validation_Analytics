/**
 * Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
 * es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
 * o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
 * identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
 * de correo seginfo@eglobal.com.mx                                                         *
 **/
package com.eglobal.tools.validation;

import com.eglobal.tools.validation.utils.CsvUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

public class CsvComparator {
	@java.lang.SuppressWarnings("all")
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(CsvComparator.class);

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

		List<String[]> leftCsvData;
		List<String[]> rightCsvData;

		leftCsvData = CsvUtils.readCsv(leftFullFilePath);
		rightCsvData = CsvUtils.readCsv(rightFullFilePath);

		boolean leftEmpty = leftCsvData.isEmpty();
		boolean rightEmpty = rightCsvData.isEmpty();

		if (leftEmpty && rightEmpty) {
			log.warn("Ambos archivos CSV estan vacios o no existen. Mostrando mensaje en la tabla.");
			showNoDataMessage(leftModel, rightModel, "Ambos archivos estan vacios o no existen.");
			applyNoDataRenderer();
			return;
		}

		if (leftEmpty || rightEmpty) {
			String missingSide = leftEmpty ? "A comparar" : "Base";
			log.warn("Solo el archivo {} existe, no se puede realizar la comparacion.",
					leftEmpty ? "Base" : "A comparar");
			showNoDataMessage(leftModel, rightModel,
					"Solo el archivo " + missingSide + " existe. No se puede realizar la comparacion.");
			applyNoDataRenderer();
			return;
		}

		if (!leftEmpty || !rightEmpty) {
			boolean leftHasMessageColumn = hasMessageColumn(leftModel);
			boolean rightHasMessageColumn = hasMessageColumn(rightModel);

			List<String> headersLeft = leftEmpty
					? (leftHasMessageColumn ? Collections.emptyList() : Collections.singletonList("Mensaje"))
					: Arrays.asList(leftCsvData.get(0));
			List<String> headersRight = rightEmpty
					? (rightHasMessageColumn ? Collections.emptyList() : Collections.singletonList("Mensaje"))
					: Arrays.asList(rightCsvData.get(0));

			for (String header : headersLeft) {
				leftModel.addColumn(leftEmpty ? header : header + " (Base)");
			}
			for (String header : headersRight) {
				rightModel.addColumn(rightEmpty ? header : header + " (A comparar)");
			}

			boolean processedLeft = false;
			boolean processedRight = false;

			if (!leftEmpty && !rightEmpty) {
				processAlignment(photoFileName, leftModel, rightModel, leftCsvData, rightCsvData, headersLeft,
						headersRight);
				processedLeft = processedRight = true;
			} else if (!leftEmpty) {
				processAlignment(photoFileName, leftModel, null, leftCsvData, null, headersLeft, null);
				processedLeft = true;
			} else {
				processAlignment(photoFileName, null, rightModel, null, rightCsvData, null, headersRight);
				processedRight = true;
			}

			if (processedLeft && processedRight) {
				identifyRowTypes(leftModel, rightModel);
			} else if (processedLeft) {
				identifyRowTypes(leftModel, null);
			} else {
				identifyRowTypes(null, rightModel);
			}

			if (processedLeft) {
				leftTable.setDefaultRenderer(Object.class, new RowColorRenderer(true));
			}
			if (processedRight) {
				rightTable.setDefaultRenderer(Object.class, new RowColorRenderer(false));
			}

			applyRowColors();
			leftTable.repaint();
			rightTable.repaint();
		}
		applyNoDataRenderer();
	}

	private void applyNoDataRenderer() {
		if (isOnlyMessage(leftTable)) {
			leftTable.setDefaultRenderer(Object.class, new NoDataRenderer());
			adjustNoDataColumnWidth(leftTable);
		}
		if (isOnlyMessage(rightTable)) {
			rightTable.setDefaultRenderer(Object.class, new NoDataRenderer());
			adjustNoDataColumnWidth(rightTable);
		}
	}

	private void adjustNoDataColumnWidth(JTable table) {
		if (table.getColumnCount() == 0)
			return;

		TableColumn column = table.getColumnModel().getColumn(0);
		FontMetrics fontMetrics = table.getFontMetrics(table.getFont());

		int tableWidth = table.getParent().getWidth();
		int preferredWidth = fontMetrics
				.stringWidth("Solo el archivo Base existe. No se puede realizar la comparación.") + 40;

		preferredWidth = Math.max(preferredWidth, tableWidth - 20);

		column.setPreferredWidth(preferredWidth);
		column.setMinWidth(preferredWidth);
		column.setMaxWidth(preferredWidth);

		table.setPreferredScrollableViewportSize(
				new Dimension(preferredWidth, table.getRowHeight() * table.getRowCount()));

		table.revalidate();
		table.repaint();
	}

	private boolean isOnlyMessage(JTable table) {
		return table.getColumnCount() == 1 && "Mensaje".equals(table.getColumnName(0));
	}

	private void processAlignment(String photoFileName, DefaultTableModel leftModel, DefaultTableModel rightModel,
			List<String[]> leftCsvData, List<String[]> rightCsvData, List<String> headersLeft,
			List<String> headersRight) {

		if (leftCsvData == null) {
			leftCsvData = new ArrayList<>();
		}
		if (rightCsvData == null) {
			rightCsvData = new ArrayList<>();
		}

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

		List<String> oneColumnsKeys = Arrays.asList("Porcentaje de ON2 DESC",
				"Campos presentes por tipo de transaccion recibidos de adquirente Visa",
				"Campos presentes por tipo de transaccion recibidos de adquirente MasterCard",
				"Campos presentes por tipo de transaccion enviados al emisor Visa",
				"Campos presentes por tipo de transaccion enviados al emisor MasterCard",
				"Campos presentes respondidos por tipo de transaccion al adquirente Visa",
				"Campos presentes respondidos por tipo de transaccion al adquirente Mastercard",
				"Campos presentes por tipo de transaccion respondidos por el emisor Visa",
				"Campos presentes por tipo de transaccion respondidos por el emisor MasterCard",
				"Tags emv por tipo de transaccion recibidos de adquirente Visa",
				"Tags emv por tipo de transaccion recibidos de adquirente MasterCard",
				"Tags emv por tipo de transaccion enviados al emisor Visa",
				"Tags emv por tipo de transaccion enviados al emisor MasterCard");
		if (oneColumnsKeys.contains(photoFileName) && leftModel != null
				&& leftModel.findColumn("Numero de transacciones") == -1) {
			leftModel.addColumn("Numero de transacciones");
		}
		if (oneColumnsKeys.contains(photoFileName) && rightModel != null
				&& rightModel.findColumn("Numero de transacciones") == -1) {
			rightModel.addColumn("Numero de transacciones");
		}
		for (String key : oneColumnsKeys) {
			List<String[]> finalLeftCsvData = leftCsvData;
			List<String[]> finalRightCsvData = rightCsvData;
			alignmentStrategies.put(key, () -> alignRowsByFirstColumn(photoFileName, leftModel, rightModel,
					finalLeftCsvData, finalRightCsvData));
		}

		List<String> twoColumnsKeys = Arrays.asList("Porcentaje de ON2 por BIN",
				"Porcentaje de ON2 por Dispatcher Adquirente", "Mapeo MTI-ProcCode a TranCode",
				"Mapeo de codigos de error C39 emisor, on2 y C39 adquierente", "Codigos de C39 por Dispatcher emisor",
				"C44 respondido por el emisor Visa por operativa", "Mapeo MTI-ProcCode a TranCode",
				"Campos presentes por tipo de transaccion recibidos de adquirentes Nacional",
				"Campos presentes por tipo de transaccion enviados a emisores Nacional",
				"Campos presentes por tipo de transaccion respondidos por emisores Nacional",
				"Campos presentes por tipo de transaccion respondidos a adquirentes Nacional",
				"Tokens presentes por tipo de transaccion recibidos de adquirentes Nacional",
				"Tokens presentes por tipo de transaccion enviados a emisores Nacional",
				"Tokens presentes por tipo de transaccion respondidos a adquirentes Nacional",
				"Tokens presentes por tipo de transaccion respondidos de emisores Nacional");
		if (oneColumnsKeys.contains(photoFileName) && leftModel != null
				&& leftModel.findColumn("Numero de transacciones") == -1) {
			leftModel.addColumn("Numero de transacciones");
		}
		if (oneColumnsKeys.contains(photoFileName) && rightModel != null
				&& rightModel.findColumn("Numero de transacciones") == -1) {
			rightModel.addColumn("Numero de transacciones");
		}
		for (String key : twoColumnsKeys) {
			List<String[]> finalLeftCsvData1 = leftCsvData;
			List<String[]> finalRightCsvData1 = rightCsvData;
			alignmentStrategies.put(key,
					() -> alignRowsByTwoColumns(leftModel, rightModel, finalLeftCsvData1, finalRightCsvData1));
		}

		List<String> threeColumnsKeys = Arrays.asList(
				"Tipo de transacciones declinadas por el emisor y aprobadas al adquirente",
				"C44 respondido al adquirente Visa por operativa");
		if (oneColumnsKeys.contains(photoFileName) && leftModel != null
				&& leftModel.findColumn("Numero de transacciones") == -1) {
			leftModel.addColumn("Numero de transacciones");
		}
		if (oneColumnsKeys.contains(photoFileName) && rightModel != null
				&& rightModel.findColumn("Numero de transacciones") == -1) {
			rightModel.addColumn("Numero de transacciones");
		}
		for (String key : threeColumnsKeys) {
			List<String[]> finalLeftCsvData2 = leftCsvData;
			List<String[]> finalRightCsvData2 = rightCsvData;
			alignmentStrategies.put(key,
					() -> alignRowsByThreeColumns(leftModel, rightModel, finalLeftCsvData2, finalRightCsvData2));
		}

		List<String> fourColumnsKeys = Collections
				.singletonList("Indicadores en el desc de Tipo de Cambio por flujo adquirente internacional a emisor");
		if (oneColumnsKeys.contains(photoFileName) && leftModel != null
				&& leftModel.findColumn("Numero de transacciones") == -1) {
			leftModel.addColumn("Numero de transacciones");
		}
		if (oneColumnsKeys.contains(photoFileName) && rightModel != null
				&& rightModel.findColumn("Numero de transacciones") == -1) {
			rightModel.addColumn("Numero de transacciones");
		}
		for (String key : fourColumnsKeys) {
			List<String[]> finalLeftCsvData3 = leftCsvData;
			List<String[]> finalRightCsvData3 = rightCsvData;
			alignmentStrategies.put(key,
					() -> alignRowsByFourColumns(leftModel, rightModel, finalLeftCsvData3, finalRightCsvData3));
		}

		List<String> sixColumnsKeys = Collections
				.singletonList("Adicionales por tipo de transaccion plataforma de adquirente y emisor");
		if (oneColumnsKeys.contains(photoFileName) && leftModel != null
				&& leftModel.findColumn("Numero de transacciones") == -1) {
			leftModel.addColumn("Numero de transacciones");
		}
		if (oneColumnsKeys.contains(photoFileName) && rightModel != null
				&& rightModel.findColumn("Numero de transacciones") == -1) {
			rightModel.addColumn("Numero de transacciones");
		}
		for (String key : sixColumnsKeys) {
			List<String[]> finalLeftCsvData4 = leftCsvData;
			List<String[]> finalRightCsvData4 = rightCsvData;
			alignmentStrategies.put(key,
					() -> alignRowsBySixColumns(leftModel, rightModel, finalLeftCsvData4, finalRightCsvData4));
		}

		List<String[]> finalLeftCsvData5 = leftCsvData;
		List<String[]> finalRightCsvData5 = rightCsvData;
		alignmentStrategies.getOrDefault(photoFileName, () -> {
			if (leftModel != null && rightModel != null) {
				defaultComparison(leftModel, rightModel, finalLeftCsvData5, finalRightCsvData5, headersLeft,
						headersRight);
			}
		}).run();
	}

	AlignmentResult alignRowsByFirstColumn(String photoName, DefaultTableModel leftModel, DefaultTableModel rightModel,
			List<String[]> leftCsvData, List<String[]> rightCsvData) {

		Map<String, List<String[]>> leftMap = new LinkedHashMap<>();
		Map<String, List<String[]>> rightMap = new LinkedHashMap<>();

		populateMap(leftCsvData, leftMap, "LEFT");
		populateMap(rightCsvData, rightMap, "RIGHT");

		Set<String> allKeys = new LinkedHashSet<>(leftMap.keySet());
		allKeys.addAll(rightMap.keySet());

		int totalAligned = 0;
		int greenCount = 0;
		int nonGreenCount = 0;

		final int CANTIDAD_OCURRENCIAS_INDEX = 1;
		boolean useFixedIndex = "Porcentaje de ON2 DESC".equals(photoName);

		Comparator<String[]> rowComparator = (row1, row2) -> {
			int minLength = Math.min(row1.length, row2.length);
			for (int i = 0; i < minLength; i++) {
				int cmp = row1[i].compareTo(row2[i]);
				if (cmp != 0) {
					return cmp;
				}
			}
			return Integer.compare(row1.length, row2.length);
		};

		for (String key : allKeys) {
			List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
			List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());

			leftRows.sort(rowComparator);
			rightRows.sort(rowComparator);

			boolean[] matchedRightRows = new boolean[rightRows.size()];

			for (String[] leftRow : leftRows) {
				boolean matchFound = false;

				for (int j = 0; j < rightRows.size(); j++) {
					if (!matchedRightRows[j]) {
						String[] rightRow = rightRows.get(j);

						if (Arrays.equals(leftRow, rightRow)) {
							matchedRightRows[j] = true;
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
					addRowWithDiffHighlight(leftModel, new String[rightRows.get(j).length], rightModel,
							rightRows.get(j), rightRows.get(j).length);
				}
			}
		}

		if (useFixedIndex) {
			applyCustomTransactionCount(leftModel, CANTIDAD_OCURRENCIAS_INDEX);
			applyCustomTransactionCount(rightModel, CANTIDAD_OCURRENCIAS_INDEX);
		} else {
			populateTransactionCount(leftModel);
			populateTransactionCount(rightModel);
		}

		return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
	}

	AlignmentResult alignRowsByTwoColumns(DefaultTableModel leftModel, DefaultTableModel rightModel,
			List<String[]> leftCsvData, List<String[]> rightCsvData) {

		Map<String, List<String[]>> leftMap = new LinkedHashMap<>();
		Map<String, List<String[]>> rightMap = new LinkedHashMap<>();

		for (String[] row : leftCsvData) {
			String key = getTwoColumnKey(row);
			leftMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
		}

		for (String[] row : rightCsvData) {
			String key = getTwoColumnKey(row);
			rightMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
		}

		Set<String> allKeys = new LinkedHashSet<>(leftMap.keySet());
		allKeys.addAll(rightMap.keySet());

		int totalAligned = 0;
		int greenCount = 0;
		int nonGreenCount = 0;

		Comparator<String[]> rowComparator = (row1, row2) -> {
			int minLength = Math.min(row1.length, row2.length);
			for (int i = 0; i < minLength; i++) {
				int cmp = row1[i].compareTo(row2[i]);
				if (cmp != 0) {
					return cmp;
				}
			}
			return Integer.compare(row1.length, row2.length);
		};

		for (String key : allKeys) {
			List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
			List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());

			leftRows.sort(rowComparator);
			rightRows.sort(rowComparator);

			boolean[] matchedRightRows = new boolean[rightRows.size()];

			log.info("Procesando clave: {}, Filas en izquierda: {}, Filas en derecha: {}", key, leftRows.size(),
					rightRows.size());

			for (String[] leftRow : leftRows) {
				boolean matchFound = false;

				for (int j = 0; j < rightRows.size(); j++) {
					if (!matchedRightRows[j]) {
						String[] rightRow = rightRows.get(j);

						if (Arrays.equals(leftRow, rightRow)) {
							matchedRightRows[j] = true;
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
					addRowWithDiffHighlight(leftModel, new String[rightRows.get(j).length], rightModel,
							rightRows.get(j), rightRows.get(j).length);
				}
			}
		}

		populateTransactionCount(leftModel);
		populateTransactionCount(rightModel);

		return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
	}

	AlignmentResult alignRowsByThreeColumns(DefaultTableModel leftModel, DefaultTableModel rightModel,
			List<String[]> leftCsvData, List<String[]> rightCsvData) {
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

		Comparator<String[]> rowComparator = (row1, row2) -> {
			int minLength = Math.min(row1.length, row2.length);
			for (int i = 0; i < minLength; i++) {
				int cmp = row1[i].compareTo(row2[i]);
				if (cmp != 0) {
					return cmp;
				}
			}
			return Integer.compare(row1.length, row2.length);
		};

		for (String key : allKeys) {
			List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
			List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());

			leftRows.sort(rowComparator);
			rightRows.sort(rowComparator);

			boolean[] matchedRightRows = new boolean[rightRows.size()];

			log.info("Procesando clave: {}, Filas en izquierda: {}, Filas en derecha: {}", key, leftRows.size(),
					rightRows.size());

			for (String[] leftRow : leftRows) {
				boolean matchFound = false;

				for (int j = 0; j < rightRows.size(); j++) {
					if (!matchedRightRows[j]) {
						String[] rightRow = rightRows.get(j);

						if (Arrays.equals(leftRow, rightRow)) {
							matchedRightRows[j] = true;
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
					addRowWithDiffHighlight(leftModel, new String[rightRows.get(j).length], rightModel,
							rightRows.get(j), rightRows.get(j).length);
				}
			}
		}
		populateTransactionCount(leftModel);
		populateTransactionCount(rightModel);

		return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
	}

	private void alignRowsByFourColumns(DefaultTableModel leftModel, DefaultTableModel rightModel,
			List<String[]> leftCsvData, List<String[]> rightCsvData) {
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

		Comparator<String[]> rowComparator = (row1, row2) -> {
			int minLength = Math.min(row1.length, row2.length);
			for (int i = 0; i < minLength; i++) {
				int cmp = row1[i].compareTo(row2[i]);
				if (cmp != 0) {
					return cmp;
				}
			}
			return Integer.compare(row1.length, row2.length);
		};

		for (String key : allKeys) {
			List<String[]> leftRows = leftMap.getOrDefault(key, new ArrayList<>());
			List<String[]> rightRows = rightMap.getOrDefault(key, new ArrayList<>());

			leftRows.sort(rowComparator);
			rightRows.sort(rowComparator);

			boolean[] matchedRightRows = new boolean[rightRows.size()];

			for (String[] leftRow : leftRows) {
				boolean matchFound = false;

				for (int j = 0; j < rightRows.size(); j++) {
					if (!matchedRightRows[j]) {
						String[] rightRow = rightRows.get(j);

						if (Arrays.equals(leftRow, rightRow)) {
							matchedRightRows[j] = true;
							matchFound = true;

							addRowWithDiffHighlight(leftModel, leftRow, rightModel, rightRow, leftRow.length);
							break;
						}
					}
				}
				if (!matchFound) {
					addRowWithDiffHighlight(leftModel, leftRow, rightModel, new String[leftRow.length], leftRow.length);
				}
			}

			for (int j = 0; j < rightRows.size(); j++) {
				if (!matchedRightRows[j]) {
					addRowWithDiffHighlight(leftModel, new String[rightRows.get(j).length], rightModel,
							rightRows.get(j), rightRows.get(j).length);
				}
			}
		}
		populateTransactionCount(leftModel);
		populateTransactionCount(rightModel);
	}

	AlignmentResult alignRowsBySixColumns(DefaultTableModel leftModel, DefaultTableModel rightModel,
			List<String[]> leftCsvData, List<String[]> rightCsvData) {
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

		List<String[]> allRows = new ArrayList<>();
		allRows.addAll(leftCsvData);
		allRows.addAll(rightCsvData);

		Comparator<String[]> rowComparator = (row1, row2) -> {
			int minLength = Math.min(row1.length, row2.length);
			for (int i = 0; i < minLength; i++) {
				int cmp = row1[i].compareTo(row2[i]);
				if (cmp != 0) {
					return cmp;
				}
			}
			return Integer.compare(row1.length, row2.length);
		};

		allRows.sort(rowComparator);

		boolean[] matchedRightRows = new boolean[rightCsvData.size()];

		int totalAligned = 0;
		int greenCount = 0;
		int nonGreenCount = 0;

		for (String[] leftRow : leftCsvData) {
			boolean matchFound = false;

			for (int j = 0; j < rightCsvData.size(); j++) {
				if (!matchedRightRows[j]) {
					String[] rightRow = rightCsvData.get(j);

					if (Arrays.equals(leftRow, rightRow)) {
						matchedRightRows[j] = true;
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

		for (int j = 0; j < rightCsvData.size(); j++) {
			if (!matchedRightRows[j]) {
				addRowWithDiffHighlight(leftModel, new String[rightCsvData.get(j).length], rightModel,
						rightCsvData.get(j), rightCsvData.get(j).length);
			}
		}
		populateTransactionCount(leftModel);
		populateTransactionCount(rightModel);
		return new AlignmentResult(totalAligned, greenCount, nonGreenCount);
	}

	private void defaultComparison(DefaultTableModel leftModel, DefaultTableModel rightModel,
			List<String[]> leftCsvData, List<String[]> rightCsvData, List<String> headersLeft,
			List<String> headersRight) {
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
				addRowWithDiffHighlight(leftModel, leftRow, rightModel, new String[headersRight.size()],
						headersLeft.size());
			}
		}
		for (int j = 1; j < rightCsvData.size(); j++) {
			String[] rightRow = rightCsvData.get(j);
			String rightKey = getComparisonKey(rightRow, rightRow.length);
			if (!processedKeys.contains(rightKey)) {
				addRowWithDiffHighlight(leftModel, new String[headersLeft.size()], rightModel, rightRow,
						headersRight.size());
			}
		}
	}

	private void applyCustomTransactionCount(DefaultTableModel model, int columnIndex) {
		if (model == null)
			return;

		int transaccionesColumnIndex = model.findColumn("Numero de transacciones");

		if (transaccionesColumnIndex == -1) {
			model.addColumn("Numero de transacciones");
			transaccionesColumnIndex = model.getColumnCount() - 1;
		}

		for (int i = 0; i < model.getRowCount(); i++) {
			boolean isEmptyRow = true;

			for (int j = 0; j < model.getColumnCount() - 1; j++) {
				Object cellValue = model.getValueAt(i, j);
				if (cellValue != null && !cellValue.toString().trim().isEmpty()) {
					isEmptyRow = false;
					break;
				}
			}

			if (!isEmptyRow) {
				Object value = model.getValueAt(i, columnIndex);
				if (value instanceof String && ((String) value).matches("\\d+")) {
					model.setValueAt(value, i, transaccionesColumnIndex);
				}
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

	private void populateTransactionCount(DefaultTableModel model) {
		if (model == null || model.getColumnCount() == 0) {
			return;
		}

		int columnIndex = model.findColumn("Numero de transacciones");
		if (columnIndex == -1) {
			model.addColumn("Numero de transacciones");
			columnIndex = model.getColumnCount() - 1;
		}

		Map<String, Integer> rowCountMap = new LinkedHashMap<>();

		for (int i = 0; i < model.getRowCount(); i++) {
			StringBuilder keyBuilder = new StringBuilder();
			boolean isEmptyRow = true;

			for (int j = 0; j < model.getColumnCount() - 1; j++) { // Excluyendo la última columna
				Object value = model.getValueAt(i, j);
				String cellValue = (value != null) ? value.toString().trim() : "";
				keyBuilder.append(cellValue).append("|");
				if (!cellValue.isEmpty()) {
					isEmptyRow = false;
				}
			}

			if (!isEmptyRow) {
				String key = keyBuilder.toString();
				rowCountMap.put(key, rowCountMap.getOrDefault(key, 0) + 1);
			}
		}

		for (int i = 0; i < model.getRowCount(); i++) {
			StringBuilder keyBuilder = new StringBuilder();
			boolean isEmptyRow = true;

			for (int j = 0; j < model.getColumnCount() - 1; j++) {
				Object value = model.getValueAt(i, j);
				String cellValue = (value != null) ? value.toString().trim() : "";
				keyBuilder.append(cellValue).append("|");
				if (!cellValue.isEmpty()) {
					isEmptyRow = false;
				}
			}

			if (!isEmptyRow) {
				String key = keyBuilder.toString();
				model.setValueAt(rowCountMap.get(key), i, columnIndex);
			}
		}
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
		if (fields.length == 0)
			return "";

		int keyLength = (fields.length > 1) ? fields.length - 1 : fields.length;

		StringBuilder key = new StringBuilder();
		for (int i = 0; i < keyLength; i++) {
			if (i != 0) {
				key.append(",");
			}
			key.append(fields[i] != null ? fields[i].trim() : "");
		}
		return key.toString();
	}

	private void addRowWithDiffHighlight(DefaultTableModel leftModel, String[] leftRow, DefaultTableModel rightModel,
			String[] rightRow, int columnCount) {
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

		if (leftModel != null) {
			leftModel.addRow(leftData);
		}
		if (rightModel != null) {
			rightModel.addRow(rightData);
		}

		if (rowDiff && leftModel != null) {
			diffRows.add(leftModel.getRowCount() - 1);
		}
	}

	private void identifyRowTypes(DefaultTableModel leftModel, DefaultTableModel rightModel) {
		matchingRows.clear();
		uniqueLeftRows.clear();
		uniqueRightRows.clear();

		int rowCountLeft = (leftModel != null) ? leftModel.getRowCount() : 0;
		int rowCountRight = (rightModel != null) ? rightModel.getRowCount() : 0;

		boolean[] matchedRightRows = new boolean[rowCountRight];
		Arrays.fill(matchedRightRows, false);

		if (leftModel != null) {
			for (int i = 0; i < rowCountLeft; i++) {
				boolean matchFound = false;
				Object[] leftRow = ((Vector<?>) leftModel.getDataVector().get(i)).toArray();
				if (isRowEmpty(leftRow)) {
					continue;
				}
				if (rightModel != null) {
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
				}
				if (!matchFound) {
					uniqueLeftRows.add(i);
				}
			}
		}

		if (rightModel != null) {
			for (int j = 0; j < rowCountRight; j++) {
				if (!matchedRightRows[j]) {
					Object[] rightRow = ((Vector<?>) rightModel.getDataVector().get(j)).toArray();
					if (!isRowEmpty(rightRow)) {
						uniqueRightRows.add(j);
					}
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
		int minLength = Math.min(leftRow.length, rightRow.length);
		if (minLength == 0)
			return false;

		int compareLength = (leftRow.length > 1 && rightRow.length > 1) ? minLength - 1 : minLength;

		for (int i = 0; i < compareLength; i++) {
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
		return Arrays.stream(row).map(value -> value != null ? value.trim().replaceAll("\\s+", " ") : "")
				.toArray(String[]::new);
	}

	private void populateMap(List<String[]> csvData, Map<String, List<String[]>> map, String sourceName) {
		if (csvData == null) {
			log.warn("Intento de poblar mapa con datos nulos en {}", sourceName);
			return;
		}

		for (int i = 0; i < csvData.size(); i++) {
			String[] row = normalizeRow(csvData.get(i));

			if (row.length == 0 || row[0].isEmpty()) {
				log.warn("{}: Fila vacia detectada en indice {} y se omitio", sourceName, i);
				continue;
			}

			String key = row[0].trim();
			if (key.isEmpty()) {
				log.warn("{}: Fila en indice {} tiene una clave vacia y podria no emparejarse correctamente: {}",
						sourceName, i, Arrays.toString(row));
			}

			map.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
		}
	}

	private void showNoDataMessage(DefaultTableModel leftModel, DefaultTableModel rightModel, String message) {
		if (leftModel != null && leftModel.getColumnCount() == 0 && !hasMessageColumn(leftModel)) {
			leftModel.addColumn("Mensaje");
			leftModel.addRow(new Object[]{message});
			adjustNoDataColumnWidth(leftTable);
		}
		if (rightModel != null && rightModel.getColumnCount() == 0 && !hasMessageColumn(rightModel)) {
			rightModel.addColumn("Mensaje");
			rightModel.addRow(new Object[]{message});
			adjustNoDataColumnWidth(rightTable);
		}
	}

	private boolean hasMessageColumn(DefaultTableModel model) {
		for (int i = 0; i < model.getColumnCount(); i++) {
			if ("Mensaje".equals(model.getColumnName(i))) {
				return true;
			}
		}
		return false;
	}

	private static class NoDataRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			c.setBackground(table.getBackground());
			c.setForeground(Color.WHITE);
			c.setFont(c.getFont().deriveFont(Font.BOLD));

			return c;
		}
	}

	private class RowColorRenderer extends DefaultTableCellRenderer {
		private boolean isLeftTable;

		public RowColorRenderer(boolean isLeftTable) {
			this.isLeftTable = isLeftTable;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
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
