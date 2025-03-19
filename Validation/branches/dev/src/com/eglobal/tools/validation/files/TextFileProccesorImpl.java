/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation.files;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import com.eglobal.tools.parser.pojo.RawcomLine;

public class TextFileProccesorImpl implements IFileProccesor<File> {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TextFileProccesorImpl.class);
	private AtomicLong bytesReaded = new AtomicLong();
	private AtomicInteger countLines = new AtomicInteger();
	private AtomicInteger percent = new AtomicInteger();
	private TimeIntervalChecker timeIntervalChecker = new TimeIntervalChecker("HH:mm:ss");
	private String startTime;
	private String endTime;
	private BlockingQueue<RawcomLine> queue;
	private File file;

	public TextFileProccesorImpl(String startTime, String endTime, BlockingQueue<RawcomLine> queue) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.queue = queue;
	}

	public TextFileProccesorImpl(String startTime, String endTime, BlockingQueue<RawcomLine> queue, File file) {
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
		if (!this.file.exists()) {
			throw new FileNotFoundException(String.format("Archivo no encontrado: %s", this.file.getPath()));
		}
		try (BufferedInputStream dis = new BufferedInputStream(new FileInputStream(this.file))) {
			while (dis.available() != 0) {
				List<byte[]> partsOfHeader = this.readPartsOfHeader(dis);
				RawcomLine line = this.parseRawcomLine(partsOfHeader, dis);
				if (line != null) {
					boolean isWithinInterval = true;
					if (line.getTimestamp().length() >= 8) {
						isWithinInterval = timeIntervalChecker.isTimeWithinInterval(line.getTimestamp().substring(0, 8), startTime, endTime);
					}
					if (isWithinInterval) {
						queue.put(line);
						countLines.incrementAndGet();
					}
					long readB = this.bytesReaded.get();
					int newPercent = (int) ((readB * 100) / file.length());
					if (this.percent.get() != newPercent) {
						this.percent.set(newPercent);
						log.debug("processing percent {}%", this.percent);
					}
				}
			}
			percent.set(99);;
			RawcomLine lineEnd = new RawcomLine();
			lineEnd.setRawData("<END FILE " + file.getName() + ">");
			queue.put(lineEnd);
		} catch (FileNotFoundException e) {
			log.error("Archivo no encontrado", e);
		} catch (IOException e) {
			log.error("Error al leer el archivo", e);
		} catch (InterruptedException e) {
			log.error("Error en el hilo", e);
		} catch (Throwable t) {
			log.error("Excepcion no controlada", t);
		}
	}

	private RawcomLine parseRawcomLine(List<byte[]> partsOfHeader, BufferedInputStream file) throws IOException {
		if (partsOfHeader.size() == 7) {
			List<String> cleanParts = partsOfHeader.stream().map(String::new).map(part -> {
				int index = part.indexOf(":");
				return part.substring(index + 1);
			}).map(String::trim).collect(Collectors.toList());
			RawcomLine line = RawcomLine.builder().timestamp(cleanParts.get(0)).dispatcher(Integer.parseInt(cleanParts.get(1))).pan(cleanParts.get(2)).iap(cleanParts.get(3)).listProcessor(cleanParts.get(4)).readWrite(cleanParts.get(5).charAt(0)).length(Integer.parseInt(cleanParts.get(6))).build();
			int len = line.getLength();
			byte[] data = new byte[len];
			file.read(data);
			this.bytesReaded.addAndGet(len);
			byte b = (byte) file.read();
			bytesReaded.incrementAndGet();
			byte[] data2 = new byte[len * 2];
			System.arraycopy(data, 0, data2, 0, len);
			int pos = len;
			data2[pos++] = b;
			bytesReaded.incrementAndGet();
			if (b != 10) {
				//si no es un salto de línea entonces leo hasta el salto de linea
				while ((b = (byte) file.read()) != 10) {
					data2[pos++] = b;
					bytesReaded.incrementAndGet();
				}
				data = data2;
			}
			// line.setRawData(new String(data));
			line.setRawByteData(data);
			return line;
		}
		return null;
	}

	@Override
	public synchronized int loadingPercent() {
		return this.percent.get();
	}

	@Override
	public long bytesRead() {
		return this.bytesReaded.get();
	}

	@Override
	public long linesRead() {
		return countLines.get();
	}

	public List<byte[]> readPartsOfHeader(BufferedInputStream input) throws IOException {
		List<byte[]> parts = new ArrayList<>();
		while (input.available() != 0) {
			byte b = 0;
			b = (byte) input.read();
			this.bytesReaded.incrementAndGet();
			if (b == 91) {
				// [
				byte[] arr = new byte[64];
				int pos = 0;
				while (b != 93) {
					// ]
					b = (byte) input.read();
					this.bytesReaded.incrementAndGet();
					arr[pos++] = b;
				}
				byte[] bytes = new byte[pos - 1];
				System.arraycopy(arr, 0, bytes, 0, pos - 1);
				parts.add(bytes);
				if (parts.size() == 7) {
					break;
				}
			}
		}
		return parts;
	}
}
