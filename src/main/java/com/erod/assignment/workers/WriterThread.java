package com.erod.assignment.workers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class WriterThread implements Runnable {

    protected BlockingQueue<String> blockingQueue = null;
    private final CountDownLatch stopLatch;

    public WriterThread(BlockingQueue<String> blockingQueue, CountDownLatch stopLatch) {
        this.blockingQueue = blockingQueue;
        this.stopLatch = stopLatch;
    }

    @Override
    public void run() {
        String outputFileName = "vehicleFormatted_Th.csv";
        try (FileWriter fileWriter = new FileWriter(outputFileName);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            while (true) {
                String buffer = blockingQueue.take();
                // Check whether end of file has been reached
                if (buffer.equals("EOF")) {
                    this.stopLatch.countDown();// Should do it in finally
                    break;
                }
                bufferedWriter.write(buffer);
                bufferedWriter.newLine();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}