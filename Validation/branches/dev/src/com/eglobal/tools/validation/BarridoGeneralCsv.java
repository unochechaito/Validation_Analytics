/**
 *   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
 *   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
 *   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
 *   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
 *   de correo seginfo@eglobal.com.mx                                                         *
 **/
package com.eglobal.tools.validation;

import com.eglobal.tools.validation.utils.CsvUtils;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * BarridoGeneralCsv.
 * <p>
 * Clase con el patron facade que genera una comparacion de todos los archivos
 * csv generados(photos).
 */
public class BarridoGeneralCsv {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BarridoGeneralCsv.class);

	public enum AlignmentType {
		FIRST_COLUMN, SECOND_COLUMN, THIRD_COLUMN, SIXTH_COLUMN;
	}

	private final int POSITION_NAME_FILE = 1;
	private final int POSITION_TOTAL_REG_BASE = 2;
	private final int POSITION_TOTAL_REG_TO_COMPARE = 3;
	// final int POSITION_DIFFERENCES = 4;
	private final Map<String, AlignmentResult> alignmentResults = new HashMap<>();
	private static final List<String> FIRST_COLUMN_ALIGN_OPTIONS = Arrays.asList(
			"Campos presentes por tipo de transaccion recibidos de adquirente MasterCard",
			"Campos presentes por tipo de transaccion recibidos de adquirente Visa",
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
	private static final List<String> SECOND_COLUMN_ALIGN_OPTIONS = Arrays.asList(
			"Campos presentes por tipo de transaccion recibidos de adquirentes Nacional",
			"Campos presentes por tipo de transaccion enviados a emisores Nacional",
			"Campos presentes por tipo de transaccion respondidos por emisores Nacional",
			"Campos presentes por tipo de transaccion respondidos a adquirentes Nacional",
			"Tokens presentes por tipo de transaccion recibidos de adquirentes Nacional",
			"Tokens presentes por tipo de transaccion enviados a emisores Nacional",
			"Tokens presentes por tipo de transaccion respondidos a adquirentes Nacional",
			"Tokens presentes por tipo de transaccion respondidos de emisores Nacional",
			"Mapeo MTI-ProcCode a TranCode");
	private static final List<String> THIRD_COLUMN_ALIGN_OPTIONS = Collections
			.singletonList("Tipo de transacciones declinadas por el emisor y aprobadas al adquirente");
	private static final List<String> SIXTH_COLUMN_ALIGN_OPTIONS = Collections
			.singletonList("Adicionales por tipo de transaccion plataforma de adquirente y emisor");
	private static final List<String> C44_COLUMN_ALIGN_OPTIONS = Arrays.asList(
			"C44 respondido al adquirente Visa por operativa", "C44 respondido por el emisor Visa por operativa");
	private Path baseDir;
	private Path toCompareDir;

	public List<Object[]> compare() throws IOException {
		List<Object[]> result = new ArrayList<>();

		if (Files.isDirectory(this.baseDir) && Files.isDirectory(this.toCompareDir)) {
			java.util.HashMap<String, File> filesBaseDir = this.listFiles(this.baseDir);
			java.util.HashMap<String, File> filesToCompareDir = this.listFiles(this.toCompareDir);

			int count = 1;
			for (String key : filesBaseDir.keySet()) {
				File baseFile = filesBaseDir.get(key);
				File compareFile = filesToCompareDir.get(key);

				Object[] data = new Object[5];
				data[0] = String.format("%d", count);
				String fileNameWithoutExt = baseFile.getName().replaceFirst("[.][^.]+$", "");
				data[POSITION_NAME_FILE] = fileNameWithoutExt;
				data[POSITION_TOTAL_REG_BASE] = String.format("%s", this.countLinesOfFile(baseFile));

				if (compareFile != null && compareFile.exists()) {
					data[POSITION_TOTAL_REG_TO_COMPARE] = String.format("%s", this.countLinesOfFile(compareFile));
				} else {
					data[POSITION_TOTAL_REG_TO_COMPARE] = "No existe";
				}

				try {
					if (fileNameWithoutExt.equals("Porcentaje de ON2 por BIN")
							|| fileNameWithoutExt.equals("Porcentaje de ON2 por Dispatcher Adquirente")) {
						data[4] = getAlertForPorcentajeOn2PorBin(baseFile);
					} else if (fileNameWithoutExt.equals("Codigos de C39 por Dispatcher emisor")) {
						data[4] = getAlertForPorcentajeC39PorDispatcher(baseFile);
					} else if (fileNameWithoutExt.equals("Porcentaje de ON2 DESC")) {
						data[4] = getAlertForOn2Desc(baseFile);
					} else if (fileNameWithoutExt
							.equals("Mapeo de codigos de error C39 emisor, on2 y C39 adquierente")) {
						data[4] = getAlertForC39(baseFile);
					} else if (fileNameWithoutExt.equals(
							"Indicadores en el desc de Tipo de Cambio por flujo adquirente internacional a emisor")) {
						data[4] = getAlertForCurrencyExchange(baseFile);
					} else if (FIRST_COLUMN_ALIGN_OPTIONS.contains(fileNameWithoutExt)) {
						assert compareFile != null;
						data[4] = processAlignment(baseFile, compareFile, AlignmentType.FIRST_COLUMN,
								fileNameWithoutExt);
					} else if (SECOND_COLUMN_ALIGN_OPTIONS.contains(fileNameWithoutExt)) {
						assert compareFile != null;
						data[4] = processAlignment(baseFile, compareFile, AlignmentType.SECOND_COLUMN,
								fileNameWithoutExt);
					} else if (THIRD_COLUMN_ALIGN_OPTIONS.contains(fileNameWithoutExt)) {
						assert compareFile != null;
						data[4] = processAlignment(baseFile, compareFile, AlignmentType.THIRD_COLUMN,
								fileNameWithoutExt);
					} else if (SIXTH_COLUMN_ALIGN_OPTIONS.contains(fileNameWithoutExt)) {
						assert compareFile != null;
						data[4] = processAlignment(baseFile, compareFile, AlignmentType.SIXTH_COLUMN,
								fileNameWithoutExt);
					} else if (C44_COLUMN_ALIGN_OPTIONS.contains(fileNameWithoutExt)) {
						if (!baseFile.exists() || compareFile == null || !compareFile.exists()) {
							log.warn("Solo el archivo {} existe. No se puede realizar la comparación C44.",
									!baseFile.exists() ? "A comparar" : "Base");
							data[4] = new Alert("Solo un archivo existe, no se puede realizar la comparación C44.",
									Color.YELLOW);
						} else {
							data[4] = processC44Alignment(baseFile, compareFile);
						}
					} else {
						data[4] = new Alert("Sin datos", Color.DARK_GRAY);
					}
				} catch (Exception e) {
					log.error("Error al procesar la comparación del archivo: " + fileNameWithoutExt, e);
					data[4] = new Alert("Error al procesar", Color.RED);
				}

				result.add(data);
				count++;
			}
		}

		return result;
	}

	private Alert processC44Alignment(File baseFile, File compareFile) {
		try {
			String basePath = baseFile.getCanonicalPath().replace(".txt", "");
			String comparePath = compareFile.getCanonicalPath().replace(".txt", "");
			List<String[]> baseCsvData = CsvUtils.readCsv(basePath);
			List<String[]> compareCsvData = CsvUtils.readCsv(comparePath);
			JTable leftTable = new JTable(new DefaultTableModel());
			JTable rightTable = new JTable(new DefaultTableModel());
			CsvComparator comparator = new CsvComparator(leftTable, rightTable);
			return getAlertForUniqueRows(baseCsvData, compareCsvData);
		} catch (IOException e) {
			log.error("Error leyendo archivos CSV para la opcion \'C44\'", e);
			return new Alert("Error al leer archivos CSV", Color.RED);
		}
	}

	private Alert processAlignment(File baseFile, File compareFile, AlignmentType alignmentType, String photoName)
			throws IOException {
		String basePath = baseFile.getCanonicalPath().replace(".txt", "");
		String comparePath = compareFile.getCanonicalPath().replace(".txt", "");
		List<String[]> baseCsvData = CsvUtils.readCsv(basePath);
		List<String[]> compareCsvData = CsvUtils.readCsv(comparePath);
		CsvComparator comparator = new CsvComparator(new JTable(), new JTable());
		AlignmentResult alignmentResult;

		switch (alignmentType) {
			case FIRST_COLUMN :
				alignmentResult = comparator.alignRowsByFirstColumn(photoName, new DefaultTableModel(),
						new DefaultTableModel(), baseCsvData, compareCsvData);
				break;
			case SECOND_COLUMN :
				alignmentResult = comparator.alignRowsByTwoColumns(new DefaultTableModel(), new DefaultTableModel(),
						baseCsvData, compareCsvData);
				break;
			case THIRD_COLUMN :
				alignmentResult = comparator.alignRowsByThreeColumns(new DefaultTableModel(), new DefaultTableModel(),
						baseCsvData, compareCsvData);
				break;
			case SIXTH_COLUMN :
				alignmentResult = comparator.alignRowsBySixColumns(new DefaultTableModel(), new DefaultTableModel(),
						baseCsvData, compareCsvData);
				break;
			default :
				throw new IllegalArgumentException("Tipo de alineacion no soportado.");
		}
		alignmentResults.put(baseFile.getName(), alignmentResult);
		return createAlertFromAlignment(alignmentResult);
	}

	private Alert getAlertForCurrencyExchange(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException("El archivo esta vacio.");
		}
		String[] headers = lines.get(0).split(",");
		int tipoCambioOtrasMonedasIndex = -1;
		int tipoCambioBanxicoIndex = -1;
		int factorConversionIndex = -1;
		int markupIndex = -1;
		for (int i = 0; i < headers.length; i++) {
			if ("tipo_cambio_otras_monedas".equalsIgnoreCase(headers[i].trim())) {
				tipoCambioOtrasMonedasIndex = i;
			} else if ("tipo_cambio_banxico".equalsIgnoreCase(headers[i].trim())) {
				tipoCambioBanxicoIndex = i;
			} else if ("factor_conversion".equalsIgnoreCase(headers[i].trim())) {
				factorConversionIndex = i;
			} else if ("markup".equalsIgnoreCase(headers[i].trim())) {
				markupIndex = i;
			}
		}
		if (tipoCambioOtrasMonedasIndex == -1 || tipoCambioBanxicoIndex == -1 || factorConversionIndex == -1
				|| markupIndex == -1) {
			throw new IllegalArgumentException("Una o mas columnas requeridas no estan presentes en el archivo.");
		}
		int totalRows = 0;
		int zeroValueCases = 0;
		for (int i = 1; i < lines.size(); i++) {
			String[] fields = lines.get(i).split(",");
			totalRows++;
			boolean isZeroInColumns = false;
			if (fields.length > tipoCambioOtrasMonedasIndex && isZero(fields[tipoCambioOtrasMonedasIndex])) {
				isZeroInColumns = true;
			}
			if (fields.length > tipoCambioBanxicoIndex && isZero(fields[tipoCambioBanxicoIndex])) {
				isZeroInColumns = true;
			}
			if (fields.length > factorConversionIndex && isZero(fields[factorConversionIndex])) {
				isZeroInColumns = true;
			}
			if (fields.length > markupIndex && isZero(fields[markupIndex])) {
				isZeroInColumns = true;
			}
			if (isZeroInColumns) {
				zeroValueCases++;
			}
		}
		String alertMessage = String.format("%d caso(s) con valores en ceros", zeroValueCases);
		if (zeroValueCases == 0) {
			return new Alert(alertMessage, new Color(34, 139, 34));
		} else if (zeroValueCases > 0 && zeroValueCases < totalRows - 1) {
			return new Alert(alertMessage, new Color(255, 165, 0));
		} else {
			return new Alert(alertMessage, new Color(178, 34, 34));
		}
	}

	private Alert getAlertForC39(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException("El archivo esta vacio.");
		}
		String[] headers = lines.get(0).split(",");
		int errorEmiIndex = -1;
		int errorAdqIndex = -1;
		for (int i = 0; i < headers.length; i++) {
			if ("error_emi".equalsIgnoreCase(headers[i].trim())) {
				errorEmiIndex = i;
			} else if ("error_adq".equalsIgnoreCase(headers[i].trim())) {
				errorAdqIndex = i;
			}
		}
		if (errorEmiIndex == -1 || errorAdqIndex == -1) {
			throw new IllegalArgumentException(
					"Las columnas \'error_emi\' o \'error_adq\' no estan presentes en el archivo.");
		}
		int totalDiscrepancies = 0;
		int totalRows = 0;
		for (int i = 1; i < lines.size(); i++) {
			String[] fields = lines.get(i).split(",");
			if (fields.length > Math.max(errorEmiIndex, errorAdqIndex)) {
				totalRows++;
				String errorEmiValue = fields[errorEmiIndex].trim();
				String errorAdqValue = fields[errorAdqIndex].trim();
				if (!errorEmiValue.equals(errorAdqValue)) {
					totalDiscrepancies++;
				}
			}
		}
		String alertMessage;
		if (totalRows == 0) {
			alertMessage = "No hay registros para calcular discrepancias.";
			return new Alert(alertMessage, Color.DARK_GRAY);
		} else {
			int percentage = (totalDiscrepancies * 100) / totalRows;
			alertMessage = percentage + " % de discrepancias entre EMI y ACQ";
			if (totalDiscrepancies == 0) {
				return new Alert(alertMessage, new Color(34, 139, 34));
			} else if (totalDiscrepancies > 0 && totalDiscrepancies < totalRows) {
				return new Alert(alertMessage, new Color(255, 165, 0));
			} else {
				return new Alert(alertMessage, new Color(178, 34, 34));
			}
		}
	}

	private Alert getAlertForOn2Desc(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException("El archivo esta vacio.");
		}
		String[] headers = lines.get(0).split(",");
		int on2Index = -1;
		int percentageIndex = -1;
		for (int i = 0; i < headers.length; i++) {
			if ("on2".equalsIgnoreCase(headers[i].trim())) {
				on2Index = i;
			} else if ("porcentaje".equalsIgnoreCase(headers[i].trim())) {
				percentageIndex = i;
			}
		}
		if (on2Index == -1 || percentageIndex == -1) {
			throw new IllegalArgumentException(
					"Las columnas \'on2\' o \'porcentaje\' no estan presentes en el archivo.");
		}
		for (int i = 1; i < lines.size(); i++) {
			String[] fields = lines.get(i).split(",");
			if (fields.length > Math.max(on2Index, percentageIndex)) {
				if ("0000".equals(fields[on2Index].trim())) {
					double percentage = Double.parseDouble(fields[percentageIndex].trim());
					String text = "Porcentaje ON2: " + percentage;
					if (percentage >= 85) {
						return new Alert(text, new Color(34, 139, 34));
					} else if (percentage >= 51) {
						return new Alert(text, new Color(255, 165, 0));
					} else {
						return new Alert(text, new Color(178, 34, 34));
					}
				}
			}
		}
		return new Alert("Sin datos", new Color(173, 181, 230, 223));
	}

	private Alert getAlertForPorcentajeOn2PorBin(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException("El archivo esta vacio.");
		}
		String[] headers = lines.get(0).split(",");
		int on2Index = -1;
		int porcentajeOn2BinIndex = -1;
		for (int i = 0; i < headers.length; i++) {
			if ("on2".equalsIgnoreCase(headers[i].trim())) {
				on2Index = i;
			} else if ("porcentaje_on2_bin".equalsIgnoreCase(headers[i].trim())
					|| "porcentaje_on2_dispatcher".equalsIgnoreCase(headers[i].trim())) {
				porcentajeOn2BinIndex = i;
			}
		}
		double porcentajeConError = getPorcentajeConError(on2Index, porcentajeOn2BinIndex, lines);
		String alertMessage = "Porcentaje de registros con error: " + String.format("%.2f%%", porcentajeConError);
		return getAlert(porcentajeConError, alertMessage);
	}

	private Alert getAlert(double porcentajeConError, String alertMessage) {
		if (porcentajeConError == 0) {
			return new Alert(alertMessage, new Color(34, 139, 34));
		} else if (porcentajeConError <= 15) {
			return new Alert(alertMessage, new Color(255, 165, 0));
		} else {
			return new Alert(alertMessage, new Color(178, 34, 34));
		}
	}

	private static double getPorcentajeConError(int on2Index, int porcentajeOn2BinIndex, List<String> lines) {
		if (on2Index == -1 || porcentajeOn2BinIndex == -1) {
			throw new IllegalArgumentException(
					"Las columnas \'on2\' o \'porcentaje_on2_bin\' no estan presentes en el archivo.");
		}
		int acumulado = 0;
		int totalRegistros = 0;
		for (int i = 1; i < lines.size(); i++) {
			String[] fields = lines.get(i).split(",");
			totalRegistros++;
			if (fields.length > Math.max(on2Index, porcentajeOn2BinIndex)) {
				String on2 = fields[on2Index].trim();
				if ("0000".equals(on2)) {
					double porcentaje = Double.parseDouble(fields[porcentajeOn2BinIndex].trim());
					if (porcentaje < 70) {
						acumulado++;
					}
				} else {
					acumulado++;
				}
			}
		}
		return (acumulado * 100.0) / totalRegistros;
	}

	private Alert getAlertForPorcentajeC39PorDispatcher(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException("El archivo está vacío.");
		}
		String[] headers = lines.get(0).split(",");
		int dispatcherEmiIndex = -1;
		int c39EmiIndex = -1;
		int porcentajeC39DispatcherIndex = -1;
		for (int i = 0; i < headers.length; i++) {
			if ("dispatcher_emi".equalsIgnoreCase(headers[i].trim())) {
				dispatcherEmiIndex = i;
			} else if ("C39_emi".equalsIgnoreCase(headers[i].trim())) {
				c39EmiIndex = i;
			} else if ("porcentaje_C39_dispatcher".equalsIgnoreCase(headers[i].trim())) {
				porcentajeC39DispatcherIndex = i;
			}
		}
		if (dispatcherEmiIndex == -1 || c39EmiIndex == -1 || porcentajeC39DispatcherIndex == -1) {
			throw new IllegalArgumentException(
					"Las columnas \'dispatcher_emi\', \'C39_emi\', o \'porcentaje_C39_dispatcher\' no estan presentes en el archivo.");
		}
		int acumulado = 0;
		int totalRegistros = 0;
		Map<String, List<String[]>> groupedRows = new HashMap<>();
		for (int i = 1; i < lines.size(); i++) {
			String[] fields = lines.get(i).split(",");
			if (fields.length > dispatcherEmiIndex) {
				String dispatcherValue = fields[dispatcherEmiIndex].trim();
				groupedRows.computeIfAbsent(dispatcherValue, k -> new ArrayList<>()).add(fields);
			}
		}
		for (Map.Entry<String, List<String[]>> entry : groupedRows.entrySet()) {
			for (String[] row : entry.getValue()) {
				totalRegistros++;
				String c39Value = row[c39EmiIndex].trim();
				if (!"00".equals(c39Value)) {
					acumulado++;
				} else {
					double porcentaje = Double.parseDouble(row[porcentajeC39DispatcherIndex].trim());
					if (porcentaje < 70) {
						acumulado++;
					}
				}
			}
		}
		double porcentajeConError = (acumulado * 100.0) / totalRegistros;
		String alertMessage = "Registros C39 con error: " + String.format("%.2f%%", porcentajeConError);
		return getAlert(porcentajeConError, alertMessage);
	}

	private Alert getAlertForUniqueRows(List<String[]> baseCsvData, List<String[]> toCompareCsvData) {
		if (baseCsvData.isEmpty() || toCompareCsvData.isEmpty()) {
			return new Alert("Archivo vacio o sin datos", Color.GRAY);
		}
		Set<String> baseRows = baseCsvData.stream().skip(1).map(row -> String.join(",", row))
				.collect(Collectors.toSet());
		Set<String> toCompareRows = toCompareCsvData.stream().skip(1).map(row -> String.join(",", row))
				.collect(Collectors.toSet());
		baseRows.removeAll(toCompareRows);
		int uniqueCount = baseRows.size();
		String alertMessage = uniqueCount + " ocurrencias unicas en archivo Base";
		if (uniqueCount == 0) {
			return new Alert(alertMessage, new Color(34, 139, 34));
		} else if (uniqueCount <= 10) {
			return new Alert(alertMessage, new Color(255, 165, 0));
		} else {
			return new Alert(alertMessage, new Color(178, 34, 34));
		}
	}

	private boolean isZero(String value) {
		if (value == null || value.trim().isEmpty()) {
			return false;
		}
		return value.trim().matches("^0+$");
	}

	private Alert createAlertFromAlignment(AlignmentResult alignmentResult) {
		int totalAligned = alignmentResult.getTotalAligned();
		int greenCount = alignmentResult.getGreenCount();
		int nonGreenCount = alignmentResult.getNonGreenCount();
		if (nonGreenCount == 0) {
			String message = "No hay categorias con diferencias.";
			return new Alert(message, Color.DARK_GRAY);
		}
		String message = String.format("%d categoria(s) con diferencias", nonGreenCount);
		if (greenCount == totalAligned) {
			return new Alert(message, new Color(34, 139, 34));
		} else if (nonGreenCount > 0 && nonGreenCount != totalAligned) {
			return new Alert(message, new Color(255, 165, 0));
		} else if (nonGreenCount == totalAligned) {
			return new Alert(message, new Color(178, 34, 34));
		} else {
			return new Alert(message, new Color(173, 216, 230, 153));
		}
	}

	public HashMap<String, File> listFiles(Path path) throws IOException {
		try (Stream<Path> stream = Files.list(path)) {
			java.util.Map<java.lang.String, java.io.File> map = stream.filter(file -> !Files.isDirectory(file))
					.map(Path::toFile).collect(Collectors.toMap(File::getName, Function.identity()));
			return new HashMap<>(map);
		}
	}

	public int countLinesOfFile(File file) throws FileNotFoundException, IOException {
		try (LineNumberReader reader = new LineNumberReader(new FileReader(file))) {
			reader.skip(Integer.MAX_VALUE);
			return reader.getLineNumber();
		}
	}

	@java.lang.SuppressWarnings("all")
	public BarridoGeneralCsv(final Path baseDir, final Path toCompareDir) {
		this.baseDir = baseDir;
		this.toCompareDir = toCompareDir;
	}
}
