package main;

import model.Student;
import service.StudentService;

public class ConnectionTest {

    public static void main(String[] args) {

        StudentService service = new StudentService();

        Student student = new Student(
                6,
                "Test Student",
                21,
                "AIML",
                4,
                "teststudent@gmail.com",
                "9876543210",
                8.0
        );

        service.addStudent(student);
    }
}