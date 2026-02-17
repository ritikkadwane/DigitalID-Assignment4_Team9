import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;

public class Person {
   private String personID;
   private String firstName;
   private String lastName;
   private String address;
   private String birthdate; // Format: DD-MM-YYYY
   private HashMap<Date, Integer> demeritPoints = new HashMap<>();
   private boolean isSuspended;

   // Constructor to initialize required fields for logic testing
   public Person(String personID, String birthdate) {
       this.personID = personID;
       this.birthdate = birthdate;
       this.isSuspended = false;
   }
}
