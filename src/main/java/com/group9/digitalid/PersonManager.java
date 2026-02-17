package com.group9.digitalid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
