package service;

import model.Student;
import util.StudentValidator;
import database.FileManager;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StudentService {

    private final ArrayList<Student> students = new ArrayList<>();
    private final FileManager fileManager = new FileManager();

//    =============GET TOTAL STUDENTS==============

    public int getTotalStudents() {
        return students.size();
    }

//    ==============Average CGPA===================

    public double getAvrCgpa(){
        double totalcgpa = 0;
        if(students.isEmpty()){
            System.out.println("No Student found");
            return 0;
        }
        else {

            for(Student student : students){
                totalcgpa = totalcgpa + student.getCgpa();
            }
        }
        return totalcgpa/students.size();
    }

//    =============HIGHEST CGPA==================

    public Student getHighestCgpa(){

        if(students.isEmpty()){
            return null;
        }
        Student highstudent = students.get(0);
        for (Student student : students){

            if(student.getCgpa() > highstudent.getCgpa()){
                    highstudent = student;
                }
        }
        return highstudent;
    }

//    ==================LOWESTcGPA==================

    public Student getLowestCgpa(){

        if(students.isEmpty()){
            return null;
        }
        Student lowstudent = students.get(0);
        for (Student student : students){
            if (student.getCgpa() < lowstudent.getCgpa()){
                lowstudent = student;
            }
        }
        return lowstudent;
    }

//    ===================STUDENTPECOURSE================

    public void studentsPerCourse(){
        HashMap<String,Integer> map = new HashMap<>();
        if(students.isEmpty()){
            System.out.println("Student not found");
            return;
        }
        else {
            for (Student student : students){
                String course = student.getCourse();

                map.put(course, map.getOrDefault(course,0) + 1);
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
    }

//    ===================STUDENTS PA semester===============


    public void studentpaSemester(){
        HashMap<Integer, Integer> map = new HashMap<>();

        if(students.isEmpty()){
            System.out.println("Not Found Student");
            return;
        }
            for(Student student : students){
                int semester = student.getSemester();

                map.put(semester, map.getOrDefault(semester, 0) + 1);

            }
            for(Map.Entry<Integer, Integer> entry : map.entrySet()) {

                System.out.println("Semister " + entry.getKey() + " : " + entry.getValue());
            }
    }

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

    // ================= SEARCH STUDENT BY ID =================

    public Student searchStudentById(int id) {

        for (Student student : students) {

            if (student.getId() == id) {

                return student;

            }

        }

        return null;

    }

//    ==================SEARCH STUDENT BY NAME======================

public ArrayList<Student> searchStudentByName(String name){

    ArrayList<Student> result = new ArrayList<>();

    for(Student student : students){

        if(student.getName().equalsIgnoreCase(name)){
            result.add(student);
        }
    }

    return result;
}

//=====================SEARCH BY COURSE=======================

public ArrayList<Student> searchStudentByCourse(String course){

    ArrayList<Student> result = new ArrayList<>();

    for(Student student : students){

        if(student.getCourse().equalsIgnoreCase(course)){
            result.add(student);
        }
    }

    return result;
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