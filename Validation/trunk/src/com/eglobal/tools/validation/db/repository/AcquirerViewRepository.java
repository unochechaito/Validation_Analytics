/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import com.eglobal.tools.validation.db.model.EAcquirerView;

public class AcquirerViewRepository implements CRUDRepository<EAcquirerView> {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AcquirerViewRepository.class);
	private static final String INSERT = "INSERT INTO acq_view (timestamp, dispatcher, bin, category, fingerprint, key, req_msg, res_msg) values (?,?,?,?,?,?,?,?)";

//  private static final String UPDATE = "";
//  private static final String DELETE = "";
	@Override
	public boolean insert(EAcquirerView t, Connection conn) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
			ps.setString(1, t.getTimestamp());
			ps.setString(2, t.getDispatcher());
			ps.setString(3, t.getBin());
			ps.setString(4, t.getCategory());
			ps.setString(5, t.getFingerprint());
			ps.setString(6, t.getKey());
			ps.setString(7, t.getRequirementMessage());
			ps.setString(8, t.getResponseMessage());
			return ps.execute();
		}
	}

	@Override
	public boolean update(EAcquirerView t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(EAcquirerView t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insert(List<EAcquirerView> t, Connection conn) throws SQLException {
		if (t == null || t.isEmpty()) {
			return false;
		}
		try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
			for (EAcquirerView eAcquirerView : t) {
				ps.setString(1, eAcquirerView.getTimestamp());
				ps.setString(2, eAcquirerView.getDispatcher());
				ps.setString(3, eAcquirerView.getBin());
				ps.setString(4, eAcquirerView.getCategory());
				ps.setString(5, eAcquirerView.getFingerprint());
				ps.setString(6, eAcquirerView.getKey());
				ps.setString(7, eAcquirerView.getRequirementMessage());
				ps.setString(8, eAcquirerView.getResponseMessage());
				ps.addBatch();
			}
			try {
				int[] result = ps.executeBatch();
				log.debug("Batch insert result: {}", Arrays.toString(result));
			} catch (SQLException e) {
				log.error("Error executing batch insert", e);
			}
			return true;
		}
	}
}
