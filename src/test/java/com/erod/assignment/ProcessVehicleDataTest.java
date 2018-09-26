package com.erod.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.erod.assignment.ProcessVehicleData.CSVDataType;
import com.erod.assignment.geolocation.TimeZoneAPI;
import com.erod.assignment.model.VehicleData;
import com.erod.assignment.model.VehicleData.VehicleDataBuilder;

import org.junit.jupiter.api.Test;

public class ProcessVehicleDataTest {

    @Test
    public void testGetVehicleDataFromRow() {
        String row = "2013-07-10 02:52:49,-44.490947,171.220966";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");

        TimeZoneAPI geoLocationMock = mock(TimeZoneAPI.class);
        when(geoLocationMock.findTimeZoneFromLocation(-44.490947, 171.220966))
                .thenReturn(ZoneId.of("Pacific/Auckland"));

        ProcessVehicleData processVehicleData = new ProcessVehicleData(geoLocationMock);
        VehicleData vehicleData = processVehicleData.getVehicleDataFromRow(row).get();
        verify(geoLocationMock).findTimeZoneFromLocation(-44.490947, 171.220966);

        assertEquals(ZoneId.of("Pacific/Auckland").toString(), vehicleData.getTimezone());

        ZonedDateTime expectedDateAndTimeInUtc = ZonedDateTime.of(LocalDateTime.parse("2013-07-10 02:52:49", formatter),
                ZoneOffset.UTC);

        assertEquals(expectedDateAndTimeInUtc, vehicleData.getDateTimeInUTC());
    }
}
