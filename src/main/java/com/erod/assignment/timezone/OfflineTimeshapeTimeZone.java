package com.erod.assignment.timezone;

import java.util.TimeZone;
import java.util.Optional;
import java.time.ZoneId;
import net.iakovlev.timeshape.TimeZoneEngine;

public class OfflineTimeshapeTimeZone implements TimeZoneAPI {

    static TimeZoneEngine engine;
    static {
        engine = TimeZoneEngine.initialize();
    }

    @Override
    public ZoneId findTimeZoneFromLocation(double latitude, double longitude) {
        Optional<ZoneId> maybeZoneId = engine.query(latitude, longitude);
        return maybeZoneId.orElseThrow(() -> {
            throw new RuntimeException("Unable to find zone");
        });
    }
}