package service;

import database.AdminDAO;
import org.springframework.stereotype.Service;
import util.PasswordUtil;
import util.TokenUtil;

@Service
public class AuthService {

    private final AdminDAO adminDAO = new AdminDAO();

    // Used at startup to create the first admin
    public boolean createOrUpdateAdmin(String username, String password) {
        return adminDAO.saveAdmin(username, PasswordUtil.hash(password));
    }

    public boolean adminExists(String username) {
        return adminDAO.getPasswordHash(username) != null;
    }

    // Returns null if the password was changed, otherwise a message explaining what is wrong
    public String changePassword(String username, String oldPassword, String newPassword) {

        if (oldPassword == null || newPassword == null) {
            return "Enter your current and new password.";
        }

        if (newPassword.length() < 8 || newPassword.length() > 128) {
            return "New password must be 8 to 128 characters long.";
        }

        String storedHash = adminDAO.getPasswordHash(username);

        if (storedHash == null || !PasswordUtil.verify(oldPassword, storedHash)) {
            return "Current password is wrong.";
        }

        if (oldPassword.equals(newPassword)) {
            return "New password must be different from the current one.";
        }

        boolean saved = adminDAO.saveAdmin(username, PasswordUtil.hash(newPassword));

        return saved ? null : "Could not save the new password. Please try again.";
    }

    // Returns a token if the login is correct, otherwise null
    public String login(String username, String password) {

        if (username == null || password == null) {
            return null;
        }

        String name = username.trim();
        String storedHash = adminDAO.getPasswordHash(name);

        if (storedHash == null || !PasswordUtil.verify(password, storedHash)) {
            return null;
        }

        return TokenUtil.create(name);
    }
}