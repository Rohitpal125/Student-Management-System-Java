package main;

import exception.StudentNotFoundException;
import service.StudentService;
public class ConnectionTest {

    public static void main(String[] args) {

        StudentService service = new StudentService();

        try {

            service.deleteStudent(6);

        } catch (StudentNotFoundException e) {

            System.out.println("Student not found!");

        }
    }
}