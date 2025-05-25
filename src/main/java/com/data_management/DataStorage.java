package com.data_management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alerts.AlertGenerator;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private static DataStorage instance; // Singleton instance
    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage(BatchDataReader reader) {
        this.patientMap = new HashMap<>();
        try {
            reader.readData(this); // auto-load data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of DataStorage. Initializes it with a default
     * reader if needed.
     *
     * @return the singleton instance
     */
    public static DataStorage getInstance() {
        if (instance == null) {
            String path = "output";
            BatchDataReader reader = new FileDataReader(path);
            instance = new DataStorage(reader);
        }
        return instance;
    }

    /**
     * Forces a new instance of DataStorage. Useful for test scenarios.
     *
     * @param reader the DataReader to initialize with
     * @return a new instance replacing the previous one
     */
    public static DataStorage forceNewInstance(BatchDataReader reader) {
        instance = new DataStorage(reader);
        return instance;
    }

    /**
     * Clears all stored patient data.
     */
    public void reset() {
        patientMap.clear();
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.get(patientId);
        if (patient == null) {
            patient = new Patient(patientId);
            patientMap.put(patientId, patient);
        }
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * Adds a new patient to the storage.
     * If the patient already exists, it will be updated with the new data.
     *
     * @param id      the unique identifier for the patient
     * @param patient the Patient object containing the patient's data
     */
    public void addPatient(int id, Patient patient) {
        patientMap.put(id, patient);
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        DataStorage storage = DataStorage.getInstance();

        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId() +
                    ", Type: " + record.getRecordType() +
                    ", Data: " + record.getMeasurementValue() +
                    ", Timestamp: " + record.getTimestamp());
        }

        AlertGenerator alertGenerator = new AlertGenerator(storage);

        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }


    /**
     * Clears all stored patient data.
     */
    public void clearAllData() {
        patientMap.clear();
    }

    /**
     * Retrieves a patient by their unique identifier.
     *
     * @param patientId the unique identifier of the patient
     * @return the Patient object if found, null otherwise
     */
    public Patient getPatient(int patientId) {
        return patientMap.get(patientId);
    }

}
