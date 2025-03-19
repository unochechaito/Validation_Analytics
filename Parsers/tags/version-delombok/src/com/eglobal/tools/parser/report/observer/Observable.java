package com.eglobal.tools.parser.report.observer;

import com.eglobal.tools.parser.report.dto.Report;

public interface Observable {
    void suscribe(EventType eventType, EventListener eventListener);
    void unsuscribe(EventType eventType, EventListener eventListener);
    void notify(EventType eventType, Report data);
}
