package service;

import java.util.Scanner;

import exception.StudentNotFoundException;
import model.Student;
import util.StudentValidator;
import database.FileManager;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class StudentService {

    private final ArrayList<Student> students = new ArrayList<>();
    private final FileManager fileManager = new FileManager();

    Scanner sc = new Scanner(System.in);

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

//    ==================SORT STUDENTS BY NAME=======================

    public void sortByName(){
        if(students.isEmpty()){
            System.out.println("No Student Found");
            return;
        }
        Collections.sort(students, (s1, s2) -> s1.getName().compareTo(s2.getName()) );

        displayStudents();
    }

//    =================SORT BY CGPA==================

    public void sortByCgpa(){

        if(students.isEmpty()){
            System.out.println("Not found");
            return;
        }
        Collections.sort(students, (s1, s2) -> Double.compare(s2.getCgpa(), s1.getCgpa()));

        displayStudents();
    }

//    ==================SORT BY AGE==============

    public void sortByAge(){
        if(students.isEmpty()){
            System.out.println("Not Found");
            return;
        }
        Collections.sort(students, (s1, s2)-> Integer.compare(s2.getAge(), s1.getAge()));

        displayStudents();
    }

//    ==============SORT BY SEMESTER=============

    public void sortBySemester(){

        if(students.isEmpty()){
            System.out.println("Student not found");
            return;
        }
        Collections.sort(students, (s2,s1)-> Integer.compare(s2.getSemester(), s1.getSemester()));
        displayStudents();
    }

//    ================FILTER BY CGPA================

    public void filterByCgpa(){
        if(students.isEmpty()){
            System.out.println("Not Student Found");
            return;
        }
        System.out.print("Enter Minimum sgpa: ");
        double MinimumCgpa = sc.nextDouble();
        sc.nextLine();

        ArrayList<Student> filterStudents = new ArrayList<>();

        for (Student student : students){
            if(student.getCgpa() >= MinimumCgpa){
                filterStudents.add(student);
            }
        }
        if(filterStudents.isEmpty()){
            System.out.println("No Found Student");
            return;
        }
        else {
            displayStudents(filterStudents);
        }

    }

//    =================FILTER BY AGE===================

    public void filterByAge(){
        if(students.isEmpty()){
            System.out.println("Not Student Found");
        }
        System.out.print("Enter Minimum Age: ");
        int MinimumAge = sc.nextInt();
        sc.nextLine();

        ArrayList<Student> filterStudent = new ArrayList<>();

        for (Student student : students){
            if(student.getAge() >= MinimumAge){
                filterStudent.add(student);
            }
        }
        if (filterStudent.isEmpty()){
            System.out.println("Not Found Student");
            return;
        }
        else {
            displayStudents(filterStudent);
        }
    }

//    ================FILTER BY SEMESTER=============

    public void filterBySemester(){
        if(students.isEmpty()){
            System.out.println("Not Student Found");
            return;
        }
        else {
            System.out.print("Enter Minimum Semester: ");
            int minSemester = sc.nextInt();
            sc.nextLine();

            ArrayList<Student> filterStudent = new ArrayList<>();

            for (Student student : students){
                if(student.getSemester() >= minSemester){
                    filterStudent.add(student);
                }
            }
            if(filterStudent.isEmpty()){
                System.out.println("Not found Student");
                return;
            }
            else {
                displayStudents(filterStudent);
            }
        }
    }

//    ===============FILTER BY COURSE=========

    public void filterByCourse(){
        if(students.isEmpty()){
            System.out.println("Not student found");
            return;
        }
        else {
            System.out.print("Enter Course: ");
            String course = sc.nextLine();

            ArrayList<Student> filterStudent = new ArrayList<>();

            for (Student student : students){
                if(student.getCourse().equalsIgnoreCase(course)){
                    filterStudent.add(student);
                }
            }
            if(filterStudent.isEmpty()){
                System.out.println("Not Student Found");
                return;
            }
            else {
                displayStudents(filterStudent);
            }
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
//    ===============METHOD OVERLOADING===============

    public void displayStudents(ArrayList<Student> studentList) {

        if(studentList.isEmpty()){
            System.out.println("No Students Found.");
            return;
        }

        for (Student student : studentList) {
            System.out.println(student);
        }

    }

    // ================= SEARCH STUDENT BY ID =================

    public Student searchStudentById(int id) throws StudentNotFoundException {

        for (Student student : students) {

            if (student.getId() == id) {
                return student;
            }
        }

        throw new StudentNotFoundException("Student with ID " + id + " not found.");
    }

//    ==================SEARCH STUDENT BY NAME======================

    public ArrayList<Student> searchStudentByName(String name)
            throws StudentNotFoundException{

    ArrayList<Student> result = new ArrayList<>();

    for(Student student : students){

        if(student.getName().equalsIgnoreCase(name)){
            result.add(student);
        }
    }

    if (result.isEmpty()) {
        throw new StudentNotFoundException("Student with Name " + name + " not found");
    }

    return result;
}

//=====================SEARCH BY COURSE=======================

    public ArrayList<Student> searchStudentByCourse(String course)
            throws StudentNotFoundException {

        ArrayList<Student> result = new ArrayList<>();

        for (Student student : students) {

            if (student.getCourse().equalsIgnoreCase(course)) {
                result.add(student);
            }
        }

        if (result.isEmpty()) {
            throw new StudentNotFoundException(
                    "Student with Course " + course + " not found."
            );
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
                                 double cgpa) throws StudentNotFoundException{

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

    public boolean deleteStudent(int id) throws StudentNotFoundException{

        Student student = searchStudentById(id);

        students.remove(student);

        fileManager.saveStudents(students);

        return true;

    }

}