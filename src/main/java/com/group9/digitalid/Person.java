package com.group9.digitalid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;

    private final List<Integer> demeritPoints = new ArrayList<>();
    private boolean isSuspended;

    private static final String PERSONS_FILE = "persons.txt";
    private static final String DEMERIT_FILE = "demeritPoints.txt";
    private static final String IDS_FILE = "ids.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Person(String personID, String birthdate) {
        this.personID = personID;
        this.birthdate = birthdate;
        this.isSuspended = false;
    }

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.isSuspended = false;
    }

    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    public boolean getIsSuspended() { return isSuspended; }

    // checking 10 chars, first 2 digits 2-9, 2+ special in middle, last 2 A-Z
    private boolean checkID(String id) {
        if (id == null || id.length() != 10) return false;

        for (int i = 0; i < 2; i++) {
            char c = id.charAt(i);
            if (c < '2' || c > '9') return false;
        }

        String mid = id.substring(2, 8);
        int special = 0;
        for (int i = 0; i < mid.length(); i++) {
            if (!Character.isLetterOrDigit(mid.charAt(i))) special++;
        }
        if (special < 2) return false;

        for (int i = 8; i < 10; i++) {
            char c = id.charAt(i);
            if (c < 'A' || c > 'Z') return false;
        }

        return true;
    }

    // checking StreetNum|Street|City|State|Country, state being Victoria
    private boolean checkAddress(String addr) {
        if (addr == null || addr.isEmpty()) return false;
        String[] p = addr.split("\\|", -1);
        if (p.length != 5) return false;
        return "Victoria".equals(p[3].trim());
    }

    // checking dd-MM-yyyy by parsing
    private boolean checkBirthdate(String d) {
        if (d == null || d.isEmpty()) return false;
        try {
            LocalDate.parse(d.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // calculating age at the time of offence for keeping suspension stable
    private int calculateAgeOnDate(String birthdateStr, String offenceDateStr) {
        LocalDate dob = LocalDate.parse(birthdateStr, DATE_FORMATTER);
        LocalDate offenceDate = LocalDate.parse(offenceDateStr, DATE_FORMATTER);
        return Period.between(dob, offenceDate).getYears();
    }

    // checking digits-only string
    private boolean isAllDigits(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    // checking 2 uppercase letters followed by digits with fixed total length
    private boolean isTwoUpperLettersThenDigits(String s, int totalLength) {
        if (s == null || s.length() != totalLength) return false;

        char c0 = s.charAt(0);
        char c1 = s.charAt(1);

        if (c0 < 'A' || c0 > 'Z') return false;
        if (c1 < 'A' || c1 > 'Z') return false;

        for (int i = 2; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }

        return true;
    }

    // checking whether an ID is already existing in a file
    private boolean idExists(Path path, String id) throws IOException {
        if (!Files.exists(path)) return false;

        try (BufferedReader r = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith(id + "|")) return true;
            }
        }

        return false;
    }

    // checking whether a person already has passport, licence, or medicare before allowing student card
    private boolean hasAnyOtherGovernmentId(String filePath) {
        Path path = Paths.get(filePath);

        try {
            if (!Files.exists(path)) return false;

            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 3) continue;

                    String savedPersonId = parts[0].trim();
                    String savedType = parts[1].trim().toLowerCase(Locale.ROOT);

                    if (savedPersonId.equals(this.personID)) {
                        if (savedType.equals("passport")
                                || savedType.equals("medicare")
                                || savedType.equals("drivers licence")
                                || savedType.equals("driver licence")
                                || savedType.equals("driverslicence")
                                || savedType.equals("driverlicence")) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean addPerson() {
        return addPerson(PERSONS_FILE);
    }

    public boolean addPerson(String filePath) {
        // checking required fields being present
        if (personID == null || firstName == null || lastName == null || address == null || birthdate == null) {
            return false;
        }

        // checking ID format
        if (!checkID(personID)) return false;

        // checking address format and Victoria requirement
        if (!checkAddress(address)) return false;

        // checking birthdate format
        if (!checkBirthdate(birthdate)) return false;

        Path path = Paths.get(filePath);

        try {
            // checking duplicate IDs before writing
            if (idExists(path, personID)) return false;

            // creating parent folder if it is missing
            File f = path.toFile();
            if (f.getParentFile() != null && !f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            // appending person record into file
            try (BufferedWriter w = new BufferedWriter(new FileWriter(f, true))) {
                w.write(personID + "|" + firstName + "|" + lastName + "|" + address + "|" + birthdate);
                w.newLine();
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String addDemeritPoints(String offenseDateStr, int points) {
        // checking offence date format
        try {
            LocalDate.parse(offenseDateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return "Failed";
        }

        // checking points range
        if (points < 1 || points > 6) return "Failed";

        // recording points into list
        demeritPoints.add(points);

        int age;
        try {
            // calculating age on offence date
            age = calculateAgeOnDate(this.birthdate, offenseDateStr);
        } catch (DateTimeParseException e) {
            return "Failed";
        }

        // summing total points
        int totalPoints = 0;
        for (int p : demeritPoints) {
            totalPoints += p;
        }

        // applying suspension rules based on age
        if (age < 21 && totalPoints > 6) this.isSuspended = true;
        else if (age >= 21 && totalPoints > 12) this.isSuspended = true;

        // writing demerit record into file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DEMERIT_FILE, true))) {
            writer.write(personID + "|" + offenseDateStr + "|" + points + "|" + totalPoints + "|" + isSuspended);
            writer.newLine();
        } catch (IOException e) {
            return "Failed";
        }

        return "Success";
    }

    public boolean addID(String idType, String idNumber) {
        return addID(idType, idNumber, IDS_FILE, LocalDate.now());
    }

    public boolean addID(String idType, String idNumber, String filePath, LocalDate onDate) {
        // checking null values
        if (idType == null || idNumber == null) return false;

        // normalising input strings
        String type = idType.trim().toLowerCase(Locale.ROOT);
        String number = idNumber.trim();

        boolean valid = false;

        // validating passport
        if (type.equals("passport")) {
            valid = isTwoUpperLettersThenDigits(number, 8);

        // validating driver licence
        } else if (type.equals("drivers licence") || type.equals("driverslicence")
                || type.equals("driver licence") || type.equals("driverlicence")) {
            valid = isTwoUpperLettersThenDigits(number, 10);

        // validating medicare
        } else if (type.equals("medicare")) {
            valid = number.length() == 9 && isAllDigits(number);

        // validating student card with age and existing ID checks
        } else if (type.equals("student card") || type.equals("studentcard")) {
            if (number.length() == 12 && isAllDigits(number)) {
                try {
                    // calculating age on provided date
                    LocalDate dob = LocalDate.parse(this.birthdate, DATE_FORMATTER);
                    int age = Period.between(dob, onDate).getYears();

                    // checking under 18 requirement and checking not having other government IDs
                    if (age < 18) {
                        boolean hasOtherId = hasAnyOtherGovernmentId(filePath);
                        valid = !hasOtherId;
                    } else {
                        valid = false;
                    }
                } catch (DateTimeParseException e) {
                    valid = false;
                }
            }

        } else {
            return false;
        }

        if (!valid) return false;

        // writing ID record into file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(personID + "|" + type + "|" + number);
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updatePersonalDetails(String oldID,
                                         String newID,
                                         String newFirstName,
                                         String newLastName,
                                         String newAddress,
                                         String newBirthdate) {
        // delegating update logic to manager
        PersonManager manager = new PersonManager();
        return manager.updatePersonalDetails(oldID, newID, newFirstName, newLastName, newAddress, newBirthdate);
    }
}