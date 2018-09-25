package com.erod.assignment.geolocation;

import java.time.ZoneId;

public interface TimeZoneAPI {

    ZoneId findTimeZoneFromLocation(double latitude, double longitude);
}