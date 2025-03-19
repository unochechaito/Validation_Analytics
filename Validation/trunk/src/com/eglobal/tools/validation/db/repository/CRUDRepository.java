/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

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
