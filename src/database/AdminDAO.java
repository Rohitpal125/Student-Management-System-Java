package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminDAO {

    // Creates the admins table the first time (nothing happens if it already exists)
    private void createTableIfMissing(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS admins (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "username VARCHAR(50) NOT NULL UNIQUE, " +
                            "password_hash VARCHAR(255) NOT NULL)"
            );
        }
    }

    // Adds a new admin, or changes the password if the username already exists
    public boolean saveAdmin(String username, String passwordHash) {

        String sql = "INSERT INTO admins (username, password_hash) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE password_hash = ?";

        try (Connection connection = DBConnection.getConnection()) {

            createTableIfMissing(connection);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, passwordHash);
                ps.setString(3, passwordHash);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Returns the stored password hash, or null if there is no such admin
    public String getPasswordHash(String username) {

        String sql = "SELECT password_hash FROM admins WHERE username = ?";

        try (Connection connection = DBConnection.getConnection()) {

            createTableIfMissing(connection);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password_hash");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}