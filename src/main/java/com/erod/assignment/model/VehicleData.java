package com.erod.assignment.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class VehicleData {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");

    private ZonedDateTime dateTimeInUTC;
    private double longitude;
    private double lattitude;
    private String timezone;
    private LocalDateTime dateTimeinZone;

    /**
     * @return the dateTime
     */
    public ZonedDateTime getDateTimeInUTC() {
        return dateTimeInUTC;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return the lattitude
     */
    public double getLattitude() {
        return lattitude;
    }

    /**
     * @return the timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * @return the dateTimeinZone
     */
    public LocalDateTime getDateTimeinZone() {
        return dateTimeinZone;
    }

    private VehicleData(VehicleDataBuilder builder) {
        this.dateTimeInUTC = builder.dateTimeInUTC;
        this.longitude = builder.longitude;
        this.lattitude = builder.lattitude;
    }

    public static class VehicleDataBuilder {
        private ZonedDateTime dateTimeInUTC;
        private double longitude;
        private double lattitude;

        public VehicleDataBuilder setDateTimeInUTC(ZonedDateTime dateTime) {
            this.dateTimeInUTC = dateTime;
            return this;
        }

        public VehicleDataBuilder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public VehicleDataBuilder setLattitude(double lattitude) {
            this.lattitude = lattitude;
            return this;
        }

        public VehicleData build() {
            return new VehicleData(this);
        }

    }

    public void processTimezoneData(ZoneId zoneId) {
        this.timezone = zoneId.toString();

        if (this.dateTimeInUTC == null) {
            System.out.println(this.dateTimeInUTC);
        }

        if (this.dateTimeInUTC != null) {
            ZonedDateTime dateAndTimeInZone = this.dateTimeInUTC.withZoneSameInstant(zoneId);
            LocalDateTime dateTime = dateAndTimeInZone.toLocalDateTime();
            this.dateTimeinZone = dateTime;
        }
    }

    public String toString() {
        ZonedDateTime zoneDT = this.dateTimeInUTC;
        String formattedTime = zoneDT.format(formatter);
        String toPrint = formattedTime + "," + this.lattitude + "," + this.longitude + "," + this.timezone + ","
                + this.dateTimeinZone;
        return toPrint;
    }

}