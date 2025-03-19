/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation;


public class AlignmentResult {
    private int totalAligned;
    private int greenCount;
    private int nonGreenCount;

    public AlignmentResult(int totalAligned, int greenCount, int nonGreenCount) {
        this.totalAligned = totalAligned;
        this.greenCount = greenCount;
        this.nonGreenCount = nonGreenCount;
    }

    public int getTotalAligned() {
        return totalAligned;
    }

    public int getGreenCount() {
        return greenCount;
    }

    public int getNonGreenCount() {
        return nonGreenCount;
    }
}