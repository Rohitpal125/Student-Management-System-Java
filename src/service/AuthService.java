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