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
    
    private int calculateAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dob = LocalDate.parse(birthdate, formatter);
        LocalDate today = LocalDate.now();
        return Period.between(dob, today).getYears();
    }
}
