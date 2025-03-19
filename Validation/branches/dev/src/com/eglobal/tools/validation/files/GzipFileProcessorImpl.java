/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import com.eglobal.tools.parser.pojo.RawcomLine;

public class GzipFileProcessorImpl implements IFileProccesor<File> {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GzipFileProcessorImpl.class);
	private long lengthFile = 0L;
	private long readedBytes = 0L;
	private long readedLines = 0L;
	private int percent = 0;
	private TimeIntervalChecker timeIntervalChecker = new TimeIntervalChecker("HH:mm:ss");
	private String startTime;
	private String endTime;
	private BlockingQueue<RawcomLine> queue;
	private File file;

	public GzipFileProcessorImpl(String startTime, String endTime, BlockingQueue<RawcomLine> queue) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.queue = queue;
	}

	public GzipFileProcessorImpl(String startTime, String endTime, BlockingQueue<RawcomLine> queue, File file) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.queue = queue;
		this.file = file;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void process() throws InterruptedException, IOException {
		try (GzipCompressorInputStream inputFile = new GzipCompressorInputStream(new FileInputStream(this.file))) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile));
			String line;
			while ((line = reader.readLine()) != null) {
				RawcomLine rawcomLine = this.processRawcomLine(line);
				this.readedBytes += line.getBytes().length + System.lineSeparator().length();
				boolean isWithinInterval = true;
				if (rawcomLine.getTimestamp().length() >= 8) {
					isWithinInterval = timeIntervalChecker.isTimeWithinInterval(rawcomLine.getTimestamp().substring(0, 8), startTime, endTime);
				}
				if (isWithinInterval) {
					this.queue.put(rawcomLine);
					this.readedLines++;
				}
//				this.percent = new Random().nextInt(98);;
				percent = (int) (readedBytes * 100 / inputFile.getUncompressedCount());
			}
			this.lengthFile = inputFile.getUncompressedCount();
		}
		this.percent = 99;
	}

	/**
	 * Recibe una linea del rawcom para extraer los campos y la data que contiene.
	 * 
	 * La linea se procesa mediante una exprecion regular que identificara los campos entre "[]" y 
	 * los dividira en dos mediante el caracter ":", la key del campo sera el valor antes del 
	 * caracter ":" y su value sera lo que se encuentre despues, al finalizar de leer los campos se
	 * guardara la ultima posicion para identificar el resto de la data.
	 * 
	 * @param line linea extraida del rawcom
	 * @return devuelve la data en un objeto RawcomLine
	 */
	public RawcomLine processRawcomLine(String line) {
		Map<String, String> fields = new HashMap<>();
		Pattern pattern = Pattern.compile("\\[([^]]+)\\]");
		Matcher matcher = pattern.matcher(line);
		int end = 0;
		while (matcher.find()) {
			java.lang.String field = matcher.group(1);
			java.lang.String[] keyValue = field.split(": ");
			if (keyValue.length == 2) {
				fields.put(keyValue[0], keyValue[1].trim());
			}
			end = matcher.end();
		}
		java.lang.String data = line.substring(end);
		return RawcomLine.builder().timestamp(fields.get("T")).dispatcher(Integer.parseInt(fields.get("D"))).pan(fields.get("C")).iap(fields.get("Iap")).listProcessor(fields.get("Lp")).readWrite(fields.get("Rw").charAt(0)).length(Integer.parseInt(fields.get("L"))).rawData(data).rawByteData(data.getBytes(StandardCharsets.ISO_8859_1)).build();
	}

	@Override
	public synchronized int loadingPercent() {
		return percent;
	}

	@Override
	public long bytesRead() {
		return this.readedBytes;
	}

	@Override
	public long linesRead() {
		return this.readedLines;
	}
}
