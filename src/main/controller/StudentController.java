package main.controller;

import model.Student;
import org.springframework.web.bind.annotation.*;
import service.StudentService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
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

    @PostMapping("/api/students")
    public String addStudent(@RequestBody Student student) {

        boolean added = studentService.addStudent(student);

        if (added) {
            return "Student added successfully";
        }

        return "Student could not be added";
    }

    @PutMapping("/api/students/{id}")
    public String updateStudent(
            @PathVariable int id,
            @RequestBody Student student) {

        boolean updated = studentService.updateStudent(
                id,
                student.getName(),
                student.getAge(),
                student.getCourse(),
                student.getSemester(),
                student.getEmail(),
                student.getPhone(),
                student.getCgpa()
        );

        if (updated) {
            return "Student updated successfully";
        }

        return "Student could not be updated";
    }

    @DeleteMapping("/api/students/{id}")
    public String deleteStudent(@PathVariable int id) {

        boolean deleted = studentService.deleteStudent(id);

        if (deleted) {
            return "Student deleted successfully";
        }

        return "Student could not be deleted";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {

        e.printStackTrace();

        return e.getClass().getName() + " : " + e.getMessage();
    }
}