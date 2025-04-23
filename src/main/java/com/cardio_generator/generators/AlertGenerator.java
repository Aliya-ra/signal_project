package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;


/**
 * The {@code AlertGenerator} class is responsible for the alert for patients
 * getting triggered or resolved. It implements {@code PatientDataGenerator} and
 * simulates the alert generation for patients.
 */
public class AlertGenerator implements PatientDataGenerator {

    public static final Random RANDOM_GENERATOR = new Random(); // final static should be ALL_CAPS_WITH_UNDERSCORE
    // AlertStates changed to alertStates (camelCase)
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs an AlertGenerator initializes the {@code alertStates}
     * with an array of booleans all set to false with the size of
     * patientCount + 1.
     * @param patientCount the number of patients
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * It sumulates the alert for a specific patient and checks if it
     * triggers or resolves. It does that based on probabilities and 
     * randomness.
     * @param patientId the id for the specific patient
     * @param outputStrategy the strategy used for output
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Lambda changed to lambda (camelCase)
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
