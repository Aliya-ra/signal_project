package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileDataReaderTest {

    @Test
    void testValidDataLine() throws IOException {
        Path tempDir = Files.createTempDirectory("testdata");
        Path testFile = tempDir.resolve("ECG.txt");

        String content = "Patient ID: 1, Timestamp: 1743760000000, Label: ECG, Data: 0.5";
        Files.write(testFile, content.getBytes());

        DataStorage storage = DataStorage.forceNewInstance(new FileDataReader(tempDir.toString()));

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(0.5, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testMalformedLineIsSkipped() throws IOException {
        Path tempDir = Files.createTempDirectory("testdata");
        Path testFile = tempDir.resolve("Broken.txt");

        String content = "This is not a valid record line";
        Files.write(testFile, content.getBytes());

        DataStorage storage = DataStorage.forceNewInstance(new FileDataReader(tempDir.toString()));

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(0, records.size());
    }

    @Test
    void testAlertLineNonNumericHandled() throws IOException {
        Path tempDir = Files.createTempDirectory("testdata");
        Path testFile = tempDir.resolve("Alert.txt");

        String content = "Patient ID: 1, Timestamp: 1743760000000, Label: Alert, Data: resolved";
        Files.write(testFile, content.getBytes());

        DataStorage storage = DataStorage.forceNewInstance(new FileDataReader(tempDir.toString()));

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals("Alert", records.get(0).getRecordType());
        assertEquals(0.0, records.get(0).getMeasurementValue());
    }
}
