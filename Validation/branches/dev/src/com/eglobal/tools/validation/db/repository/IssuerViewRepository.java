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
import java.util.List;

import com.eglobal.tools.validation.db.model.EIssuerView;

public class IssuerViewRepository implements CRUDRepository<EIssuerView> {

	private static final String INSERT = "INSERT INTO iss_view (dispatcher, bin, key, req_msg, res_msg, category, fingerprint, timestamp) values (?,?,?,?,?,?,?,?)";
//	private static final String UPDATE = "";
//	private static final String DELETE = "";

	@Override
	public boolean insert(EIssuerView t, Connection conn) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
			ps.setString(1, t.getDispatcher());
			ps.setString(2, t.getBin());
			ps.setString(3, t.getKey());
			ps.setString(4, t.getRequirementMessage());
			ps.setString(5, t.getResponseMessage());
			ps.setString(6, t.getCategory());
			ps.setString(7, t.getFingerprint());
			ps.setString(8, t.getTimestamp());
			return ps.execute();
		}
	}

	@Override
	public boolean update(EIssuerView t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(EIssuerView t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insert(List<EIssuerView> t, Connection conn) throws SQLException {
		if (t == null || t.isEmpty()) {
			return false;
		}
		try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
			for (EIssuerView eIssuerView : t) {
				ps.setString(1, eIssuerView.getDispatcher());
				ps.setString(2, eIssuerView.getBin());
				ps.setString(3, eIssuerView.getKey());
				ps.setString(4, eIssuerView.getRequirementMessage());
				ps.setString(5, eIssuerView.getResponseMessage());
				ps.setString(6, eIssuerView.getCategory());
				ps.setString(7, eIssuerView.getFingerprint());
				ps.setString(8, eIssuerView.getTimestamp());
				ps.addBatch();
			}
			ps.executeBatch();
			return true;
		}
	}
}
