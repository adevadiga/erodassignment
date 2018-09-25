package com.erod.assignment.model;

import java.time.ZonedDateTime;

public class Vehicle {

    private ZonedDateTime dateTime;
    private double longitude;
    private double lattitude;

    /**
     * @return the dateTime
     */
    public ZonedDateTime getDateTime() {
        return dateTime;
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

    private Vehicle(VehicleDataBuilder builder) {
        this.dateTime = builder.dateTime;
        this.longitude = builder.longitude;
        this.lattitude = builder.lattitude;
    }

    public static class VehicleDataBuilder {
        private ZonedDateTime dateTime;
        private double longitude;
        private double lattitude;

        public VehicleDataBuilder setDateTime(ZonedDateTime dateTime) {
            this.dateTime = dateTime;
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

        public Vehicle build() {
            return new Vehicle(this);
        }

    }

}