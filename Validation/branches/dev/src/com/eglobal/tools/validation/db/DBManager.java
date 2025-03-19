/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DBManager {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DBManager.class);
	private static DBManager __instance;
	private final String DB_NAME;
	private final String DB_DIR;
	private static final String DROP_ACQ_VIEW_TABLE = "drop table if exists acq_view";
	private static final String CREATE_ACQ_VIEW_TABLE = "CREATE TABLE acq_view(id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp TEXT NOT NULL, dispatcher TEXT NOT NULL, bin TEXT NOT NULL, category TEXT NOT NULL, fingerprint TEXT NOT NULL, key TEXT NOT NULL, req_msg TEXT NOT NULL, res_msg TEXT)";
	private static final String CREATE_ACQ_VIEW_INDEX1 = "CREATE INDEX category_index_acq_msg ON acq_view (category)";
	private static final String CREATE_ACQ_VIEW_INDEX2 = "CREATE INDEX dispatcher_bin_index_acq_msg ON acq_view (dispatcher, bin)";
	private static final String CREATE_ACQ_VIEW_INDEX3 = "CREATE INDEX dispatcher_index_acq_msg ON acq_view (dispatcher)";
	private static final String CREATE_ACQ_VIEW_INDEX4 = "CREATE INDEX timestamp_key_index_acq_msg ON acq_view (timestamp,key)";
	private static final String DROP_DESC_LINEA_TABLE = "drop table if exists desc_linea";
	private static final String CREATE_DESC_LINEA_TABLE = "CREATE TABLE desc_linea (id INTEGER PRIMARY KEY AUTOINCREMENT, bin TEXT NOT NULL, acq_dispatcher TEXT NOT NULL, iss_dispatcher TEXT NOT NULL, timestamp TEXT NOT NULL, key TEXT, key_acq TEXT NOT NULL REFERENCES acq_msg (key), key_iss TEXT NOT NULL REFERENCES iss_msg (key), data TEXT NOT NULL)";
	private static final String CREATE_DESC_LINEA_INDEX1 = "CREATE INDEX key_acq_index_desc ON desc_linea (key_acq)";
	private static final String CREATE_DESC_LINEA_INDEX2 = "CREATE INDEX key_iss_index_desc ON desc_linea (key_iss)";
	private static final String CREATE_DESC_LINEA_INDEX3 = "CREATE INDEX timestamp_key_index_desc ON desc_linea (timestamp, key_acq, key_iss)";
	private static final String DROP_DESC_LINEA_ADD_TABLE = "drop table if exists desc_linea_add";
	private static final String CREATE_DESC_LINEA_ADD_TABLE = "CREATE TABLE desc_linea_add (id INTEGER PRIMARY KEY AUTOINCREMENT, desc_key TEXT, data TEXT)";
	private static final String DROP_ISS_VIEW_TABLE = "drop table if exists iss_view";
	private static final String CREATE_ISS_VIEW_TABLE = "CREATE TABLE iss_view(id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp TEXT, dispatcher TEXT NOT NULL, bin TEXT NOT NULL, category TEXT NOT NULL, fingerprint TEXT NOT NULL, key TEXT NOT NULL, req_msg TEXT NOT NULL, res_msg TEXT)";
	private static final String CREATE_ISS_VIEW_INDEX1 = "CREATE INDEX category_index_iss_msg ON iss_view (category)";
	private static final String CREATE_ISS_VIEW_INDEX2 = "CREATE INDEX dispatcher_bin_index_iss_msg ON iss_view (dispatcher, bin)";
	private static final String CREATE_ISS_VIEW_INDEX3 = "CREATE INDEX dispatcher_index_iss_msg ON iss_view (dispatcher)";
	private static final String CREATE_ISS_VIEW_INDEX4 = "CREATE INDEX timestamp_key_index_iss_msg ON iss_view (timestamp,key)";
	private static final String DROP_HIBERNATE_SEQUENCE = "drop table if exists hibernate_sequence";
	private static final String CREATE_HIBERNATE_SEQUENCE = "CREATE TABLE hibernate_sequence (next_val BIGINT)";
	private static final String INSERT_HIBERNATE_SEQUENCE = "INSERT INTO hibernate_sequence (next_val) VALUES (1)";

	private DBManager() {
		String uniqueId = UUID.randomUUID().toString().substring(0, 15);
		String uniqueDbName = "validation_" + uniqueId + ".db";
//		this.DB_DIR = System.getProperty("user.dir") + File.separator + "Validation" + File.separator + "tmp" + File.separator + uniqueDbName;
		this.DB_DIR = "tmp" + File.separator + uniqueDbName;
		this.DB_NAME = "jdbc:sqlite:" + DB_DIR;
		init();
		log.info("Using database: " + DB_NAME);
		setUpDB();
	}

	public void deleteDB() {
		log.info("Deleting DB " + DB_DIR + "...");
		File f = new File(DB_DIR);
		if (f.exists()) {
			if (!f.delete()) {
				log.warn("Cannot delete DB file");
			}
		}
	}

	private void init() {
		try {
			log.info("Initialising BDManager...");
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.error("Cannot init DBManager", e);
		}
	}

	public static DBManager getInstance() {
		if (__instance == null) {
			__instance = new DBManager();
		}
		return __instance;
//		return new DBManager();
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_NAME);
	}

	public void setUpDB() {
		try (
			Connection conn = getConnection();
			Statement statement = conn.createStatement()) {
			log.debug("Dropping Acquirer view table...");
			statement.setQueryTimeout(10);
			statement.executeUpdate(DROP_ACQ_VIEW_TABLE);
			log.debug("Creating Acquirer view table...");
			statement.executeUpdate(CREATE_ACQ_VIEW_TABLE);
			log.debug("Creating Acquirer view Indexes...");
			statement.executeUpdate(CREATE_ACQ_VIEW_INDEX1);
			statement.executeUpdate(CREATE_ACQ_VIEW_INDEX2);
			statement.executeUpdate(CREATE_ACQ_VIEW_INDEX3);
			statement.executeUpdate(CREATE_ACQ_VIEW_INDEX4);
			log.debug("Dropping Desc Linea table...");
			statement.executeUpdate(DROP_DESC_LINEA_TABLE);
			log.debug("Creating Desc Linea table...");
			statement.executeUpdate(CREATE_DESC_LINEA_TABLE);
			log.debug("Creating Desc Linea Indexes...");
			statement.executeUpdate(CREATE_DESC_LINEA_INDEX1);
			statement.executeUpdate(CREATE_DESC_LINEA_INDEX2);
			statement.executeUpdate(CREATE_DESC_LINEA_INDEX3);
			log.debug("Dropping Desc Linea Add table...");
			statement.executeUpdate(DROP_DESC_LINEA_ADD_TABLE);
			log.debug("Creating Desc Linea Add table...");
			statement.executeUpdate(CREATE_DESC_LINEA_ADD_TABLE);
			log.debug("Dropping Issuer view table...");
			statement.executeUpdate(DROP_ISS_VIEW_TABLE);
			log.debug("Creating Issuer view table...");
			statement.executeUpdate(CREATE_ISS_VIEW_TABLE);
			log.debug("Creating Issuer view Indexes...");
			statement.executeUpdate(CREATE_ISS_VIEW_INDEX1);
			statement.executeUpdate(CREATE_ISS_VIEW_INDEX2);
			statement.executeUpdate(CREATE_ISS_VIEW_INDEX3);
			statement.executeUpdate(CREATE_ISS_VIEW_INDEX4);
			log.debug("Dropping hibernate sequence");
			statement.executeUpdate(DROP_HIBERNATE_SEQUENCE);
			log.debug("Creating hibernate sequence");
			statement.executeUpdate(CREATE_HIBERNATE_SEQUENCE);
			log.debug("Initialising hibernate sequence");
			statement.executeUpdate(INSERT_HIBERNATE_SEQUENCE);
			log.debug("Setup DB done");
		} catch (SQLException e) {
			log.error("Error trying to setup database", e);
		}
	}
}
