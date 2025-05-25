package com.alerts;

import com.alerts.alertFactories.AlertFactory;
import com.alerts.alertFactories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.*;


public class BloodPressureStrategy implements AlertStrategy {

    private final AlertFactory factory = new BloodPressureAlertFactory();
    private final Map<Integer, List<Double>> systolicTrends = new HashMap<>();
    private final Map<Integer, List<Double>> diastolicTrends = new HashMap<>();

    @Override
    public boolean checkAlert(PatientRecord record, Patient patient, List<Alert> triggeredAlerts) {
        String type = record.getRecordType();
        if (!type.equals("SystolicPressure") && !type.equals("DiastolicPressure")) return false;

        String patientId = String.valueOf(patient.getPatientId());
        double value = record.getMeasurementValue();
        long time = record.getTimestamp();
        boolean triggered = false;

        // Critical thresholds
        if ((type.equals("SystolicPressure") && (value < 90 || value > 180)) ||
            (type.equals("DiastolicPressure") && (value < 60 || value > 120))) {
            triggeredAlerts.add(factory.createAlert(patientId, "Critical " + type + ": " + value, time));
            triggered = true;
        }

        // Trend detection
        Map<Integer, List<Double>> trendMap = type.equals("SystolicPressure") ? systolicTrends : diastolicTrends;
        List<Double> trend = trendMap.computeIfAbsent(patient.getPatientId(), id -> new ArrayList<>());
        trend.add(value);
        if (trend.size() > 3) trend.remove(0);
        if (trend.size() == 3 && isConsistentTrend(trend)) {
            String label = type.equals("SystolicPressure") ? "Systolic Trend" : "Diastolic Trend";
            triggeredAlerts.add(factory.createAlert(patientId, label + ": " + trend, time));

            triggered = true;
        }

        return triggered;
    }

    private boolean isConsistentTrend(List<Double> values) {
        return (values.get(1) - values.get(0) > 10 && values.get(2) - values.get(1) > 10) ||
               (values.get(0) - values.get(1) > 10 && values.get(1) - values.get(2) > 10);
    }
}
