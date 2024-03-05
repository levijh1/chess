package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static dataAccess.DatabaseManager.executeQuery;

public class PasswordHasher {
    public String generateHashedPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public boolean verifyPassword(String username, String password) throws DataAccessException {
        String sql = "SELECT password FROM Users WHERE username = ?";
        List<Object> resultList = executeQuery(sql, "password", username);

        String hashedPassword;
        try {
            hashedPassword = (String) resultList.getFirst();
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, hashedPassword);
    }
}
