package com.group9.digitalid;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

class PersonAddIDTest {

    private Path tmpIdsFile;

    @BeforeEach
    void setup() throws IOException {
        tmpIdsFile = Files.createTempFile("ids_", ".txt");
    }

    @AfterEach
    void cleanup() throws IOException {
        if (tmpIdsFile != null) Files.deleteIfExists(tmpIdsFile);
    }

    @Test
    void validPassportReturnsTrue() {
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertTrue(p.addID("passport", "AB123456", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void invalidPassportLengthReturnsFalse() {
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertFalse(p.addID("passport", "AB12345", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void validDriversLicenceReturnsTrue() {
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertTrue(p.addID("drivers licence", "AB12345678", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void invalidMedicareNonDigitReturnsFalse() {
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        assertFalse(p.addID("medicare", "12345A789", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }

    @Test
    void studentCardAllowedOnlyUnder18() {
        Person under18 = new Person("22s_d%&fXY", "01-01-2010");
        assertTrue(under18.addID("student card", "123456789012", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));

        Person over18 = new Person("56s_d%&fAB", "01-01-1990");
        assertFalse(over18.addID("student card", "123456789012", tmpIdsFile.toString(), LocalDate.of(2026, 2, 21)));
    }
}