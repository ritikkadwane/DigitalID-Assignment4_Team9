package com.group9.digitalid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PersonManager {
    private static final String FILENAME = "citizens.txt";

    /**
     * Updates a citizen's personal details in the database.
     * 
     * This method enforces specific business rules for the RMIT DigitalID system:
     * 
     *      1. Even-digit IDs are locked and cannot be changed (IDs starting with 0, 2, 4, 6, or 8).
     * 
     *      2. Minors under 18 cannot update their address.
     * 
     *      3. If you're changing someone's birthdate, you can't change anything else at the same time (no ID, name, or address changes).
     * 
     * @param oldID the citizen's current ID
     * @param newID the new ID (can stay the same)
     * @param newName the new name
     * @param newAddress the new address
     * @param newBirthdate the new birthdate (format: dd-MM-yyyy)
     * @return true if the update succeeds, false if validation fails or the citizen isn't found
     */
    public boolean updatePersonalDetails(String oldID, String newID, String newName, 
                                        String newAddress, String newBirthdate) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILENAME));
            
            int foundIndex = -1;
            String foundLine = null;
            
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(oldID + "|")) {
                    foundIndex = i;
                    foundLine = lines.get(i);
                    break;
                }
            }
            
            if (foundIndex == -1) {
                return false;
            }
            
            String[] parts = foundLine.split("\\|");
            String currentID = parts[0];
            String currentName = parts[1];
            String currentAddress = parts[2];
            String currentDOB = parts[3];
            
            char firstDigit = oldID.charAt(0);
            if (firstDigit == '0' || firstDigit == '2' || firstDigit == '4' || 
                firstDigit == '6' || firstDigit == '8') {
                if (!newID.equals(oldID)) {
                    System.out.println("Error: Even-digit IDs cannot be modified");
                    return false;
                }
            }
            
            int age = calculateAge(currentDOB);
            if (age < 18) {
                if (!currentAddress.equals(newAddress)) {
                    System.out.println("Error: Minors cannot change addresses");
                    return false;
                }
            }
            
            if (!newBirthdate.equals(currentDOB)) {
                if (!newID.equals(currentID) || !newName.equals(currentName) || !newAddress.equals(currentAddress)) {
                    return false;
                }
            }
            
            // TODO: Integration with shared validation logic
            // Once Person 1's validation methods are merged, uncomment the following:
            // if (!Validator.isValidID(newID)) return false;
            // if (!Validator.isValidName(newName)) return false;
            // if (!Validator.isValidAddress(newAddress)) return false;
            // if (!Validator.isValidBirthdate(newBirthdate)) return false;
            
            String updatedLine = newID + "|" + newName + "|" + newAddress + "|" + newBirthdate;
            lines.set(foundIndex, updatedLine);
            Files.write(Paths.get(FILENAME), lines);
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Figures out how old someone is based on when they were born.
     * 
     * @param birthdate the person's birthdate in dd-MM-yyyy format
     * @return their age in years
     */
    private int calculateAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dob = LocalDate.parse(birthdate, formatter);
        LocalDate today = LocalDate.now();
        return Period.between(dob, today).getYears();
    }
}
