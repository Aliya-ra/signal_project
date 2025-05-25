package design_patterns;

import com.alerts.*;
import com.alerts.alertFactories.*;
import com.cardio_generator.HealthDataSimulator;
import com.data_management.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DesignPatternTest {

    // Factory Pattern Tests
    @Test
    void testBloodPressureFactoryCreatesCorrectAlert() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "Critical Diastolic", 1000L);
        assertTrue(alert.getCondition().startsWith("Blood Pressure Alert:"));
    }

    @Test
    void testBloodOxygenFactoryCreatesCorrectAlert() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("1", "Low Oxygen", 1000L);
        assertTrue(alert.getCondition().startsWith("Blood Oxygen Alert:"));
    }

    @Test
    void testECGFactoryCreatesCorrectAlert() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("1", "ECG Spike", 1000L);
        assertTrue(alert.getCondition().startsWith("ECG Alert:"));
    }

    // Strategy Pattern Test
    @Test
    void testOxygenStrategyTriggersLowAlert() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        List<Alert> alerts = new ArrayList<>();
        Patient patient = new Patient(1);
        PatientRecord record = new PatientRecord(1, 90.0, "OxygenSaturation", 123L);

        boolean triggered = strategy.checkAlert(record, patient, alerts);

        assertTrue(triggered);
        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getCondition().contains("Low Oxygen Saturation"));
    }

    // Singleton Pattern Tests
    @Test
    void testDataStorageSingletonReturnsSameInstance() {
        DataStorage a = DataStorage.getInstance();
        DataStorage b = DataStorage.getInstance();
        assertSame(a, b);
    }

    @Test
    void testHealthDataSimulatorSingletonReturnsSameInstance() {
        HealthDataSimulator a = HealthDataSimulator.getInstance();
        HealthDataSimulator b = HealthDataSimulator.getInstance();
        assertSame(a, b);
    }
}
