package nl.inholland.student.noservicedesk.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import nl.inholland.student.noservicedesk.database.UserRepository;
import org.bson.Document;

public class AuthService {
    private UserRepository userRepository;

    public boolean authenticate(String username, String password) {
        Document user = userRepository.findByEmail(username);
        if (user == null) return false;

        String storedHash = user.getString("password");
        if (storedHash == null) return false;

        storedHash = storedHash.replaceAll("\\s+", "");
        return verify(password, storedHash);
    }
    public String hash(String plain) {
        return BCrypt.withDefaults().hashToString(12, plain.toCharArray());
    }

    public boolean verify(String plain, String hashed) {
        return BCrypt.verifyer().verify(plain.toCharArray(), hashed).verified;
    }
}
