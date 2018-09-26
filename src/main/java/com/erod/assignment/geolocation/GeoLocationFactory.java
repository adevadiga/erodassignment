package com.erod.assignment.geolocation;

public class GeoLocationFactory {

    public static TimeZoneAPI getTimeZoneAPI(String serviceType) {
        if (serviceType == "offline") {
            return new OfflineTimeshapeTimeZone();
        } else if (serviceType == "online") {
            return new GoogleMapsTimeZone();
        }
        return null;
    }
}