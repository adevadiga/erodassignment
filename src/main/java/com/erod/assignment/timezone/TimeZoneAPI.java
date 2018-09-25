package com.erod.assignment.timezone;

import java.time.ZoneId;

public interface TimeZoneAPI {

    ZoneId findTimeZoneFromLocation(double latitude, double longitude);
}