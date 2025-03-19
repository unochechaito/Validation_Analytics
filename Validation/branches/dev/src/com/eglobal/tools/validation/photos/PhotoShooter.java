/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation.photos;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.eglobal.tools.validation.Photo;
import com.eglobal.tools.validation.db.DBManager;
import lombok.AllArgsConstructor;

public class PhotoShooter {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PhotoShooter.class);
	static final char SEPARATOR = ',';
	private List<Photo> photos;
	private File directory;
	private PropertyChangeSupport pts = new PropertyChangeSupport(this);

	public PhotoShooter(List<Photo> photos, File directory) {
		this.photos = photos;
		this.directory = directory;
	}

	public void addListener(PropertyChangeListener listener) {
		pts.addPropertyChangeListener(listener);
	}

	public void start() {
		ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Photo Shotter");
			}
		});
		log.debug("Photo Shotter created!");
		executor.submit(() -> takePhotos());
		executor.shutdown();
	}

	public Void takePhotos() {
		int percentByPhoto = 100 / photos.size();
		int percent = 0;
		for (Photo photo : photos) {
			log.debug("Taking " + photo.getName());
			takePhoto(photo);
			pts.firePropertyChange("percent", percent, percent += percentByPhoto);
		}
		pts.firePropertyChange("percent", percent, 100);
		return null;
	}

	private Void takePhoto(Photo photo) {
		if (directory.canWrite()) {
			String path = directory + File.separator + photo.getOutput_file();
			log.debug(photo.getId() + " outout file: " + path);
			try (
				FileWriter fw = new FileWriter(new File(path));
				BufferedWriter bw = new BufferedWriter(fw);
				Connection conn = DBManager.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(photo.getQuery());
				ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					sb.append(rsmd.getColumnName(i));
					if (i < rsmd.getColumnCount()) {
						sb.append(SEPARATOR);
					}
				}
				sb.append('\n');
				bw.write(sb.toString());
				sb.setLength(0);
				while (rs.next()) {
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						java.lang.String result = rs.getString(i);
						sb.append(result instanceof String ? result.replaceAll(",", "/") : result);
						if (i < rsmd.getColumnCount()) {
							sb.append(SEPARATOR);
						}
					}
					sb.append('\n');
					bw.write(sb.toString());
					sb.setLength(0);
				}
			} catch (IOException e) {
				log.error("Writing photos error", e);
			} catch (SQLException e) {
				log.error("DB Error", e);
			}
		}
		return null;
	}
}
