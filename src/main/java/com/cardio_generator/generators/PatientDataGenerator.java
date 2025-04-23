package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This interface represents data generators for patients 
 * and have a {@code generate} method to modify the data for
 * the given patient and output it.
 */
public interface PatientDataGenerator {

    /**
     * The method gets a patientId and outputs it using an outputStrategy.
     * It might modify the data befor outputing it, but that depends on the
     * implementations in the classes.
     * @param patientId for the specific patient
     * @param outputStrategy used to output the data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
