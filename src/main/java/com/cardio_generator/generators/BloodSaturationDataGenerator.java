package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * The {@code BloodSaturationDataGenerator} class is responsible for
 * generating random data for a desired number of patients passed through
 * the constructor. Some random variations can be applied to the initial 
 * data to simulate real-time changes.
 * <p>Implements the {@link PatientDataGenerator} interface by providing
 * an implementation of the {@code generate} method.
 */

public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;


    /**
     * Constructs a {@code BloodSaturationDataGenerator} for the specified
     * number of patients. Initializes the {@code lastSaturationValues} 
     * with some random values from 95 to 100 for each patient.
     * @param patientCount the number of patients to simulate
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }


    /**
     * Generates and outputs a new value for the blood saturation
     * for the given patient. The new value is the current value,
     * possibly modified by 1 and it is between 90 and 100.
     * @param patientId the id for the specific patient
     * @param outputStrategy the strategy that is used to output the data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
