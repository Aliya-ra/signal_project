package data_management;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.WebSocketDataReader;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketDataReaderTest {

    private DataStorage storage;

    @BeforeEach
    public void setup() {
        storage = DataStorage.getInstance();
        storage.clearAllData(); 
    }

    @AfterEach
    public void cleanup() {
        storage.clearAllData();
    }

    @Test
    public void testSimulateOnMessageStoresPatientData() {
        WebSocketDataReader reader = new WebSocketDataReader();
        String message = "99,1748181000000,ECG,0.65";

        reader.simulateOnMessage(message);

        Patient p = DataStorage.getInstance().getPatient(99);
        assertNotNull(p);
        assertEquals(1, p.getRecords().size());
        assertEquals("ECG", p.getRecords().get(0).getRecordType());
    }


    @Test
    public void testMalformedMessageWithMissingFields() {
        WebSocketDataReader reader = new WebSocketDataReader();
        String message = "10,ECG,0.44";  // Only 3 parts

        assertDoesNotThrow(() -> reader.simulateOnMessage(message));
    }

    @Test
    public void testNonNumericValuesHandledGracefully() {
        WebSocketDataReader reader = new WebSocketDataReader();
        String message = "abc,1748181441508,ECG,notanumber";

        assertDoesNotThrow(() -> reader.simulateOnMessage(message));
    }


}
