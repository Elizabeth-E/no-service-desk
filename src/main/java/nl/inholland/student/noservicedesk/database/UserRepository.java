package nl.inholland.student.noservicedesk.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import nl.inholland.student.noservicedesk.Models.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static nl.inholland.student.noservicedesk.database.MongoHelper.getMongoObjectIdString;

public class UserRepository {
    private final MongoCollection<Document> userCollection;
    private final ObjectMapper objectMapper;

    public UserRepository(MongoCollection<Document> userCollection) {
        this.userCollection = userCollection;
        this.objectMapper = new ObjectMapper();

    }

    //    public Document findByEmail(String username) {
//        return
//    }
//    public boolean authenticate(String username, String password) {
//
//        Document user = userCollection.find(eq("email_address", username)).first();
//
//        if (user == null)
//            return false;
//
//        String storedHash = user.getString("password");
//        storedHash = storedHash.replaceAll("\\s+", "");
//
//        return verifyPassword(password, storedHash);
//    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        for (Document doc : userCollection.find()) {
            // Convert BSON to JSON
            Document normalized = Document.parse(doc.toJson());

            // normalize object id
            normalized.put("_id", getMongoObjectIdString(normalized.get("_id")));

            // Convert into User object
            try {
                User user = objectMapper.readValue(normalized.toJson(), User.class);
                users.add(user);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public Document findByEmail(String username) {
        Document user = userCollection.find(eq("email_address", username)).first();
        return user;
    }
}
