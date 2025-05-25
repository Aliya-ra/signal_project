package com.alerts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        List<Double> diaTrend = new ArrayList<>();
        List<Double> sysTrend = new ArrayList<>();
        TreeMap<Long, Double> oxygenMap = new TreeMap<>();
        double latestOxygen = -1;

        List<Double> ecgWindow = new ArrayList<>();
        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();
            long time = record.getTimestamp();

            // --- BLOOD PRESSURE ALERTS ---
            if (type.equals("DiastolicPressure")) {
                // Critical threshold
                if (value < 60 || value > 120) {
                    triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Critical Diastolic: " + value,
                        time
                    ));
                }
                // Trend alert
                diaTrend.add(value);
                if (diaTrend.size() > 3) diaTrend.remove(0);
                if (diaTrend.size() == 3 && isConsistentTrend(diaTrend)) {
                    triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Diastolic Trend: " + diaTrend,
                        time
                    ));
                }        
            } else if (type.equals("SystolicPressure")) {
                if (value < 90 || value > 180) {
                    triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Critical Systolic: " + value,
                        time
                    ));
                }
                sysTrend.add(value);
                if (sysTrend.size() > 3) sysTrend.remove(0);
                if (sysTrend.size() == 3 && isConsistentTrend(sysTrend)) {
                    triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Systolic Trend: " + sysTrend,
                        time
                    ));
                }
            }

            // --- BLOOD OXYGEN ALERTS ---
            else if (type.equals("OxygenSaturation")) {
                if (value < 92) {
                    triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Low Oxygen Saturation: " + value,
                        time
                    ));
                }

                // Rapid drop check
                oxygenMap.put(time, value);

                SortedMap<Long, Double> last10min = oxygenMap.subMap(time - 600_000, time); // Last 10 minutes
                if (!last10min.isEmpty()) {
                    double oldest = last10min.values().iterator().next(); // oldest value in window
                    if (oldest - value >= 5) {
                        triggerAlert(new Alert(
                            String.valueOf(patient.getPatientId()),
                            "Rapid Oxygen Drop: " + oldest + " → " + value,
                            time
                        ));
                    }
                }
            }
            latestOxygen = value;
        

            // --- COMBINED ALERT ---
            if (type.equals("SystolicPressure") && value < 90 && latestOxygen < 92) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()),
                    "Hypotensive Hypoxemia (Sys < 90 & Oxy < 92)",
                    time
                ));
            }

            // --- ECG ALERT ---
            else if (type.equals("ECG")) {
                double avg = ecgWindow.stream().mapToDouble(d -> d).average().orElse(0);

                if (avg > 0 && value > 1.5 * avg) {
                    triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "ECG Spike Detected: " + value + " (avg: " + avg + ")",
                        time
                    ));
                }

                ecgWindow.add(value);
                if (ecgWindow.size() > 5) ecgWindow.remove(0);
            }


            // --- MANUAL ALERT ---
            else if (type.equals("Alert") || type.equals("ManualAlert")) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()),
                    "Manual Alert Triggered",
                    time
                ));
            }
        }
    }

    /**
     * Checks if the last three values in a list show a consistent trend, either
     * increasing or decreasing by more than 10 units.
     *
     * @param values the list of values to check for a consistent trend
     * @return true if the trend is consistent, false otherwise
     */
    private boolean isConsistentTrend(List<Double> values) {
    return (values.get(1) - values.get(0) > 10 && values.get(2) - values.get(1) > 10) ||
           (values.get(0) - values.get(1) > 10 && values.get(1) - values.get(2) > 10);
    }


    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
    }

    /**
     * Retrieves the list of all triggered alerts. This can be used for reporting,
     * analysis, or further processing.
     *
     * @return a list of {@link Alert} objects that have been triggered
     */
    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }
}
