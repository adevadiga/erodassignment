# EROD Assignment

Running locally

1. Clone the repo
2. cd erodassignment
3. mvn compile
4. mvn exec:java -Dexec.mainClass="com.erod.assignment.App"

Currently the code uses the file "vehicle.csv" file inside src/main/resource as input.

When you run the main class it processes the file, applied the transformation and writes the formatted data to "vehicleFormatted.csv" file inside root folder.


## Different flows

1. The code can write output to csv file
     Input: src/main/resource/vehicle.csv
     Output: vehicleFormatted.csv
     Class/Method: ProcessVehicleData.processAndWriteToFile()

2. The code can write transformation to logger
    Input: src/main/resource/vehicle.csv
    Ouput: Logger
    Class/Method: ProcessVehicleData.process()
    
3. The code uses two threads. One thread for reading file and another for writing to file. Both file operate on a BlockingQueue.
    Input: src/main/resource/vehicle.csv
    Ouput: vehicleFormatted_Th.csv
    Class/Method: ProcessVehicleData.processWithWorkerThreads()
