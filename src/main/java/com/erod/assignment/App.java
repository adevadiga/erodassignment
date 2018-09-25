package com.erod.assignment;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App {

    public static final String fileName = "vehicle.csv";

    public static void main(String[] args) {

        System.out.println("Hello World!");
        try {
            new ProcessVehicleData().process(new App().getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getFile() {
        return this.getClass().getClassLoader().getResource(fileName).getFile();
    }

}
