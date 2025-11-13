package nl.inholland.student.noservicedesk.services;

import nl.inholland.student.noservicedesk.database.MongoDB;

public class UserService {

    private final MongoDB db;

    public UserService(MongoDB db) {
        this.db = db;
    }

    public boolean authenticate(String username, String password) {
        return db.authenticate(username, password);
    }

}

