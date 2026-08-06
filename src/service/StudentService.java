package service;

import model.Student;
import util.StudentValidator;
import database.FileManager;
import java.io.*;
import java.util.ArrayList;


public class StudentService {

    private final ArrayList<Student> students = new ArrayList<>();
    private final FileManager fileManager = new FileManager();

    // Constructor
    public StudentService() {
        students.addAll(fileManager.loadStudents());
    }

    // ================= ADD STUDENT =================

    public void addStudent(Student student){

        if (!StudentValidator.isValidName(student.getName())) {
            System.out.println("Invalid Name!");
            return;
        }

        if (!StudentValidator.isValidAge(student.getAge())) {
            System.out.println("Invalid Age!");
            return;
        }

        if (!StudentValidator.isValidEmail(student.getEmail())) {
            System.out.println("Invalid Email!");
            return;
        }

        if (!StudentValidator.isValidPhone(student.getPhone())) {
            System.out.println("Invalid Phone Number!");
            return;
        }

        if (!StudentValidator.isValidSemester(student.getSemester())) {
            System.out.println("Invalid Semester!");
            return;
        }

        if (!StudentValidator.isValidCgpa((int) student.getCgpa())) {
            System.out.println("Invalid CGPA!");
            return;
        }

        if(students.contains(student)){
            System.out.println("Student already exists!");
            return;
        }

        students.add(student);
        fileManager.saveStudents(students);

        System.out.println("Student Added Successfully.");
    }

    // ================= DISPLAY STUDENTS =================

    public void displayStudents() {

        if (students.isEmpty()) {

            System.out.println("No Student Found.");
            return;

        }

        for (Student student : students) {

            System.out.println(student);

        }

    }

    // ================= SEARCH STUDENT =================

    public Student searchStudentById(int id) {

        for (Student student : students) {

            if (student.getId() == id) {

                return student;

            }

        }

        return null;

    }

    // ================= UPDATE STUDENT =================

    public boolean updateStudent(int id,
                                 String name,
                                 int age,
                                 String course,
                                 int semester,
                                 String email,
                                 String phone,
                                 double cgpa) {

        Student student = searchStudentById(id);

        if (!StudentValidator.isValidName(name)) {
            return false;
        }

        if (!StudentValidator.isValidAge(age)) {
            return false;
        }

        if (!StudentValidator.isValidEmail(email)) {
            return false;
        }

        if (!StudentValidator.isValidPhone(phone)) {
            return false;
        }

        if (!StudentValidator.isValidSemester(semester)) {
            return false;
        }

        if (!StudentValidator.isValidCgpa((int) cgpa)) {
            return false;
        }

        if (student == null) {

            return false;

        }

        student.setName(name);
        student.setAge(age);
        student.setCourse(course);
        student.setSemester(semester);
        student.setEmail(email);
        student.setPhone(phone);
        student.setCgpa(cgpa);

        fileManager.saveStudents(students);

        return true;

    }

    // ================= DELETE STUDENT =================

    public boolean deleteStudent(int id) {

        Student student = searchStudentById(id);

        if (student == null) {

            return false;

        }

        students.remove(student);

        fileManager.saveStudents(students);

        return true;

    }

}