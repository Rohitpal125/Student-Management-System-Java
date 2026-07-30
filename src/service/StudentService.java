package service;

import model.Student;

import java.io.*;
import java.util.ArrayList;

public class StudentService {

    private final ArrayList<Student> students = new ArrayList<>();

    // Constructor
    public StudentService() {
        loadStudentsFromFile();
    }

    // ================= ADD STUDENT =================

    public void addStudent(Student student){

        if(students.contains(student)){
            System.out.println("Student already exists!");
            return;
        }

        students.add(student);
        saveStudentsToFile();

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

        saveStudentsToFile();

        return true;

    }

    // ================= DELETE STUDENT =================

    public boolean deleteStudent(int id) {

        Student student = searchStudentById(id);

        if (student == null) {

            return false;

        }

        students.remove(student);

        saveStudentsToFile();

        return true;

    }

    // ================= SAVE STUDENTS =================

    private void saveStudentsToFile() {

        try {

            BufferedWriter bw =
                    new BufferedWriter(
                            new FileWriter("students.txt"));

            for (Student student : students) {

                String line =
                        student.getId() + "," +
                                student.getName() + "," +
                                student.getAge() + "," +
                                student.getCourse() + "," +
                                student.getSemester() + "," +
                                student.getEmail() + "," +
                                student.getPhone() + "," +
                                student.getCgpa();

                bw.write(line);
                bw.newLine();

            }

            bw.close();

        } catch (Exception e) {

            System.out.println("Error Saving File.");

        }

    }

    // ================= LOAD STUDENTS =================

    private void loadStudentsFromFile() {

        try {

            BufferedReader br =
                    new BufferedReader(
                            new FileReader("students.txt"));

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                Student student = new Student(

                        Integer.parseInt(data[0]),
                        data[1],
                        Integer.parseInt(data[2]),
                        data[3],
                        Integer.parseInt(data[4]),
                        data[5],
                        data[6],
                        Double.parseDouble(data[7])

                );

                students.add(student);

            }

            br.close();

        } catch (FileNotFoundException e) {

            System.out.println("students.txt not found.");
            System.out.println("New file will be created automatically.");

        } catch (Exception e) {

            System.out.println("Error Loading File.");

        }

    }

}