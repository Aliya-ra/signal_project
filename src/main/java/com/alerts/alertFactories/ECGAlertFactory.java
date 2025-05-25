package com.alerts.alertFactories;

import com.alerts.Alert;

public class ECGAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "ECG Alert: " + condition, timestamp);
    }
}
