package com.group9.digitalid;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
// Test class for Person demerit points functionality
class PersonDemeritTest {

    @Test
    void testValidDemeritAddition() {
        // Create a person with valid birthdate
        Person p = new Person("56s_d%&fAB", "15-11-1990");

        // Add valid demerit points and expect success
        assertEquals("Success", p.addDemeritPoints("10-02-2026", 3), "Should succeed with valid inputs");
   }
    @Test
    void testInvalidDateFormat() {
        // Create person
        Person p = new Person("56s_d%&fAB", "15-11-1990");

       // Invalid date format (slashes instead of dashes) should fail
       assertEquals("Failed", p.addDemeritPoints("10/02/2026", 3), "Should fail due to invalid date format");
   }

   @Test
   void testPointsOutsideRange() {
       // Create person
       Person p = new Person("56s_d%&fAB", "15-11-1990");

       // Points outside valid range should fail
       assertEquals("Failed", p.addDemeritPoints("10-02-2026", 7), "Should fail because points exceed 6");
   }
   @Test
   void testUnder21SuspensionTrigger() {
       // Person under 21 years old
       Person p = new Person("22s_d%&fXY", "01-01-2008");

       // Add points exceeding threshold for under 21
       p.addDemeritPoints("01-01-2026", 4);
       p.addDemeritPoints("02-01-2026", 3); // Total = 7

       // Should be suspended
       assertTrue(p.getIsSuspended(), "Under 21 should be suspended if total points > 6");
   }

   @Test
   void testOver21SuspensionTrigger() {
       // Person over 21 years old
       Person p = new Person("56s_d%&fAB", "01-01-1990");

       // Add points exceeding threshold for 21+
       p.addDemeritPoints("01-01-2026", 6);
       p.addDemeritPoints("02-01-2026", 6);
       p.addDemeritPoints("03-01-2026", 1); // Total = 13

       // Should be suspended
       assertTrue(p.getIsSuspended(), "Over 21 should be suspended if total points > 12");
    }
    @Test
    void testPointsBelowValidRange() {
        // Create a person with valid ID and birthdate
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        /* Attempt to add 0 points (below minimum of 1)
        Expected to be "Failed" because points must be between 1-6*/
        assertEquals("Failed", p.addDemeritPoints("10-02-2026", 0), "Should fail because points are below minimum of 1");
    }
    @Test
    void testNegativePoints() {
        // Create a person with valid ID and birthdate for testing
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        /* Adding -1 points (negative value, invalid)
        Negative points should always be rejected as they make no business sense*/

        assertEquals("Failed", p.addDemeritPoints("10-02-2026", -1), "Should fail because points cannot be negative");
    }
    @Test
    void testUnder21ExactlyAtThresholdNotSuspended() {
        Person p = new Person("22s_d%&fXY", "01-01-2008");
        //Adding exactly 6 points (at threshold)
        p.addDemeritPoints("01-01-2026", 6);
        // Verify NOT suspended (threshold is > 6, not >= 6)
        assertFalse(p.getIsSuspended(), "Under 21 should NOT be suspended with exactly 6 points");
    }
    @Test
    void testOver21ExactlyAtThresholdNotSuspended() {

        //Created person over 21 years old (born 1990, currently 36)
        Person p = new Person("56s_d%&fAB", "01-01-1990");

        //Added demerit points across two offenses totaling exactly 12
        p.addDemeritPoints("01-01-2026", 6);
        p.addDemeritPoints("02-01-2026", 6);
        // 21+ suspended when points > 12 (not >= 12)
        assertFalse(p.getIsSuspended(), "Over 21 should NOT be suspended with exactly 12 points");
    }
    @Test
    void testInvalidDateFormatReversed() {
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        // Attempt to use YYYY-MM-DD format (invalid, should be DD-MM-YYYY)
        assertEquals("Failed", p.addDemeritPoints("2026-02-10", 3), "Should fail due to incorrect date format");
    }
    @Test
    void testEmptyDateString() {
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        // Empty date string should be rejected
        assertEquals("Failed", p.addDemeritPoints("", 3), "Should fail with empty date string");
    }
    @Test
    void testDemeritPointsWrittenToFile() throws IOException {
        Files.deleteIfExists(Paths.get("demeritPoints.txt"));
        // Create person and add demerit points
        Person p = new Person("56s_d%&fAB", "15-11-1990");
        p.addDemeritPoints("10-02-2026", 3);

        // Verify file was created
        assertTrue(Files.exists(Paths.get("demeritPoints.txt")),"File should be created");

            // Verify file contains correct data
        String content = Files.readString(Paths.get("demeritPoints.txt"));
        assertTrue(content.contains("56s_d%&fAB"), "File should contain person ID");
        assertTrue(content.contains("10-02-2026"), "File should contain offense date");

        // Cleaning up
        Files.deleteIfExists(Paths.get("demeritPoints.txt"));
    }
}
