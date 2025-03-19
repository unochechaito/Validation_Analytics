/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.statistics;

public class TransactionStatistics {
	private long counter;
	private long approved;
	private long declined;

	@java.lang.SuppressWarnings("all")
	public long getCounter() {
		return this.counter;
	}

	@java.lang.SuppressWarnings("all")
	public long getApproved() {
		return this.approved;
	}

	@java.lang.SuppressWarnings("all")
	public long getDeclined() {
		return this.declined;
	}
}
