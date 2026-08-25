package main;

import service.StudentService;

public class ConnectionTest {

    public static void main(String[] args) {

        StudentService service = new StudentService();

        System.out.println("Database connection successful!");
    }
}