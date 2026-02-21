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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private HashMap<Date, Integer> demeritPoints = new HashMap<>();
    private boolean isSuspended;

    private static final String PERSONS_FILE = "persons.txt";

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

    // 10 chars, first 2 digits 2-9, 2+ special in middle, last 2 A-Z
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

    // StreetNum|Street|City|State|Country, state has to be Victoria
    private boolean checkAddress(String addr) {
        if (addr == null || addr.isEmpty()) return false;
        String[] p = addr.split("\\|");
        if (p.length != 5) return false;
        return "Victoria".equals(p[3].trim());
    }

    // DD-MM-YYYY
    private boolean checkBirthdate(String d) {
        if (d == null || d.isEmpty()) return false;
        return d.trim().matches("(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(19|20)\\d{2}");
    }

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

    public boolean addPerson() {
        return addPerson(PERSONS_FILE);
    }

    public boolean addPerson(String filePath) {
        if (personID == null || firstName == null || lastName == null || address == null || birthdate == null)
            return false;
        if (!checkID(personID)) return false;
        if (!checkAddress(address)) return false;
        if (!checkBirthdate(birthdate)) return false;

        Path path = Paths.get(filePath);
        try {
            if (idExists(path, personID)) return false;
            File f = path.toFile();
            if (f.getParentFile() != null && !f.getParentFile().exists())
                f.getParentFile().mkdirs();
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            Date offenseDate = dateFormat.parse(offenseDateStr);
            if (points < 1 || points > 6) return "Failed";
            demeritPoints.put(offenseDate, points);
            Date bDate = dateFormat.parse(this.birthdate);
            Calendar birth = Calendar.getInstance();
            birth.setTime(bDate);
            Calendar now = Calendar.getInstance();
            int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) age--;
            int totalPoints = 0;
            for (int p : demeritPoints.values()) {
                totalPoints += p;
            }
            if (age < 21 && totalPoints > 6) this.isSuspended = true;
            else if (age >= 21 && totalPoints > 12) this.isSuspended = true;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("demeritPoints.txt", true))) {
                writer.write(personID + "|" + offenseDateStr + "|" + points + "|" + totalPoints + "|" + isSuspended);
                writer.newLine();
            } catch (IOException e) {
                return "Failed";
            }
            return "Success";
        } catch (ParseException e) {
            return "Failed";
        }
    }
}
