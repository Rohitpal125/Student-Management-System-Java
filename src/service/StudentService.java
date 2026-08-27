package service;

import java.util.List;
import java.util.Scanner;

import exception.StudentNotFoundException;
import model.Student;
import util.StudentValidator;
import java.util.ArrayList;
import java.util.logging.Logger;
import database.StudentDAO;
import org.springframework.stereotype.Service;


@Service
public class StudentService {


    private final StudentDAO studentDAO = new StudentDAO();


    private static final Logger logger = Logger.getLogger(StudentService.class.getName());

    Scanner sc = new Scanner(System.in);

//    =============GET TOTAL STUDENTS==============

    public int getTotalStudents() {
        return studentDAO.getTotalStudents();
    }


//    ============GET STUDENT BY ID=================

    public Student getStudentById(int id) throws StudentNotFoundException {

        Student student = studentDAO.getStudentById(id);

        if (student == null) {
            throw new StudentNotFoundException(
                    "Student with ID " + id + " not found."
            );
        }

        return student;
    }

//    ==============Average CGPA===================

    public double getAverageCgpa() {
        return studentDAO.getAverageCgpa();
    }
//    =============HIGHEST CGPA==================

    public Student getHighestCgpa() {
        return studentDAO.getHighestCgpa();
    }
//    ==================LOWESTcGPA==================

    public Student getLowestCgpa() {
        return studentDAO.getLowestCgpa();
    }

//    ===================STUDENTPECOURSE================

    public void studentsPerCourse() {

        studentDAO.studentsPerCourse();
    }

//    ===================STUDENTS PA semester===============


    public void studentsPerSemester() {

        studentDAO.studentsPerSemester();
    }

//    ==================SORT STUDENTS BY NAME=======================

    public void sortByName() {

        studentDAO.sortByName();
    }

//    =================SORT BY CGPA==================

    public void sortByCgpa() {

        studentDAO.sortByCgpa();
    }

//    ==================SORT BY AGE==============

    public void sortByAge() {

        studentDAO.sortByAge();
    }

//    ==============SORT BY SEMESTER=============

    public void sortBySemester() {

        studentDAO.sortBySemester();
    }

//    ================FILTER BY CGPA================

    public void filterByCgpa() {

        System.out.print("Enter Minimum CGPA: ");
        double minimumCgpa = sc.nextDouble();
        sc.nextLine();

        studentDAO.filterByCgpa(minimumCgpa);
    }

//    =================FILTER BY AGE===================

    public void filterByAge() {

        System.out.print("Enter Minimum Age: ");
        int minimumAge = sc.nextInt();
        sc.nextLine();

        studentDAO.filterByAge(minimumAge);
    }

//    ================FILTER BY SEMESTER=============

    public void filterBySemester() {

        System.out.print("Enter Minimum Semester: ");
        int minSemester = sc.nextInt();
        sc.nextLine();

        studentDAO.filterBySemester(minSemester);
    }

//    ===============FILTER BY COURSE=========

    public void filterByCourse() {

        System.out.print("Enter Course: ");
        String course = sc.nextLine();

        studentDAO.filterByCourse(course);
    }

    // ================= ADD STUDENT =================

    public boolean addStudent(Student student) {

        if (!isValidStudent(student)) {
            return false;
        }

        boolean added = studentDAO.addStudent(student);

        if (added) {
            logger.info(
                    "Student added successfully. ID: " + student.getId()
            );
        }

        return added;
    }

    // ================= DISPLAY STUDENTS =================

    public void displayStudents() {

        studentDAO.getAllStudents();

    }

    public List<Student> getAllStudents() {

        return studentDAO.getAllStudentsList();

    }
//    ===============METHOD OVERLOADING===============

    public void displayStudents(ArrayList<Student> studentList) {

        if (studentList.isEmpty()) {
            System.out.println("Student not found");
            return;
        }

        for (Student student : studentList) {
            System.out.println(student);
        }
    }


// ================== SEARCH STUDENT BY NAME ======================

    public ArrayList<Student> searchStudentByName(String name)
            throws StudentNotFoundException {

        ArrayList<Student> result =
                new ArrayList<>(studentDAO.searchStudentByName(name));

        if (result.isEmpty()) {
            throw new StudentNotFoundException(
                    "Student with Name " + name + " not found"
            );
        }

        return result;
    }

/// ================== SEARCH STUDENT BY COURSE ======================

public ArrayList<Student> searchStudentByCourse(String course)
        throws StudentNotFoundException {

    ArrayList<Student> result =
            new ArrayList<>(studentDAO.searchStudentByCourse(course));

    if (result.isEmpty()) {
        throw new StudentNotFoundException(
                "Student with Course " + course + " not found"
        );
    }

    return result;
}

    // ================= UPDATE STUDENT =================

    public boolean updateStudent(
            int id,
            String name,
            int age,
            String course,
            int semester,
            String email,
            String phone,
            double cgpa
    ) {

        Student updatedStudent = new Student(
                id,
                name,
                age,
                course,
                semester,
                email,
                phone,
                cgpa
        );

        if (!isValidStudent(updatedStudent)) {
            return false;
        }

        boolean updated = studentDAO.updateStudent(updatedStudent);

        if (updated) {
            logger.info("Student updated successfully. ID: " + id);
        }

        return updated;
    }

    // ================= DELETE STUDENT =================

    public boolean deleteStudent(int id) {

        boolean deleted = studentDAO.deleteStudent(id);

        if (deleted) {
            logger.info("Student deleted successfully. ID: " + id);
        }

        return deleted;
    }


    private boolean isValidStudent(Student student) {

        if (!StudentValidator.isValidName(student.getName())) {
            System.out.println("Invalid Name!");
            return false;
        }

        if (!StudentValidator.isValidAge(student.getAge())) {
            System.out.println("Invalid Age!");
            return false;
        }

        if (!StudentValidator.isValidEmail(student.getEmail())) {
            System.out.println("Invalid Email!");
            return false;
        }

        if (!StudentValidator.isValidPhone(student.getPhone())) {
            System.out.println("Invalid Phone Number!");
            return false;
        }

        if (!StudentValidator.isValidSemester(student.getSemester())) {
            System.out.println("Invalid Semester!");
            return false;
        }

        if (!StudentValidator.isValidCgpa(student.getCgpa())) {
            System.out.println("Invalid CGPA!");
            return false;
        }

        return true;
    }

}