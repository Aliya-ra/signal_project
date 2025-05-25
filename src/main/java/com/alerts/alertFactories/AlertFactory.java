package com.alerts.alertFactories;

import com.alerts.Alert;

public interface AlertFactory {


    Alert createAlert(String patientId, String condition, long timestamp);
}
