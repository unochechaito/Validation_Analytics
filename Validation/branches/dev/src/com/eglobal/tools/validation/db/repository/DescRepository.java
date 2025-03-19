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


import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.db.model.EDescLinea;


public class DescRepository implements CRUDRepository<EDescLinea> {
	private static final String INSERT_TXN = "INSERT INTO desc_linea (bin, acq_dispatcher, iss_dispatcher, \"timestamp\", \"key\", \"key_acq\", \"key_iss\",\"data\") VALUES(?,?,?,?,?,?,?,?);";


	public boolean insert(EDescLinea t) throws SQLException {
		return insert(t, DBManager.getInstance().getConnection());
	}


	public boolean insert(EDescLinea descLinea, Connection conn) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(INSERT_TXN)) {
			ps.setString(1, descLinea.getBin());
			ps.setString(2, descLinea.getAcquirerDispatcher());
			ps.setString(3, descLinea.getIssuerDispatcher());
			ps.setString(4, descLinea.getTimestamp());
			ps.setString(5, descLinea.getKey());
			ps.setString(6, descLinea.getKeyAcq());
			ps.setString(7, descLinea.getKeyIss());
			ps.setString(8, descLinea.getData());
			ps.execute();
		} catch (SQLException e) {
			System.err.println("Error al insertar en la base de datos.");
			throw new SQLException("Error al realizar la operación de inserción.");
		}
		return true;
	}


	public boolean insert(List<EDescLinea> listDescLinea, Connection conn) throws SQLException {
		if (listDescLinea == null || listDescLinea.isEmpty()) {
			return false;
		}


		try (PreparedStatement ps = conn.prepareStatement(INSERT_TXN)) {
			for (EDescLinea descLinea : listDescLinea) {
				ps.setString(1, descLinea.getBin());
				ps.setString(2, descLinea.getAcquirerDispatcher());
				ps.setString(3, descLinea.getIssuerDispatcher());
				ps.setString(4, descLinea.getTimestamp());
				ps.setString(5, descLinea.getKey());
				ps.setString(6, descLinea.getKeyAcq());
				ps.setString(7, descLinea.getKeyIss());
				ps.setString(8, descLinea.getData());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			System.err.println("Error al insertar en la base de datos.");
			throw new SQLException("Error al realizar la operación de inserción.");
		}
		return true;
	}


	@Override
	public boolean update(EDescLinea t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean delete(EDescLinea t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
