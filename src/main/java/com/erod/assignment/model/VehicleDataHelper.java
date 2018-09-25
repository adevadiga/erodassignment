package com.erod.assignment.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.erod.assignment.ProcessVehicleData.CSVDataType;
import com.erod.assignment.model.VehicleData.VehicleDataBuilder;

public class VehicleDataHelper {

    public VehicleData prepareVehicleDataFromRow(String row) {
        VehicleDataBuilder vehicleDataBuilder = new VehicleDataBuilder();
        readAndFormatCellData();
    }

    public void readAndFormatCellData(String token, CSVDataType type, VehicleDataBuilder builder,
            DateTimeFormatter formatter) {
        switch (type) {
        case DATETIME:
            ZonedDateTime dateAndTimeInUtc = ZonedDateTime.of(LocalDateTime.parse(token, formatter), ZoneOffset.UTC);
            builder.setDateTimeInUTC(dateAndTimeInUtc);
            return;

        case LATITUDE:
            double lattitude = Double.parseDouble(token);
            builder.setLattitude(lattitude);
            return;

        case LONGITUDE:
            double longitude = Double.parseDouble(token);
            builder.setLongitude(longitude);
            return;

        default:
            System.out.println("Ignoring the column.");
            return;
        }
    }
}