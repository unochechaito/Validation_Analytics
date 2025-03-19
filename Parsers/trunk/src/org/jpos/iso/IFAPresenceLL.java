/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package org.jpos.iso;

public class IFAPresenceLL extends ISOStringFieldPackager {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IFAPresenceLL.class);
    static final int LENGTH_PRESENCE = 2;

    public IFAPresenceLL() {
        super();
    }

    public IFAPresenceLL(int length, String description) {
        super(length, description, LeftPadder.ZERO_PADDER, AsciiInterpreter.INSTANCE, NullPrefixer.INSTANCE);
    }

    @Override
    public int getMaxPackedLength() {
        return getLength() + 2;
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method \'pack\'");
    }

    @Override
    public int unpack(ISOComponent c, byte[] b, int offset) throws ISOException {
        int precense = Integer.parseInt(new String(b, offset, 1));
        int length = Integer.parseInt(new String(b, offset + 1, LENGTH_PRESENCE));
        if (precense == 0) {
            log.debug("campo: {}, longitud: {}, apagado", super.getDescription(), length);
            return LENGTH_PRESENCE + 1;
        }
        if (length > getLength()) {
            throw new ISOException("El tamaño del campo supera al del establecido.");
        }
        String data = new String(b, offset + (LENGTH_PRESENCE + 1), length);
        c.setValue(data);
        log.debug("campo: {}, longitud: {}, valor: {}", super.getDescription(), length, data);
        return (LENGTH_PRESENCE + 1) + length;
    }

    @Override
    public void setLength(int len) {
        checkLength(len, 99);
        super.setLength(len);
    }
}
