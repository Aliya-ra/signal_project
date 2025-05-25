package com.alerts;

import com.alerts.alertFactories.AlertFactory;
import com.alerts.alertFactories.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.*;


public class HeartRateStrategy implements AlertStrategy {

    private final AlertFactory factory = new ECGAlertFactory();
    private final Map<Integer, LinkedList<Double>> ecgWindows = new HashMap<>();

    @Override
    public boolean checkAlert(PatientRecord record, Patient patient, List<Alert> triggeredAlerts) {
        if (!record.getRecordType().equals("ECG")) return false;

        String patientId = String.valueOf(patient.getPatientId());
        double value = record.getMeasurementValue();
        long time = record.getTimestamp();
        boolean triggered = false;

        LinkedList<Double> window = ecgWindows.computeIfAbsent(patient.getPatientId(), id -> new LinkedList<>());
        double avg = window.stream().mapToDouble(d -> d).average().orElse(0);

        if (avg > 0 && value > 1.5 * avg) {
            triggeredAlerts.add(factory.createAlert(patientId, "ECG Spike Detected: " + value + " (avg: " + avg + ")", time));
            triggered = true;
        }

        window.add(value);
        if (window.size() > 5) window.removeFirst();

        return triggered;
    }
}
