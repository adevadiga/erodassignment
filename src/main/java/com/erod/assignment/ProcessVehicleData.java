package com.erod.assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.erod.assignment.model.Vehicle;
import com.erod.assignment.model.Vehicle.VehicleDataBuilder;
import com.erod.assignment.timezone.OfflineTimeshapeTimeZone;

public class ProcessVehicleData {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");

    public enum CSVDataType {
        DATETIME(1), LATITUDE(2), LONGITUDE(3);

        private final int columnIndex;

        CSVDataType(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public int getColumnIndex() {
            return this.columnIndex;
        }

        private static final Map<Integer, CSVDataType> lookup = new HashMap<Integer, CSVDataType>();
        static {
            for (CSVDataType type : EnumSet.allOf(CSVDataType.class))
                lookup.put(type.getColumnIndex(), type);
        }

        public static CSVDataType get(int code) {
            return lookup.get(code);
        }
    }

    public void process(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            String line = null;
            while ((line = reader.readLine()) != null) {

                VehicleDataBuilder builder = new VehicleDataBuilder();

                StringTokenizer st = new StringTokenizer(line, ",");
                int column = 1;
                while (st.hasMoreElements()) {
                    String token = st.nextToken();
                    // System.out.println(token);
                    formatInputData(token, CSVDataType.get(column), builder);
                    column++;
                }

                Vehicle vehicleData = builder.build();
                System.out.println(vehicleData);
                System.out.println("-----");
                prettyPrintVehicleData(vehicleData);
                System.out.println("-----");
            }
        }
    }

    private void formatInputData(String token, CSVDataType type, VehicleDataBuilder builder) {

        ZonedDateTime time = null;
        if (type == CSVDataType.DATETIME) {
            LocalDateTime utcDateTime = LocalDateTime.parse(token, formatter);
            ZonedDateTime dateAndTimeInUtc = ZonedDateTime.of(utcDateTime, ZoneOffset.UTC);
            System.out.println("ANOOOOOOOOO");
            System.out.println("Current date and time in a particular timezone-UTC : " + dateAndTimeInUtc);

            ZoneId australia = ZoneId.of("Pacific/Auckland");

            //////
            LocalDateTime localTime = dateAndTimeInUtc.toLocalDateTime();
            System.out.println("localTime: " + localTime);
            ZonedDateTime dateAndTimeInZone = ZonedDateTime.of(localTime, australia);
            System.out.println("localTime Coverted: " + dateAndTimeInZone);
            //////

            ZonedDateTime utcDate = dateAndTimeInUtc.withZoneSameInstant(australia);
            System.out.println("Current date and time in Australia : " + utcDate);

            System.out.println("ANOOOOOOOOO");
            time = utcDateTime.atZone(ZoneOffset.UTC);
            System.out.println("***********");
            System.out.println(time);
            System.out.println("***********");
        }

        switch (type) {
        case DATETIME:
            ZonedDateTime dateAndTimeInUtc = ZonedDateTime.of(LocalDateTime.parse(token, formatter), ZoneOffset.UTC);
            builder.setDateTime(dateAndTimeInUtc);
            return;

        case LATITUDE:
            double lattitude = Double.parseDouble(token);
            System.out.println(lattitude);
            builder.setLattitude(lattitude);
            return;

        case LONGITUDE:
            double longitude = Double.parseDouble(token);
            System.out.println(longitude);
            builder.setLongitude(longitude);
            return;

        default:
            System.out.println("Invalid");
            return;
        }
    }

    public void prettyPrintVehicleData(Vehicle vehicleData) {
        ZoneId zoneId = new OfflineTimeshapeTimeZone().findTimeZoneFromLocation(vehicleData.getLattitude(),
                vehicleData.getLongitude());
        System.out.println("ZoneId..........." + zoneId);

        // ZonedDateTime utcDate =
        // vehicleData.getDateTime().withZoneSameInstant(zoneId);
        // ZonedDateTime dateAndTimeInZone = ZonedDateTime.of(vehicleData.getDateTime(),
        // zoneId);
        ZonedDateTime dateAndTimeInZone = vehicleData.getDateTime().withZoneSameInstant(zoneId);

        // System.out.println(dateAndTimeInZone);
        System.out.println(dateAndTimeInZone.toLocalDateTime());
        // System.out.println(dateAndTimeInZone.toOffsetDateTime());
        // System.out.println(formatter.format(dateAndTimeInZone));

    }
}