package nl.inholland.student.noservicedesk.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.result.UpdateResult;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
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

public class TicketRepository {
    private final MongoCollection<Document> ticketCollection;

    public TicketRepository(MongoCollection<Document> ticketCollection) {
        this.ticketCollection = ticketCollection;
    }

    public void insert(Ticket ticket) {
        Document doc = new Document();

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

    public List<Ticket> getTicketsByUser(User user) {
        List<Ticket> tickets = new ArrayList<>();
        ObjectId userId = user.get_id();

        List<Bson> pipeline = Arrays.asList(
                // Only tickets reported by this user
                match(eq("reported_by", userId)),

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

            var created = doc.getDate("date_created");
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


    public UpdateResult update(Ticket ticket) {
        if (ticket == null || ticket.get_id() == null) {
            throw new IllegalArgumentException("Ticket and ticket _id cannot be null");
        }

        Document updatedFields = new Document();

        // only set fields you allow to be updated
        updatedFields.put("date_created", ticket.getDate_created() != null ? java.util.Date.from(ticket.getDate_created()) : null);
        updatedFields.put("deadline", ticket.getDeadline() != null ? java.util.Date.from(ticket.getDeadline()) : null);

        updatedFields.put("subject", ticket.getSubject());
        updatedFields.put("description", ticket.getDescription());
        updatedFields.put("priority", ticket.getPriority());
        updatedFields.put("is_resolved", ticket.getIs_resolved());
        updatedFields.put("reported_by", ticket.getReported_by());

        // Don't update if nothing is provided
        return ticketCollection.updateOne(
                eq("_id", ticket.get_id()),
                new Document("$set", updatedFields)
        );
    }

    public void deleteById(ObjectId id) {
        ticketCollection.deleteOne(eq("_id", id));
    }
}

