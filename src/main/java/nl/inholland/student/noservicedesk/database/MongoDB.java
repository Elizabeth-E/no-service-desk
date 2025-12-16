package nl.inholland.student.noservicedesk.database;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import nl.inholland.student.noservicedesk.AppContext;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDB {
    private final AppContext context;
    private MongoClient client;
    private MongoDatabase database;

    private Ticket ticket;

    // Collections used throughout the application
    private MongoCollection<Document> ticketCollection;
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> handledTicketsCollection;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MongoDB(AppContext context) {
        this.context = context;
    }

    public void connectDB() {
        String uri = context.get("mongodb.conn");
        client = MongoClients.create(uri);

        database = client.getDatabase("NoDesk");

        ticketCollection = database.getCollection("Ticket");
        userCollection = database.getCollection("User");
        handledTicketsCollection = database.getCollection("HandledTicket");

        System.out.println("Connected to MongoDB!");
    }

    // CREATE
    public void createTicket(Ticket ticket) throws JsonProcessingException {
        try {
            Document doc = Document.parse(objectMapper.writeValueAsString(ticket));
            ticketCollection.insertOne(doc);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // READ (get by ID)
    public Document getTicketById(String id) {
        return ticketCollection.find(eq("_id", id)).first();
    }

    // READ (all)
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();

        for (Document doc : ticketCollection.find()) {
            // Convert BSON -> JSON -> Document again so we can manipulate it
            Document normalized = Document.parse(doc.toJson());

            // --- FIX DATE FIELDS: unwrap {$date: "..."} ---
            normalized.put("date_created",
                    getMongoDateString(normalized.get("date_created")));

            normalized.put("deadline",
                    getMongoDateString(normalized.get("deadline")));

            // --- FIX ObjectId fields too ---
            normalized.put("_id", getMongoObjectIdString(normalized.get("_id")));
            normalized.put("reported_by", getMongoObjectIdString(normalized.get("reported_by")));

            // Convert into Ticket object
            try {
                Ticket ticket = objectMapper.readValue(normalized.toJson(), Ticket.class);
                tickets.add(ticket);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return tickets;
    }

    // Helper to unwrap Mongo's date object
    private String getMongoDateString(Object value) {
        if (value instanceof Document d) {
            return d.getString("$date");
        }
        return value != null ? value.toString() : null;
    }

    // Helper to unwrap Mongo's ObjectId
    private String getMongoObjectIdString(Object value) {
        if (value instanceof Document d) {
            return d.getString("$oid");
        }
        return value != null ? value.toString() : null;
    }


    // UPDATE
    public void updateTicket(String id, Document updatedFields) {
        Document updateDoc = new Document("$set", updatedFields);
        ticketCollection.updateOne(eq("_id", id), updateDoc);
    }

    // DELETE
    public void deleteTicket(String id) {
        ticketCollection.deleteOne(eq("_id", id));
    }

    public boolean authenticate(String username, String password) {

        Document user = userCollection.find(eq("email_address", username)).first();

        if (user == null)
            return false;

        String storedHash = user.getString("password");
        storedHash = storedHash.replaceAll("\\s+", "");


        return verifyPassword(password, storedHash);
    }
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result;

        result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);

        return result.verified;
    }


    // Close connection
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        for (Document doc : userCollection.find()) {
            // Convert BSON -> JSON -> Document again so we can manipulate it
            Document normalized = Document.parse(doc.toJson());

            // --- FIX ObjectId fields too ---
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
}
