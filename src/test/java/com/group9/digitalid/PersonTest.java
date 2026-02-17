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
   

}
