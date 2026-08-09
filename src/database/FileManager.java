package database;

import model.Student;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.util.ArrayList;

public class FileManager {

    private static final String FILE_NAME = "students.txt";

    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    // ================= SAVE STUDENTS =================

    public void saveStudents(ArrayList<Student> students) {

        try {

            BufferedWriter bw =
                    new BufferedWriter(
                            new FileWriter(FILE_NAME));

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

            logger.log(Level.SEVERE, "Error saving students file.", e);

        }

    }

    // ================= LOAD STUDENTS =================

    public ArrayList<Student> loadStudents() {

        ArrayList<Student> students = new ArrayList<>();

        try {

            BufferedReader br =
                    new BufferedReader(
                            new FileReader(FILE_NAME));

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
            logger.info("students.txt not found. New file will be created automatically.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading students file.", e);
        }

        return students;

    }

}