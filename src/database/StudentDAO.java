package database;

import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public Student getStudentById(int id) {

        String sql = "SELECT * FROM students WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            getCourseName(connection, rs.getInt("course_id")),
                            rs.getInt("semester"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDouble("cgpa")
                    );
                }

                return null;

            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateStudent(Student student) {

        String sql = "UPDATE students SET " +
                "name = ?, " +
                "age = ?, " +
                "email = ?, " +
                "course_id = ?, " +
                "semester = ?, " +
                "phone = ?, " +
                "cgpa = ? " +
                "WHERE id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            int courseId = getCourseId(connection, student.getCourse());

            ps.setString(1, student.getName());
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getEmail());
            ps.setInt(4, courseId);
            ps.setInt(5, student.getSemester());
            ps.setString(6, student.getPhone());
            ps.setDouble(7, student.getCgpa());
            ps.setInt(8, student.getId());

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
    private String getCourseName(Connection connection, int courseId)
            throws SQLException {

        String sql = "SELECT course_name FROM courses WHERE course_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("course_name");
                }

                throw new SQLException("Course not found: " + courseId);
            }
        }
    }

    public List<Student> searchStudentByName(String name) {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students WHERE name LIKE ? ORDER BY id";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String courseName =
                            getCourseName(connection, rs.getInt("course_id"));

                    Student student = new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            courseName,
                            rs.getInt("semester"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDouble("cgpa")
                    );

                    students.add(student);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<Student> searchStudentByCourse(String courseName) {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students " +
                "WHERE course_id = (" +
                "SELECT course_id FROM courses WHERE course_name = ?" +
                ") ORDER BY id";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, courseName);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String course =
                            getCourseName(connection, rs.getInt("course_id"));

                    Student student = new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            course,
                            rs.getInt("semester"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDouble("cgpa")
                    );

                    students.add(student);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public int getTotalStudents() {

        String sql = "SELECT COUNT(*) FROM students";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getAverageCgpa() {

        String sql = "SELECT AVG(cgpa) FROM students";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public Student getHighestCgpa() {

        String sql = "SELECT * FROM students " +
                "ORDER BY cgpa DESC " +
                "LIMIT 1";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                String courseName =
                        getCourseName(connection, rs.getInt("course_id"));

                return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        courseName,
                        rs.getInt("semester"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDouble("cgpa")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Student getLowestCgpa() {

        String sql = "SELECT * FROM students " +
                "ORDER BY cgpa ASC " +
                "LIMIT 1";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                String courseName =
                        getCourseName(connection, rs.getInt("course_id"));

                return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        courseName,
                        rs.getInt("semester"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDouble("cgpa")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void studentsPerCourse() {

        String sql = "SELECT c.course_name, COUNT(s.id) AS student_count " +
                "FROM courses c " +
                "LEFT JOIN students s " +
                "ON c.course_id = s.course_id " +
                "GROUP BY c.course_id, c.course_name " +
                "ORDER BY c.course_name";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                System.out.println(
                        rs.getString("course_name") +
                                " : " +
                                rs.getInt("student_count")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void studentsPerSemester() {

        String sql = "SELECT semester, COUNT(id) AS student_count " +
                "FROM students " +
                "GROUP BY semester " +
                "ORDER BY semester";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                System.out.println(
                        "Semester " +
                                rs.getInt("semester") +
                                " : " +
                                rs.getInt("student_count")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}