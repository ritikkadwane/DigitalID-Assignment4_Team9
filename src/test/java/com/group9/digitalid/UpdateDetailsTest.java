package com.group9.digitalid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateDetailsTest {
    private static final String TEST_FILE = "citizens.txt";
    private PersonManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new PersonManager();
        
        List<String> testData = Arrays.asList(
            "111|John|Street1|01-01-1990",
            "333|Kid|Street2|01-01-2015"
        );
        Files.write(Paths.get(TEST_FILE), testData);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }
}
