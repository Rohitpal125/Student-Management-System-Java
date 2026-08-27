package main.controller;

import model.Student;
import service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello Student Management System!";
    }

    @GetMapping("/api/students")
    public List<Student> getStudents() {
        return studentService.getAllStudents();
    }
}