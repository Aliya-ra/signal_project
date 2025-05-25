package data_management;

import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void testGetRecordsFiltersByTimestamp() {
        Patient patient = new Patient(1);

        long t1 = 1000;
        long t2 = 2000;
        long t3 = 3000;
        long t4 = 4000;

        patient.addRecord(70.0, "HeartRate", t1);
        patient.addRecord(75.0, "HeartRate", t2);
        patient.addRecord(80.0, "HeartRate", t3);
        patient.addRecord(85.0, "HeartRate", t4);

        // Query range: t2 to t3
        List<PatientRecord> filtered = patient.getRecords(t2, t3);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(r -> r.getTimestamp() >= t2 && r.getTimestamp() <= t3));
    }

    @Test
    void testGetPatientId() {
        Patient patient = new Patient(7);
        assertEquals(7, patient.getPatientId());
    }

    @Test
    void testTimeFilteringWorks() {
        Patient patient = new Patient(101);

        patient.addRecord(50.0, "ECG", 500L);
        patient.addRecord(60.0, "ECG", 1000L);
        patient.addRecord(70.0, "ECG", 1500L);

        List<PatientRecord> filtered = patient.getRecords(800L, 1300L);
        assertEquals(1, filtered.size());
        assertEquals(60.0, filtered.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testAddAndGetRecords() {
        Patient patient = new Patient(42);

        patient.addRecord(75.0, "HeartRate", 1000L);
        patient.addRecord(80.0, "HeartRate", 2000L);

        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        assertEquals(2, records.size());
        assertEquals("HeartRate", records.get(0).getRecordType());
    }

}
