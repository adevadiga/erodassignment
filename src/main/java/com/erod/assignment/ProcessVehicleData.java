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

import com.erod.assignment.geolocation.OfflineTimeshapeTimeZone;
import com.erod.assignment.geolocation.TimeZoneAPI;
import com.erod.assignment.model.VehicleData;
import com.erod.assignment.model.VehicleData.VehicleDataBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessVehicleData {

    private static final Logger logger = LoggerFactory.getLogger(ProcessVehicleData.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");
    private final TimeZoneAPI geoLocation;

    public ProcessVehicleData() {
        this.geoLocation = new OfflineTimeshapeTimeZone();
    }

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

        public static CSVDataType getType(int code) {
            return lookup.get(code);
        }
    }

    public void process(String path) throws IOException {
        logger.info("Processing CSV file...");
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                VehicleData data = getVehicleDataFromRow(line);
                writeFormattedVehicleData(data);
            }
        }
    }

    private VehicleData getVehicleDataFromRow(String row) {
        VehicleDataBuilder vehicleDataBuilder = new VehicleDataBuilder();

        StringTokenizer st = new StringTokenizer(row, ",");
        int columnIndex = 1;
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            readAndFormatCellData(token, CSVDataType.getType(columnIndex), vehicleDataBuilder);
            columnIndex++;
        }

        VehicleData vehicleData = vehicleDataBuilder.build();
        ZoneId zoneId = this.geoLocation.findTimeZoneFromLocation(vehicleData.getLattitude(),
                vehicleData.getLongitude());

        vehicleData.processTimezoneData(zoneId);
        return vehicleData;
    }

    private void writeFormattedVehicleData(VehicleData data) {
        prettyPrintVehicleData(data);
    }

    /*
     * private void processAndFormatData(String row) { VehicleDataBuilder
     * vehicleDataBuilder = new VehicleDataBuilder();
     * 
     * StringTokenizer st = new StringTokenizer(row, ","); int columnIndex = 1;
     * while (st.hasMoreElements()) { String token = st.nextToken();
     * readAndFormatCellData(token, CSVDataType.getType(columnIndex),
     * vehicleDataBuilder); columnIndex++; }
     * 
     * VehicleData vehicleData = vehicleDataBuilder.build();
     * prettyPrintVehicleData(vehicleData); }
     */

    private void readAndFormatCellData(String token, CSVDataType type, VehicleDataBuilder builder) {
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

    public void prettyPrintVehicleData(VehicleData vehicleData) {
        ZoneId zoneId = this.geoLocation.findTimeZoneFromLocation(vehicleData.getLattitude(),
                vehicleData.getLongitude());
        String zoneName = zoneId.toString();

        ZonedDateTime dateAndTimeInZone = vehicleData.getDateTimeInUTC().withZoneSameInstant(zoneId);
        LocalDateTime dateTime = dateAndTimeInZone.toLocalDateTime();

        String toPrint = vehicleData.getDateTimeInUTC() + "," + vehicleData.getLattitude() + ","
                + vehicleData.getLongitude() + "," + zoneName + "," + dateTime;
        System.out.println(toPrint);
    }
}