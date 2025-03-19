/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.report.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.eglobal.tools.parser.report.dto.Report;

public class EventManager implements Observable {
    Map<EventType, List<EventListener>> listeners = new HashMap<>();

    public EventManager(EventType... eventsType) {
        for (EventType eventType : eventsType) {
            this.listeners.put(eventType, new ArrayList<>());
        }
    }

    @Override
    public synchronized void suscribe(EventType eventType, EventListener eventListener) {
        java.util.List<com.eglobal.tools.parser.report.observer.EventListener> listener = listeners.get(eventType);
        listener.add(eventListener);
    }

    @Override
    public synchronized void unsuscribe(EventType eventType, EventListener eventListener) {
        java.util.List<com.eglobal.tools.parser.report.observer.EventListener> listener = listeners.get(eventType);
        listener.remove(eventListener);
    }

    @Override
    public synchronized void notify(EventType eventType, Report data) {
        java.util.List<com.eglobal.tools.parser.report.observer.EventListener> listener = listeners.get(eventType);
        for (EventListener eventListener : listener) {
            eventListener.update(eventType, data);
        }
    }
}
