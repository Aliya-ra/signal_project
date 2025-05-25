package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A RealTimeDataReader implementation that connects to a WebSocket server
 * and processes real-time incoming data.
 */
public class WebSocketDataReader implements RealTimeDataReader {

    private WebSocketClient client;

    /**
     * Constructs a new WebSocketDataReader and connects to a WebSocket server.
     *
     * @param uri the WebSocket URI to connect to
     */
    @Override
    public void connectToWebSocket(String uri) {
        try {
            client = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to WebSocket server.");
                }

                @Override
                public void onMessage(String message) {
                    parseAndStoreMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connection closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("WebSocket error: " + ex.getMessage());
                }
            };

            client.connect();
        } catch (URISyntaxException e) {
            System.err.println("Invalid WebSocket URI: " + uri);
        }
    }

    /**
     * Gracefully disconnects from the WebSocket server.
     */
    @Override
    public void disconnect() {
        if (client != null && client.isOpen()) {
            client.close();
        }
    }

    /**
     * Parses and stores a message received from the WebSocket.
     * This is public for testing purposes.
     *
     * @param message the raw message string
     */
    public void parseAndStoreMessage(String message) {
        try {
            String[] parts = message.split(",");

            if (parts.length != 4) {
                System.err.println("Invalid message format: " + message);
                return;
            }

            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String recordType = parts[2].trim();
            double measurementValue = Double.parseDouble(parts[3].trim().replace("%", ""));

            DataStorage.getInstance().addPatientData(patientId, measurementValue, recordType, timestamp);

            System.out.printf("[WS] Stored: patientId=%d, %s=%.2f at %d%n",
                    patientId, recordType, measurementValue, timestamp);

        } catch (Exception e) {
            System.err.println("Failed to parse message: " + message);
            e.printStackTrace();
        }
    }

    public void simulateOnMessage(String message) {
        parseAndStoreMessage(message);
    }
}
