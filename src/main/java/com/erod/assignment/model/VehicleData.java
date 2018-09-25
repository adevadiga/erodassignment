package com.erod.assignment.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class VehicleData {

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
        ZonedDateTime dateAndTimeInZone = this.dateTimeInUTC.withZoneSameInstant(zoneId);
        LocalDateTime dateTime = dateAndTimeInZone.toLocalDateTime();
        this.dateTimeinZone = dateTime;
    }

}