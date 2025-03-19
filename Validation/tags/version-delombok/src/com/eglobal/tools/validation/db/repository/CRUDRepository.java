package com.eglobal.tools.validation.db.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author egldt1029
 */

public interface CRUDRepository <T>{
	
	public boolean insert(T t, Connection conn) throws SQLException;
	
	public boolean update(T t, Connection conn) throws SQLException;
	
	public boolean delete(T t, Connection conn) throws SQLException;
	
	public boolean insert(List<T> t, Connection conn) throws SQLException;
	
}
