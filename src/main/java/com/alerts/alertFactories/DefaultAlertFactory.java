package com.alerts.alertFactories;

import com.alerts.Alert;

public class DefaultAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp);
    }
}
