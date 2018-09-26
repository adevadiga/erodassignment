package com.erod.assignment.geolocation;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.iakovlev.timeshape.TimeZoneEngine;

public class OfflineTimeshapeTimeZone implements TimeZoneAPI {

    class Pair {
        double latitude;
        double longitude;

        public Pair(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Pair))
                return false;
            Pair that = (Pair) obj;

            if (this.latitude == that.latitude && this.longitude == that.longitude) {
                return true;
            }
            return false;
        }

    }

    // Simple in-memory cache for Zone's
    static Map<Pair, ZoneId> zoneIdCache = new HashMap<>();

    static TimeZoneEngine engine;
    static {
        engine = TimeZoneEngine.initialize();
    }

    @Override
    public ZoneId findTimeZoneFromLocation(double latitude, double longitude) {
        Pair pair = new Pair(latitude, longitude);
        if (zoneIdCache.containsKey(pair)) {
            return zoneIdCache.get(pair);
        }

        Optional<ZoneId> maybeZoneId = engine.query(latitude, longitude);
        maybeZoneId.ifPresent(zoneId -> zoneIdCache.put(pair, zoneId));

        return maybeZoneId.orElse(null);

        // orElseThrow(() -> {
        // throw new RuntimeException("Unable to find zone");
        // });
    }
}