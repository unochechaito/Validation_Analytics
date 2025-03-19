/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package org.jpos.iso;

public class IFAPresenceLLL extends ISOFieldPackager {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IFAPresenceLLL.class);
	static final int LENGTH_PRESENCE_TOTAL = 4;
	static final int LENGTH_PRESENCE = 3;

	public IFAPresenceLLL() {
		super();
	}

	public IFAPresenceLLL(int length, String description) {
		super(length, description);
	}

	@Override
	public int getMaxPackedLength() {
		throw new UnsupportedOperationException("Unimplemented method \'getMaxPackedLength\'");
	}

	@Override
	public byte[] pack(ISOComponent c) throws ISOException {
		throw new UnsupportedOperationException("Unimplemented method \'pack\'");
	}

	@Override
	public int unpack(ISOComponent c, byte[] b, int offset) throws ISOException {
		int bitOn = Integer.parseInt(new String(b, offset, 1));
		int length = Integer.parseInt(new String(b, offset + 1, LENGTH_PRESENCE));
		if (bitOn == 0) {
			log.debug("campo: {}, longitud: {}, apagado", super.getDescription(), length);
			return LENGTH_PRESENCE_TOTAL;
		}
		if (length > super.getLength()) {
			throw new ISOException("El tamano del campo supera al del establecido.");
		}
		String data = new String(b, offset + LENGTH_PRESENCE_TOTAL, length);
		c.setValue(data);
		log.debug("campo: {}, longitud: {}, valor: {}", super.getDescription(), length, data);
		return LENGTH_PRESENCE_TOTAL + length;
	}
}
