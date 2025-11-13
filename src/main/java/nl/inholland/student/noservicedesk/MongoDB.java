package nl.inholland.student.noservicedesk;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class MongoDB {
    private final AppContext context;
    private MongoClient client;
    private MongoDatabase database;

    // Collections used throughout the application
    private MongoCollection<Document> ticketCollection;
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> handledTicketsCollection;

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
    public void createTicket(Document ticket) {
        ticketCollection.insertOne(ticket);
    }

    // READ (get by ID)
    public Document getTicketById(String id) {
        return ticketCollection.find(eq("_id", id)).first();
    }

    // READ (all)
    public Iterable<Document> getAllTickets() {
        return ticketCollection.find();
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

    // Close connection
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
