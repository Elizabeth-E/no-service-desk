package nl.inholland.student.noservicedesk.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.UnwindOptions;
import nl.inholland.student.noservicedesk.Models.Ticket;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static nl.inholland.student.noservicedesk.database.MongoHelper.getMongoDateString;
import static nl.inholland.student.noservicedesk.database.MongoHelper.getMongoObjectIdString;

public class TicketRepository {
    private final MongoCollection<Document> ticketCollection;
    private final ObjectMapper objectMapper;

    public TicketRepository(MongoCollection<Document> ticketCollection) {
        this.ticketCollection = ticketCollection;
        this.objectMapper = new ObjectMapper();
    }

    public void insert(Ticket ticket) {
        try {
            ticketCollection.insertOne(Document.parse(objectMapper.writeValueAsString(ticket)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();

        List<Bson> pipeline = Arrays.asList(
                lookup("User", "reported_by", "_id", "reporter"),
                unwind("$reporter", new UnwindOptions().preserveNullAndEmptyArrays(true)),

                // Create reported_by_name = "first_name last_name"
                addFields(new Field<>("reported_by_name",
                        new Document("$trim", new Document("input",
                                new Document("$concat", Arrays.asList(
                                        new Document("$ifNull", Arrays.asList("$reporter.first_name", "")),
                                        " ",
                                        new Document("$ifNull", Arrays.asList("$reporter.last_name", ""))
                                ))
                        ))
                )),

                project(fields(
                        include("_id", "date_created", "deadline", "subject", "description", "priority",
                                "status", "is_resolved", "reported_by", "reported_by_name")
                ))
        );

        for (Document doc : ticketCollection.aggregate(pipeline)) {
            // Convert BSON to JSON
            Document normalizedDoc = Document.parse(doc.toJson());

            // normalizing date and object id fields
            normalizedDoc.put("date_created", getMongoDateString(normalizedDoc.get("date_created")));
            normalizedDoc.put("deadline", getMongoDateString(normalizedDoc.get("deadline")));
            normalizedDoc.put("_id", getMongoObjectIdString(normalizedDoc.get("_id")));
            normalizedDoc.put("reported_by", getMongoObjectIdString(normalizedDoc.get("reported_by")));

            // Convert into Ticket object
            try {
                Ticket ticket = objectMapper.readValue(normalizedDoc.toJson(), Ticket.class);
                tickets.add(ticket);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return tickets;
    }

    public void updateFields(String id, Document updatedFields) {
        ticketCollection.updateOne(eq("_id", new ObjectId(id)), new Document("$set", updatedFields));
    }

    public void deleteById(String id) {
        ticketCollection.deleteOne(eq("_id", id));
    }
}

