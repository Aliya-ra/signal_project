package com.data_management;

public interface RealTimeDataReader {
    void connectToWebSocket(String url);
    void disconnect();
}
