/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.report;

import com.eglobal.tools.parser.report.dto.Report;
import com.eglobal.tools.parser.report.observer.EventListener;
import com.eglobal.tools.parser.report.observer.EventType;

public class LogFieldParserListener implements EventListener {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogFieldParserListener.class);

    @Override
    public void update(EventType eventType, Report data) {
        log.debug("EventType: {} Data: {}", eventType, data);
    }
}
