package data_management;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketIntegrationTest {

    @Test
    public void testWebSocketIntegrationStoresRealTimeData() throws InterruptedException {
        // Start WebSocket client
        WebSocketDataReader reader = new WebSocketDataReader();
        reader.connectToWebSocket("ws://localhost:8080");

        // Wait for some messages to arrive
        Thread.sleep(3000); // Wait 3 seconds

        // Check if any data was stored
        boolean hasData = DataStorage.getInstance().getAllPatients()
                .stream()
                .anyMatch(p -> !p.getRecords().isEmpty());

        assertTrue(hasData, "At least one patient should have received records from WebSocket stream.");

        reader.disconnect();
    }
}
