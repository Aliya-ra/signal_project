package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Implements {@link OutputStrategy} and sends the data of patient to
 * a TCP client. The data is formatted as CSV with.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;


    /**
     * Constructs a {@code TcpOutputStrategy} on a certain port
     * that listens for TCP connections
     * @param port the tcp port that the server will listen for connections
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sends the data in a line to the connected TCP.
     * The data is formatted as CSV with columns: {@code patientId,timestamp,label,data}
     * @param patientId the Id for the specific patient
     * @param timestamp the time that the data was generated
     * @param label the label that describes the type of data
     * @param data the data that needs to be transfered
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
