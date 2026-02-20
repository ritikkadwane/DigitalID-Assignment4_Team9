# DigitalID Platform – Assignment 4  
ISYS3413 / ISYS3475 / ISYS1118  
Group 9  

## Project Overview

This project implements core functionality of the DigitalID platform using Java and Maven.

The current module includes implementation and testing of the Person class, including demerit point handling and suspension logic.

The following functional areas are being developed and tested:

- addPerson()
- updatePersonalDetails()
- addDemeritPoints()

Unit testing is implemented using JUnit 5 and the project is integrated with GitHub Actions for continuous integration.

---

## Technologies Used

- Java 17  
- Maven  
- JUnit 5  
- GitHub Actions (CI/CD)

---

## Project Structure

The project follows the standard Maven structure:

src/
 ├── main/java/com/group9/digitalid/
 │     └── Person.java
 └── test/java/com/group9/digitalid/
       └── PersonDemeritTest.java

Production code is located in src/main/java.  
Unit tests are located in src/test/java.

---

## Current Implementation Status

- Person class implemented with demerit point tracking  
- Suspension rules based on age and accumulated points  
- File logging of demerit records  
- 12 JUnit test cases covering:
  - Valid scenarios
  - Invalid input validation
  - Boundary conditions
  - Suspension threshold logic
  - File output verification  
- Maven build successfully passing locally  
- CI pipeline configured to run mvn clean test on push  

---

## Java Version Requirement

All team members must use:

Java 17

Confirm using:

java -version

---

## Building and Testing Locally

To clean and run all tests:

mvn clean test

You should see:

BUILD SUCCESS

---

## Continuous Integration

GitHub Actions automatically runs:

mvn clean test

on every push to the main branch.

A successful workflow run confirms:
- Project compiles correctly  
- All tests pass  
- Build is stable  