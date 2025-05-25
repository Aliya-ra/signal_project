package com.data_management;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class FileDataReader implements BatchDataReader {
    private final String directory;

    public FileDataReader(String directory) {
        this.directory = directory;
    }

    @Override
    public void readData(DataStorage storage) throws IOException {
        Path dirPath = Paths.get(directory);
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            System.err.println("Invalid directory: " + directory);
            return;
        }

        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.getFileName().toString().endsWith(".txt"))
                 .forEach(path -> readFile(path, storage));
        }
    }

    private void readFile(Path path, DataStorage storage) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {

                try {
                    String[] splits = line.split(",\\s*");
                    if (splits.length != 4) {
                        System.err.println("Invalid record format: " + line + " in " + path);
                        continue;
                    }

                    int patientId = Integer.parseInt(splits[0].split(":", 2)[1].trim());
                    long timestamp = Long.parseLong(splits[1].split(":", 2)[1].trim());
                    String label = splits[2].split(":", 2)[1].trim();
                    String rawValue = splits[3].split(":", 2)[1].trim().replace("%", "");

                    double value;

                    // Handle alert records with non-numeric values gracefully
                    if (label.equalsIgnoreCase("Alert") || label.equalsIgnoreCase("ManualAlert")) {
                        try {
                            value = Double.parseDouble(rawValue); // just in case it's numeric
                        } catch (NumberFormatException e) {
                            value = 0.0; // fallback dummy value
                        }
                    } else {
                        value = Double.parseDouble(rawValue);
                    }

                    storage.addPatientData(patientId, value, label, timestamp);

                } catch (Exception e) {
                    System.err.println("Invalid number format: " + line + " in " + path);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + path);
        }
    }


}
