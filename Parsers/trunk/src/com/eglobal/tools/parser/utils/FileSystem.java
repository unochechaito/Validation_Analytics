/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Clase de utileria que contiene metodos que ayudan a validar string path y acceso a determinados directorios.
 * 
 * @author Erick Villalobos Aleman evillalobos.prov@eglobal.com.mx
 */
public class FileSystem {
	@java.lang.SuppressWarnings("all")
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(FileSystem.class);
	private static final List<String> ALLOWED_PATHS = Arrays.asList("/posprodu/standin/Authentic_Server_POS", System.getProperty("user.home"),"/validacion_cambios/almacen_fotos");

	private FileSystem() {
	}

	/**
	 * Válida si la ruta está dentro de la lista de rutas permitidas.
	 */
	public static final Predicate<String> isAllowedPath = strPath -> ALLOWED_PATHS.stream().anyMatch(strPath::startsWith);
}
