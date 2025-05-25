package main;

import org.junit.jupiter.api.Test;

import com.Main;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MainAppTest {

    @Test
    void testRunSimulatorWithNoArgs() {
        assertDoesNotThrow(() -> {
            Main.main(new String[] {}); // Simulates default run
        });
    }

    @Test
    void testRunDataStoragePath() {
        assertDoesNotThrow(() -> {
            Main.main(new String[] { "DataStorage" }); // Simulates alternate path
        });
    }
}
