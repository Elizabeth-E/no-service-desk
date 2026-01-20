package nl.inholland.student.noservicedesk.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import nl.inholland.student.noservicedesk.Models.HandledTicket;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HandledTicketRepository {
    private final MongoCollection<Document> tickets;
    private final MongoCollection<Document> handledTickets;

    public HandledTicketRepository(MongoCollection<Document> ticketsCollection, MongoCollection<Document> handedTicketsCollection) {
        this.tickets = ticketsCollection;
        this.handledTickets = handedTicketsCollection;
    }

    public void insertHandledTicket(HandledTicket handledTicket) {
        if (handledTicket.getTicketId() == null) throw new IllegalArgumentException("ticketId cannot be null");
        if (handledTicket.getHandledBy() == null) throw new IllegalArgumentException("handledBy cannot be null");

        // Optional safety: ensure ticket exists, otherwise pipeline writes nothing.
        long count = tickets.countDocuments(Filters.eq("_id", handledTicket.getTicketId()));
        if (count == 0) {
            throw new IllegalArgumentException("Ticket not found for id: " + handledTicket.getTicketId());
        }

        Date now = Date.from(Instant.now());

        List<Bson> pipeline = Arrays.asList(
                // 1) Find the ticket
                Aggregates.match(Filters.eq("_id", handledTicket.getTicketId())),

                // 2) Add handled-ticket fields + embed the *original* ticket as snapshot
                Aggregates.addFields(
                        new Field<>("handledDate", now),
                        new Field<>("ticketId", handledTicket.getTicketId()),
                        new Field<>("handledBy", handledTicket.getHandledBy()),
                        new Field<>("comment", handledTicket.getComment() == null ? "" : handledTicket.getComment()),
                        new Field<>("ticket", "$$ROOT") // snapshot of the ticket at this moment
                ),

                // 3) Keep only the handled-ticket fields (so we don't store the ticket twice at root)
                Aggregates.project(Projections.fields(
                        Projections.include("handledDate", "ticketId", "handledBy", "comment", "ticket")
                )),

                // 4) Write result into HandledTicket collection (each handling becomes a new record)
                Aggregates.merge(handledTickets.getNamespace().getCollectionName())
        );

        // Run aggregation on Ticket collection; $merge writes into HandledTicket.
        tickets.aggregate(pipeline).toCollection();
    }

    /**
     * Returns all handled-ticket records for a specific ticketId.
     * (Does not map the embedded ticket snapshot into the HandledTicket model, but it exists in MongoDB.)
     */
    public List<HandledTicket> getByTicketId(ObjectId ticketId) {
        if (ticketId == null) throw new IllegalArgumentException("ticketId cannot be null");

        List<HandledTicket> results = new ArrayList<>();

        for (Document doc : handledTickets
                .find(Filters.eq("ticketId", ticketId))
                .sort(Sorts.descending("handledDate"))) {

            results.add(fromDocument(doc));
        }

        return results;
    }

    private HandledTicket fromDocument(Document doc) {
        HandledTicket ht = new HandledTicket();

        ht.set_Id(doc.getObjectId("_id"));
        ht.setTicketId(doc.getObjectId("ticketId"));
        ht.setHandledBy(doc.getObjectId("handledBy"));
        ht.setComment(doc.getString("comment"));

        Date handledDate = doc.getDate("handledDate");
        if (handledDate != null) {
            ht.setHandledDate(handledDate.toInstant());
        }

        return ht;
    }
}

