// Import JUnit classes for assertions and tests
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

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


}
