package com.eglobal.tools.validation.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.db.model.EDescLineaAdd;

/**
 * 
 * @author egldt1029
 * 
 */

public class DescAddRepository implements CRUDRepository<EDescLineaAdd>{

	private static final String INSERT_TXN = "INSERT INTO desc_linea_add (desc_key, data) VALUES(?,?);";
	
	public boolean insert(EDescLineaAdd t) throws SQLException{
		return insert(t, DBManager.getInstance().getConnection());
	}
	
	@Override
	public boolean insert(EDescLineaAdd descLineaAdd, Connection conn) throws SQLException {
		try(PreparedStatement ps = conn.prepareStatement(INSERT_TXN)){
			ps.setString(1, descLineaAdd.getDescKey());
			ps.setString(2, descLineaAdd.getData());
			ps.execute();
		} catch (SQLException e) {
			System.err.println("Error al insertar en la base de datos.");
			throw new SQLException("Error al realizar la operación de inserción.");
		}
		return true;
	}
	
	@Override
	public boolean insert(List<EDescLineaAdd> listDescLineaAdd, Connection conn) throws SQLException {
		try(PreparedStatement ps = conn.prepareStatement(INSERT_TXN)){	
			for(EDescLineaAdd descLineaAdd : listDescLineaAdd) {
				ps.setString(1, descLineaAdd.getDescKey());
				ps.setString(2, descLineaAdd.getData());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			System.err.println("Error al insertar en la base de datos." + e);
			throw new SQLException("Error al realizar la operación de inserción.");
		}
		return true;
	}

	@Override
	public boolean update(EDescLineaAdd t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(EDescLineaAdd t, Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
