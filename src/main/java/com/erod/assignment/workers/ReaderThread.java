package com.erod.assignment.workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import com.erod.assignment.ProcessVehicleData;
import com.erod.assignment.model.VehicleData;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ReaderThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ReaderThread.class);
    protected BlockingQueue<String> blockingQueue = null;
    String filename = null;

    public ReaderThread(BlockingQueue<String> blockingQueue, String fileName) {
        this.blockingQueue = blockingQueue;
        this.filename = fileName;
    }

    @Override
    public void run() {
        ProcessVehicleData processVehicleData = new ProcessVehicleData();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(filename)));
            String buffer = null;
            while ((buffer = br.readLine()) != null) {
                Optional<VehicleData> dataOptional = processVehicleData.getVehicleDataFromRow(buffer);
                dataOptional.ifPresent(data -> {
                    try {
                        blockingQueue.put(processVehicleData.prettyPrintVehicleData(data));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
            blockingQueue.put("EOF"); // When end of file has been reached

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}