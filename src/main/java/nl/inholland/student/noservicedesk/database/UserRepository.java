package nl.inholland.student.noservicedesk.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import nl.inholland.student.noservicedesk.Models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

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

    public User findByEmail(String email) {
        Document doc = userCollection.find(
                eq("email_address", email)
        ).first();

        if (doc == null) {
            return null;
        }

        // Normalize Mongo ObjectId -> String for your model
        doc.put("_id", doc.getObjectId("_id").toHexString());

        try {
            return objectMapper.readValue(doc.toJson(), User.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail_address() == null || user.getEmail_address().isBlank()) {
            throw new IllegalArgumentException("Email address is required");
        }

        // Check duplicate email
        if (findByEmail(user.getEmail_address()) != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Build document using your DB field names
        Document doc = new Document()
                .append("first_name", user.getFirst_name())
                .append("last_name", user.getLast_name())
                .append("role", user.getRole())
                .append("email_address", user.getEmail_address())
                .append("location", user.getLocation())
                .append("phone", user.getPhone())
                .append("password", user.getPassword());

        userCollection.insertOne(doc);

        // Mongo adds _id automatically
        ObjectId newId = doc.getObjectId("_id");
        user.set_id(newId);

        return user;
    }

    /**
     * Deletes user by Mongo ObjectId string. Returns true if a user was deleted.
     */
    public boolean deleteUserById(String id) {
        if (id == null || id.isBlank()) return false;

        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException ex) {
            return false; // invalid ObjectId string
        }

        DeleteResult result = userCollection.deleteOne(eq("_id", objectId));
        return result.getDeletedCount() > 0;
    }

    /**
     * Deletes user by email address. Returns true if a user was deleted.
     */
    public boolean deleteUserByEmail(String email) {
        if (email == null || email.isBlank()) return false;

        DeleteResult result = userCollection.deleteOne(eq("email_address", email));
        return result.getDeletedCount() > 0;
    }
}
