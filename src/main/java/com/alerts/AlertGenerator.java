package com.alerts;

import java.util.*;

import com.alerts.alertFactories.AlertFactory;
import com.alerts.alertFactories.BloodOxygenAlertFactory;
import com.alerts.alertFactories.BloodPressureAlertFactory;
import com.alerts.alertFactories.DefaultAlertFactory;
import com.alerts.alertFactories.ECGAlertFactory;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {

    private AlertFactory bloodPressureFactory = new BloodPressureAlertFactory();
    private AlertFactory bloodOxygenFactory = new BloodOxygenAlertFactory();
    private AlertFactory ecgFactory = new ECGAlertFactory();
    private AlertFactory defaultFactory = new DefaultAlertFactory();

    private final Map<String, AlertStrategy> strategyMap = new HashMap<>();
    {
        BloodPressureStrategy bpStrategy = new BloodPressureStrategy();
        strategyMap.put("SystolicPressure", bpStrategy);
        strategyMap.put("DiastolicPressure", bpStrategy);
        strategyMap.put("OxygenSaturation", new OxygenSaturationStrategy());
        strategyMap.put("ECG", new HeartRateStrategy());
    }

    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the {@link #triggerAlert}
     * method. This method defines the specific conditions under which an alert will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        for (PatientRecord record : records) {
            AlertStrategy strategy = strategyMap.get(record.getRecordType());
            if (strategy != null) {
                strategy.checkAlert(record, patient, triggeredAlerts);
            }

            evaluateManual(patient, record);
            evaluateCombined(patient, record);
        }
    }

    /**
     * Helper method to evaluate manual alert types.
     */
    private void evaluateManual(Patient patient, PatientRecord record) {
        String type = record.getRecordType();
        if (type.equals("Alert") || type.equals("ManualAlert")) {
            Alert base = defaultFactory.createAlert(
                String.valueOf(patient.getPatientId()),
                "Manual Alert Triggered",
                record.getTimestamp()
            );
            triggerAlert(new PriorityAlertDecorator(new RepeatedAlertDecorator(base, 2)));
        }
    }

    /**
     * Helper method to trigger a combined alert when systolic pressure and oxygen levels are both low.
     */
    private void evaluateCombined(Patient patient, PatientRecord record) {
        if (!record.getRecordType().equals("SystolicPressure") || record.getMeasurementValue() >= 90) return;

        List<PatientRecord> oxygenRecords = patient.getRecords(0, record.getTimestamp());
        double latestOxygen = -1;
        for (PatientRecord r : oxygenRecords) {
            if (r.getRecordType().equals("OxygenSaturation")) {
                latestOxygen = r.getMeasurementValue();
            }
        }

        if (latestOxygen < 92) {
            Alert base = defaultFactory.createAlert(
                String.valueOf(patient.getPatientId()),
                "Hypotensive Hypoxemia (Sys < 90 & Oxy < 92)",
                record.getTimestamp()
            );
            triggerAlert(new PriorityAlertDecorator(new RepeatedAlertDecorator(base, 2)));
        }
    }

    /**
     * Checks if the last three values in a list show a consistent increasing or decreasing trend
     */
    private boolean isConsistentTrend(List<Double> values) {
        return (values.get(1) - values.get(0) > 10 && values.get(2) - values.get(1) > 10) ||
               (values.get(0) - values.get(1) > 10 && values.get(1) - values.get(2) > 10);
    }

    /**
     * Triggers an alert
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
    }

    /**
     * Returns all alerts that were triggered
     */
    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }
}
