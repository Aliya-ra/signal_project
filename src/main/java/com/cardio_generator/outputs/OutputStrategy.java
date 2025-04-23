package com.cardio_generator.outputs;

/**
 * The {@code OutputStrategy} interface represents the strategy used to output
 * the data for the given patient. 
 */
public interface OutputStrategy {
    /**
     * Outputs the specific type of data for the given patient,
     * specified by label and timestamp.
     * @param patientId for the specific patient
     * @param timestamp for the datapoint in milliseconds
     * @param label to describe the type of data
     * @param data the data that needs to be output
     */
    void output(int patientId, long timestamp, String label, String data);
}
