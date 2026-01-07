//package nl.inholland.student.noservicedesk.database;
//
//import at.favre.lib.crypto.bcrypt.BCrypt;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import static com.mongodb.client.model.Aggregates.*;
//import static com.mongodb.client.model.Filters.*;
//import static com.mongodb.client.model.Projections.*;
//import static com.mongodb.client.model.UnwindOptions.*;
//
//import com.mongodb.client.model.Field;
//import com.mongodb.client.model.UnwindOptions;
//import nl.inholland.student.noservicedesk.AppContext;
//import nl.inholland.student.noservicedesk.Models.Ticket;
//import nl.inholland.student.noservicedesk.Models.User;
//import org.bson.Document;
//import org.bson.conversions.Bson;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static com.mongodb.client.model.Filters.eq;
//
//public class MongoDB {
//    private final AppContext context;
//    private MongoClient client;
//    private MongoDatabase database;
//
//    private Ticket ticket;
//
//    // Collections used throughout the application
//    private MongoCollection<Document> ticketCollection;
//    private MongoCollection<Document> userCollection;
//    private MongoCollection<Document> handledTicketsCollection;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public MongoDB(AppContext context) {
//        this.context = context;
//    }
//
//    public void connectDB() {
//        String uri = context.get("mongodb.conn");
//        client = MongoClients.create(uri);
//
//        database = client.getDatabase("NoDesk");
//
//        ticketCollection = database.getCollection("Ticket");
//        userCollection = database.getCollection("User");
//        handledTicketsCollection = database.getCollection("HandledTicket");
//
//        System.out.println("Connected to MongoDB!");
//    }
//
//    public void createTicket(Ticket ticket) throws JsonProcessingException {
//        try {
//            Document doc = Document.parse(objectMapper.writeValueAsString(ticket));
//            ticketCollection.insertOne(doc);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Document getTicketById(String id) {
//        return ticketCollection.find(eq("_id", id)).first();
//    }
//
//    public List<Ticket> getAllTickets() {
//        List<Ticket> tickets = new ArrayList<>();
//
//        List<Bson> pipeline = Arrays.asList(
//                lookup("User", "reported_by", "_id", "reporter"),
//                unwind("$reporter", new UnwindOptions().preserveNullAndEmptyArrays(true)),
//
//                // Create reported_by_name = "first_name last_name"
//                addFields(new Field<>("reported_by_name",
//                        new Document("$trim", new Document("input",
//                                new Document("$concat", Arrays.asList(
//                                        new Document("$ifNull", Arrays.asList("$reporter.first_name", "")),
//                                        " ",
//                                        new Document("$ifNull", Arrays.asList("$reporter.last_name", ""))
//                                ))
//                        ))
//                )),
//
//                project(fields(
//                        include("_id", "date_created", "deadline", "subject", "description", "priority",
//                                "status", "is_resolved", "reported_by", "reported_by_name")
//                ))
//        );
//
//        for (Document doc : ticketCollection.aggregate(pipeline)) {
//            // Convert BSON to JSON
//            Document normalized = Document.parse(doc.toJson());
//
//            // normalizing date and object id fields
//            normalized.put("date_created", getMongoDateString(normalized.get("date_created")));
//            normalized.put("deadline", getMongoDateString(normalized.get("deadline")));
//            normalized.put("_id", getMongoObjectIdString(normalized.get("_id")));
//            normalized.put("reported_by", getMongoObjectIdString(normalized.get("reported_by")));
//
//            // Convert into Ticket object
//            try {
//                Ticket ticket = objectMapper.readValue(normalized.toJson(), Ticket.class);
//                tickets.add(ticket);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//        return tickets;
//    }
//
//
//
//    public void updateTicket(String id, Document updatedFields) {
//        Document updateDoc = new Document("$set", updatedFields);
//        ticketCollection.updateOne(eq("_id", id), updateDoc);
//    }
//
//    public void deleteTicket(String id) {
//        ticketCollection.deleteOne(eq("_id", id));
//    }
//
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
//    public static String hashPassword(String plainPassword) {
//        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
//    }
//
//    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
//        BCrypt.Result result;
//
//        result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
//
//        return result.verified;
//    }
//
//
//    // Close connection to db
//    public void close() {
//        if (client != null) {
//            client.close();
//        }
//    }
//
//    public List<User> getAllUsers() {
//        List<User> users = new ArrayList<>();
//
//        for (Document doc : userCollection.find()) {
//            // Convert BSON to JSON
//            Document normalized = Document.parse(doc.toJson());
//
//            // normalize object id
//            normalized.put("_id", getMongoObjectIdString(normalized.get("_id")));
//
//            // Convert into User object
//            try {
//                User user = objectMapper.readValue(normalized.toJson(), User.class);
//                users.add(user);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//        return users;
//    }
//}
