package nl.inholland.student.noservicedesk.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.inholland.student.noservicedesk.AppContext;
import org.bson.Document;

public class MongoConn {
    private MongoClient client;
    private MongoDatabase database;
    private final AppContext context;

    public MongoConn(AppContext context) {
        this.context = context;
    }
    public void connectDB() {
        String uri = context.get("mongodb.conn");
        client = MongoClients.create(uri);

        database = client.getDatabase("NoDesk");

        System.out.println("Connected to MongoDB!");
    }

    public MongoCollection<Document> tickets() { return database.getCollection("Ticket"); }
    public MongoCollection<Document> users() { return database.getCollection("User"); }
    public MongoCollection<Document> handledTickets() { return database.getCollection("HandledTicket"); }

    public void close() { client.close(); }
}
