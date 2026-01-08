package nl.inholland.student.noservicedesk.services;

import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.database.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public boolean authenticate(String username, String password) {
        return authService.authenticate(username, password);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}

