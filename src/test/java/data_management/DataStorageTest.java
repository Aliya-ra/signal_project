package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.BatchDataReader;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.io.*;
import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() throws IOException {
        // DataReader reader
        BatchDataReader reader = new BatchDataReader() {
            @Override
            public void readData(DataStorage storage) {
                storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
                storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);
            }
        };
        DataStorage storage = DataStorage.forceNewInstance(reader);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }


    @Test
    void testTimeFiltering() {
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        storage.addPatientData(3, 88.0, "ECG", 1000L);
        storage.addPatientData(3, 89.0, "ECG", 2000L);
        storage.addPatientData(3, 90.0, "ECG", 3000L);

        List<PatientRecord> filtered = storage.getRecords(3, 1500L, 2500L);
        assertEquals(1, filtered.size());
        assertEquals(89.0, filtered.get(0).getMeasurementValue(), 0.001);
    }


    @Test
    void testGetAllPatients() {
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        storage.addPatientData(1, 90.0, "SystolicPressure", 1000L);
        storage.addPatientData(2, 75.0, "DiastolicPressure", 1000L);

        List<Patient> patients = storage.getAllPatients();
        assertEquals(2, patients.size());
    }


     @Test
    void testAddMultipleRecordsSamePatient() {
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        storage.addPatientData(2, 80.0, "DiastolicPressure", 1000L);
        storage.addPatientData(2, 85.0, "DiastolicPressure", 2000L);

        List<PatientRecord> records = storage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(2, records.size());
    }


    @Test
    void testAddAndRetrieveSingleRecord() {
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);

        assertEquals(1, records.size());
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertEquals(120.0, records.get(0).getMeasurementValue(), 0.001);
    }

}
