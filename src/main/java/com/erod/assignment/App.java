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
            // new ProcessVehicleData().process(getFile()); //Write to logger output
            new ProcessVehicleData().processAndWriteToFile(getFile()); // Write to file
            // new ProcessVehicleData().processWithWorkerThreads(getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Successfully processed csv file with vehicle data.");
    }

    static String getFile() {
        java.net.URL url = App.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new RuntimeException("Unable to find file");
        }

        return url.getFile();
    }

}
