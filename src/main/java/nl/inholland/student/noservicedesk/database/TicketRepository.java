package nl.inholland.student.noservicedesk.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

public class TicketRepository {
    private final MongoCollection<Document> ticketCollection;
    private final ObjectMapper objectMapper;

    public TicketRepository(MongoCollection<Document> ticketCollection) {
        this.ticketCollection = ticketCollection;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void insert(Ticket ticket) {
        Document doc = new Document();

        // if you generate id in app:
        // doc.put("_id", new ObjectId(t.getId()));

        doc.put("date_created", ticket.getDate_created() != null ? java.util.Date.from(ticket.getDate_created()) : null);
        doc.put("deadline", ticket.getDeadline() != null ? java.util.Date.from(ticket.getDeadline()) : null);

        doc.put("subject", ticket.getSubject());
        doc.put("description", ticket.getDescription());
        doc.put("priority", ticket.getPriority());
        doc.put("is_resolved", ticket.getIs_resolved());
        doc.put("reported_by", ticket.getReported_by());

        ticketCollection.insertOne(doc);
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
            Ticket t = new Ticket();

            t.set_id(doc.getObjectId("_id"));

            var created = doc.getDate("date_created");   // java.util.Date
            t.setDate_created(created != null ? created.toInstant() : null);

            var deadline = doc.getDate("deadline");
            t.setDeadline(deadline != null ? deadline.toInstant() : null);

            t.setSubject(doc.getString("subject"));
            t.setDescription(doc.getString("description"));
            t.setPriority(doc.getString("priority"));
            t.setIs_resolved(Boolean.TRUE.equals(doc.getBoolean("is_resolved")));

            ObjectId reportedBy = doc.getObjectId("reported_by");
            t.setReported_by(reportedBy);

            t.setReported_by_name(doc.getString("reported_by_name"));

            tickets.add(t);
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

