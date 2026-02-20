package com.group9.digitalid;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Person {
    private String personID;
    //private String firstName;
    //private String lastName;
    //private String address;
    private String birthdate; // Format: DD-MM-YYYY
    // Using List to avoid overwriting points if two offenses share the same date
    private java.util.List<Integer> demeritPointsList = new java.util.ArrayList<>();
    private boolean isSuspended;

    // Constructor to initialize required fields for logic testing
    public Person(String personID, String birthdate) {
        this.personID = personID;
        this.birthdate = birthdate;
        this.isSuspended = false;
    }
    public String addDemeritPoints(String offenseDateStr, int points) {
        // Create date formatter with strict parsing
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            // Parse offense date (will throw exception if invalid format)
            dateFormat.parse(offenseDateStr); // Validate date format, throws ParseException if invalid
            // Validate that points are within allowed range (1-6)
            if (points < 1 || points > 6) {
               return "Failed"; // Invalid points
            }

            // Add points to list (avoids collision if same date used twice)
            demeritPointsList.add(points);
            // Parse birthdate to calculate age
            Date bDate = dateFormat.parse(this.birthdate);
            Calendar birth = Calendar.getInstance();
            birth.setTime(bDate);
            Calendar now = Calendar.getInstance();

            // Calculate age
            // Calculate age accurately using month and day comparison
            int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            if (now.get(Calendar.MONTH) < birth.get(Calendar.MONTH) ||
                (now.get(Calendar.MONTH) == birth.get(Calendar.MONTH) &&
                now.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
                age--; // Adjust if birthday hasn't occurred yet this year
            }

            // Calculate total demerit points accumulated
            int totalPoints = 0;
            for (int p : demeritPointsList) {
                totalPoints += p;
            }

            // Apply suspension rules based on age and points
            if (age < 21 && totalPoints > 6) {
                this.isSuspended = true;
            } else if (age >= 21 && totalPoints > 12) {
                this.isSuspended = true;
            }
            // Format: personID|date|points|total|suspended
            // suspended: false (can drive) / true (license suspended)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("demeritPoints.txt", true))) {
                writer.write(personID + "|" + offenseDateStr + "|" + points + "|" + totalPoints + "|" + isSuspended);
                writer.newLine();
            } catch (IOException e) {
                return "Failed"; // File write failed
            }

            // Placeholder: return Success for now
            return "Success";

        } catch (ParseException e) {
            // Return Failed if offense date format is invalid
            return "Failed";
        }
        
    }
    // Getter for testing suspension status
    public boolean getIsSuspended() {
       return isSuspended;
    }
}
