package main;

import exception.StudentNotFoundException;
import model.Student;
import service.StudentService;

import java.util.ArrayList;

public class ConnectionTest {

    public static void main(String[] args) {

        StudentService service = new StudentService();

        try {

            ArrayList<Student> result =
                    service.searchStudentByCourse("AIML");

            service.displayStudents(result);

        } catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());
        }
    }
}