package com.erod.assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import com.erod.assignment.geolocation.GeoLocationFactory;
import com.erod.assignment.geolocation.TimeZoneAPI;
import com.erod.assignment.model.VehicleData;
import com.erod.assignment.model.VehicleData.VehicleDataBuilder;
import com.erod.assignment.workers.ReaderThread;
import com.erod.assignment.workers.WriterThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessVehicleData {

    private static final Logger logger = LoggerFactory.getLogger(ProcessVehicleData.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");
    private final TimeZoneAPI geoLocation;

    public ProcessVehicleData() {
        this(GeoLocationFactory.getTimeZoneAPI("offline"));
    }

    public ProcessVehicleData(TimeZoneAPI geoLocation) {
        this.geoLocation = geoLocation;
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

    /*
     * Process CSV file and output the formatted lines to logger now.
     */
    public void process(String path) throws IOException {
        logger.info("Processing CSV file... " + path);
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                Optional<VehicleData> dataOptional = getVehicleDataFromRow(line);
                dataOptional.ifPresent(data -> writeFormattedVehicleData(data));
            }
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Total time taken: " + elapsedTime);
    }

    public Optional<VehicleData> getVehicleDataFromRow(String row) {
        VehicleDataBuilder vehicleDataBuilder = new VehicleDataBuilder();

        // Note: indexOf is slightly better than StringTokenizer. Hence using
        // string.indexOf than StringTokenizer. StringTokenizer code block is commented
        // below.
        String[] values = split(row, ",");
        int columnIndex = 1;
        if (values != null) {
            int len = values.length;
            for (int i = 0; i < len; i++) {
                String token = values[i];
                try {
                    formatCellData(token, CSVDataType.getType(columnIndex), vehicleDataBuilder);
                } catch (Throwable t) {
                    // Assume that 1 row failed , but continue to process subsequent rows.
                    // If possible add rowNumber.
                    String message = "Failed to process row: " + row;
                    logger.error(message, t);
                    return Optional.empty();
                }
                columnIndex++;
            }
        }

        // StringTokenizer st = new StringTokenizer(row, ",");
        // int columnIndex = 1;
        // while (st.hasMoreElements()) {
        // String token = st.nextToken();
        // try {
        // formatCellData(token, CSVDataType.getType(columnIndex),
        // vehicleDataBuilder);
        // } catch (Throwable t) {
        // // Assume that 1 row failed , but continue to process subsequent rows.
        // // If possible add rowNumber.
        // String message = "Failed to process row: " + row;
        // logger.error(message, t);
        // return Optional.empty();
        // }

        // columnIndex++;
        // }

        VehicleData vehicleData = vehicleDataBuilder.build();
        ZoneId zoneId = this.geoLocation.findTimeZoneFromLocation(vehicleData.getLattitude(),
                vehicleData.getLongitude());
        vehicleData.processTimezoneData(zoneId);
        return Optional.ofNullable(vehicleData);
    }

    public void writeFormattedVehicleData(VehicleData data) {
        String line = prettyPrintVehicleData(data);
        System.out.println(line);
    }

    private void formatCellData(String token, CSVDataType type, VehicleDataBuilder builder) {
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

    public String prettyPrintVehicleData(VehicleData vehicleData) {
        return vehicleData.toString();
    }

    public static String[] split(final String line, final String delimiter) {
        int i = 0, index = 0;
        int j = line.indexOf(delimiter);
        String sub = null;
        String[] split = new String[3];
        while (j >= 0) {
            sub = line.substring(i, j);
            split[index++] = sub;
            i = j + 1;
            j = line.indexOf(",", i);
        }
        sub = line.substring(i);
        split[index++] = sub;
        return split;
        /*
         * CharSequence[] temp = new CharSequence[(line.length() / 2) + 1]; int
         * wordCount = 0; int i = 0; int j = line.indexOf(delimiter, 0); // first
         * substring
         * 
         * while (j >= 0) { temp[wordCount++] = line.substring(i, j); i = j + 1; j =
         * line.indexOf(delimiter, i); // rest of substrings }
         * 
         * temp[wordCount++] = line.substring(i); // last substring
         * 
         * String[] result = new String[wordCount]; System.arraycopy(temp, 0, result, 0,
         * wordCount);
         * 
         * return result;
         */
    }

    /*
     * Process rows and write processed data to file
     */
    public void processAndWriteToFile(String path) throws IOException {
        logger.info("Processing CSV file... " + path);
        long startTime = System.currentTimeMillis();
        String outputFileName = "vehicleFormatted.csv";

        try (FileWriter fileWriter = new FileWriter(outputFileName);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                Optional<VehicleData> dataOptional = getVehicleDataFromRow(line);
                dataOptional.ifPresent(data -> {
                    try {
                        bufferedWriter.write(prettyPrintVehicleData(data));
                        bufferedWriter.newLine();
                    } catch (Exception e) {
                        throw new RuntimeException("Error while writig to file");
                    }

                });
            }
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Total time taken: " + elapsedTime);
    }

    /*
     * Use two threads - one for read & one for write & blocking queue to process
     * rows
     */
    public void processWithWorkerThreads(String path) throws InterruptedException, IOException {
        logger.info("Processing CSV file with reader and write thread");
        long startTime = System.currentTimeMillis();

        CountDownLatch cdl = new CountDownLatch(1);

        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1024);
        ReaderThread reader = new ReaderThread(queue, path);
        WriterThread writer = new WriterThread(queue, cdl);

        new Thread(reader).start();
        new Thread(writer).start();
        cdl.await();

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Total time taken: " + elapsedTime);
    }

}