package database;

import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {

    public void addStudent(Student student) {

        String sql = "INSERT INTO students " +
                "(id, name, age, email, course_id, semester, phone, cgpa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            int courseId = getCourseId(connection, student.getCourse());

            ps.setInt(1, student.getId());
            ps.setString(2, student.getName());
            ps.setInt(3, student.getAge());
            ps.setString(4, student.getEmail());
            ps.setInt(5, courseId);
            ps.setInt(6, student.getSemester());
            ps.setString(7, student.getPhone());
            ps.setDouble(8, student.getCgpa());

            int rows = ps.executeUpdate();

            System.out.println(rows + " student inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getAllStudents() {

        String sql = "SELECT * FROM students ORDER BY id";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {

                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getInt("age") + " | " +
                                rs.getString("email") + " | " +
                                rs.getInt("course_id") + " | " +
                                rs.getInt("semester") + " | " +
                                rs.getString("phone") + " | " +
                                rs.getDouble("cgpa")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getStudentById(int id) {

        String sql = "SELECT * FROM students WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    System.out.println(
                            rs.getInt("id") + " | " +
                                    rs.getString("name") + " | " +
                                    rs.getInt("age") + " | " +
                                    rs.getString("email") + " | " +
                                    rs.getInt("course_id") + " | " +
                                    rs.getInt("semester") + " | " +
                                    rs.getString("phone") + " | " +
                                    rs.getDouble("cgpa")
                    );

                } else {
                    System.out.println("Student not found!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStudent(int id, int age, double cgpa) {

        String sql = "UPDATE students SET age = ?, cgpa = ? WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, age);
            ps.setDouble(2, cgpa);
            ps.setInt(3, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student updated successfully!");
            } else {
                System.out.println("Student not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {

        String sql = "DELETE FROM students WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Student not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCourseId(Connection connection, String courseName)
            throws SQLException {

        String sql = "SELECT course_id FROM courses WHERE course_name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, courseName);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("course_id");
                }

                throw new SQLException("Course not found: " + courseName);
            }
        }
    }
}