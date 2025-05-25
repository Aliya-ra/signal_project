package com.alerts;

import com.alerts.alertFactories.AlertFactory;
import com.alerts.alertFactories.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.*;


public class OxygenSaturationStrategy implements AlertStrategy {

    private final AlertFactory factory = new BloodOxygenAlertFactory();
    private final Map<Integer, TreeMap<Long, Double>> patientOxygenHistory = new HashMap<>();

    @Override
    public boolean checkAlert(PatientRecord record, Patient patient, List<Alert> triggeredAlerts) {
        if (!record.getRecordType().equals("OxygenSaturation")) return false;

        String patientId = String.valueOf(patient.getPatientId());
        double value = record.getMeasurementValue();
        long time = record.getTimestamp();
        boolean triggered = false;

        // Low oxygen alert
        if (value < 92) {
            triggeredAlerts.add(factory.createAlert(patientId, "Low Oxygen Saturation: " + value, time));
            triggered = true;
        }

        // Rapid drop check (in last 10 minutes)
        TreeMap<Long, Double> history = patientOxygenHistory.computeIfAbsent(patient.getPatientId(), id -> new TreeMap<>());
        history.put(time, value);

        SortedMap<Long, Double> last10min = history.subMap(time - 600_000, time);
        if (!last10min.isEmpty()) {
            double oldest = last10min.values().iterator().next();
            if (oldest - value >= 5) {
                triggeredAlerts.add(factory.createAlert(patientId, "Rapid Oxygen Drop: " + oldest + " → " + value, time));
                triggered = true;
            }
        }

        return triggered;
    }
}
