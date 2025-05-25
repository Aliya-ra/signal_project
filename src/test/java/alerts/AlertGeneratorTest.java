package alerts;

import com.data_management.*;
import com.alerts.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {

    DataReader testReader = dataStorage -> {
        // Do nothing; we'll insert patients manually in each test
    };

    @Test
    void testCriticalDiastolicAlertTriggered() {
        DataStorage storage = DataStorage.getInstance();
        storage.reset();
        
        Patient patient = new Patient(1);
        patient.addRecord(130.0, "DiastolicPressure", System.currentTimeMillis());
        storage.addPatient(1, patient);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getCondition().contains("Critical Diastolic"));
    }

    @Test
    void testHypotensiveHypoxemiaAlertTriggered() {
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        Patient patient = new Patient(2);
        long now = System.currentTimeMillis();

        patient.addRecord(88.0, "SystolicPressure", now);
        patient.addRecord(91.0, "OxygenSaturation", now + 1);
        storage.addPatient(2, patient);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        List<Alert> alerts = generator.getTriggeredAlerts();
        List<String> conditions = alerts.stream()
                                        .map(Alert::getCondition)
                                        .collect(Collectors.toList());

        assertEquals(3, alerts.size()); // Because systolic, oxygen, and combined triggered
        assertTrue(conditions.stream().anyMatch(c -> c.contains("Critical Systolic")));
        assertTrue(conditions.stream().anyMatch(c -> c.contains("Low Oxygen Saturation")));
        assertTrue(conditions.stream().anyMatch(c -> c.contains("Hypotensive Hypoxemia")));
    }

    @Test
    void testRapidOxygenDropAlertTriggered() {
        DataReader dummyReader = ds -> {};
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        Patient patient = new Patient(4);

        long t1 = System.currentTimeMillis();
        long t2 = t1 + 300_000; // 5 minutes later

        patient.addRecord(97.0, "OxygenSaturation", t1);
        patient.addRecord(91.0, "OxygenSaturation", t2);

        storage.addPatient(4, patient);
        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        List<String> conditions = generator.getTriggeredAlerts()
            .stream()
            .map(Alert::getCondition)
            .toList();

        assertTrue(conditions.stream().anyMatch(c -> c.contains("Rapid Oxygen Drop")));
    }

    @Test
    void testSystolicTrendAlertTriggered() {
        DataReader dummyReader = ds -> {};
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        Patient patient = new Patient(3);

        long t = System.currentTimeMillis();
        patient.addRecord(100.0, "SystolicPressure", t);
        patient.addRecord(111.0, "SystolicPressure", t + 1);
        patient.addRecord(123.0, "SystolicPressure", t + 2);

        storage.addPatient(3, patient);
        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        List<String> conditions = generator.getTriggeredAlerts()
            .stream()
            .map(Alert::getCondition)
            .toList();

        assertTrue(conditions.stream().anyMatch(c -> c.contains("Systolic Trend")));
    }

    @Test
    void testECGSpikeAlertTriggered() {

        DataReader dummyReader = ds -> {};
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        Patient patient = new Patient(5);
        long now = System.currentTimeMillis();

        // Add 5 normal ECG readings
        patient.addRecord(100.0, "ECG", now);
        patient.addRecord(102.0, "ECG", now + 1);
        patient.addRecord(99.0, "ECG", now + 2);
        patient.addRecord(98.0, "ECG", now + 3);
        patient.addRecord(101.0, "ECG", now + 4);

        // Add 1 spike
        patient.addRecord(170.0, "ECG", now + 5);

        storage.addPatient(5, patient);
        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        List<String> conditions = generator.getTriggeredAlerts()
            .stream()
            .map(Alert::getCondition)
            .toList();

        assertTrue(conditions.stream().anyMatch(c -> c.contains("ECG Spike Detected")));
    }

    @Test
    void testManualAlertTriggered() {
        DataReader dummyReader = ds -> {};
        DataStorage storage = DataStorage.getInstance();
        storage.reset();

        Patient patient = new Patient(6);

        patient.addRecord(1.0, "Alert", System.currentTimeMillis()); // Value is irrelevant
        storage.addPatient(6, patient);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        List<String> conditions = generator.getTriggeredAlerts()
            .stream()
            .map(Alert::getCondition)
            .toList();

        assertTrue(conditions.stream().anyMatch(c -> c.contains("Manual Alert Triggered")));
    }

     @Test
    void testPriorityDecoratorAddsTag() {
        Alert base = new Alert("123", "Low Oxygen Saturation", 1000L);
        Alert decorated = new PriorityAlertDecorator(base);

        assertEquals("123", decorated.getPatientId());
        assertEquals("[PRIORITY] Low Oxygen Saturation", decorated.getCondition());
        assertEquals(1000L, decorated.getTimestamp());
    }


     @Test
    void testRepeatedDecoratorAddsSuffix() {
        Alert base = new Alert("456", "ECG Spike Detected", 2000L);
        Alert decorated = new RepeatedAlertDecorator(base, 3);

        assertEquals("456", decorated.getPatientId());
        assertEquals("ECG Spike Detected (Repeated 3x)", decorated.getCondition());
        assertEquals(2000L, decorated.getTimestamp());
    }


    @Test
    void testCombinedDecorator() {
        Alert base = new Alert("789", "Critical Diastolic", 3000L);
        Alert decorated = new PriorityAlertDecorator(new RepeatedAlertDecorator(base, 2));

        assertEquals("789", decorated.getPatientId());
        assertEquals("[PRIORITY] Critical Diastolic (Repeated 2x)", decorated.getCondition());
        assertEquals(3000L, decorated.getTimestamp());
    }

}
