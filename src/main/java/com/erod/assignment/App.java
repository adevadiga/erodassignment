package com.erod.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a CSV with a UTC datetime, latitude and longitude columns and append the
 * timezone the vehicle is in and the localised datetime.
 *
 */
public class App {

    public static final String fileName = "vehicle.csv";
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Starting processing csv file with vehicle data");
        try {
            new ProcessVehicleData().process(getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Successfully processed csv file with vehicle data.");
    }

    static String getFile() {
        return App.class.getClassLoader().getResource(fileName).getFile();
    }

}
