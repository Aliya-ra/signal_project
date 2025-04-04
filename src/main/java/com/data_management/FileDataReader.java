package com.data_management;

import java.io.*;
import java.util.Scanner;

public class FileDataReader implements DataReader {
    private String filePath;

    public FileDataReader(String filePath) {
        this.filePath = filePath;
    }
//
//    @Override
    public void readData(DataStorage storage) throws IOException {
//        try (Scanner scanner = new Scanner(new File(filePath))) {
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine();
//                String[] parts = line.split(",");
//                int patientId = Integer.parseInt(parts[0]);
//                double value = Double.parseDouble(parts[1]);
//                String type = parts[2];
//                long timestamp = Long.parseLong(parts[3]);
//                storage.addPatientData(patientId, value, type, timestamp);
//            }
//        }
    }
}
