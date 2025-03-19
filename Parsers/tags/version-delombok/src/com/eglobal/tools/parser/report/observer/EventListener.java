package com.eglobal.tools.parser.report.observer;

import com.eglobal.tools.parser.report.dto.Report;

public interface EventListener {
    void update(EventType eventType, Report data);
}
