package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implements {@link #OutputStrategy} and it is responsible for
 * creating and writing in the file that the data for the patients are stored.
 */
public class FileOutputStrategy implements OutputStrategy {  //name of the class changed be UpperCamelCase
    private String baseDirectory; //changed to camelCase

    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>(); //name of the non-static finals should be lowerCamelCase


    /**
     * Constructs a {@code FileOutputStrategy} with a given directory to
     * save the files.
     * @param baseDirectory the directory that the files will be stored 
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    /**
     * Creates (if needed) and writes the data for the given patient
     * in a file for the corresponing label.
     * @param patientId the Id for the specific patient
     * @param timestamp the time that the data was generated
     * @param label the label that describes the type of data
     * @param data the data that needs to be stored
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString()); //FilePath should be lowerCamelCase => filePath

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}