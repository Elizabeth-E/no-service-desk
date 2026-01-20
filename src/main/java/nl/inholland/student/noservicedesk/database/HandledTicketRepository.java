package nl.inholland.student.noservicedesk.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
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

        // Ensure ticket exists
        long count = tickets.countDocuments(Filters.eq("_id", handledTicket.getTicketId()));
        if (count == 0) {
            throw new IllegalArgumentException("Ticket not found for id: " + handledTicket.getTicketId());
        }

        Date now = Date.from(Instant.now());
        ObjectId newHandledId = new ObjectId(); // ✅ generate a NEW id for history row

        MergeOptions mergeOptions = new MergeOptions()
                .whenMatched(MergeOptions.WhenMatched.FAIL)          // ✅ never overwrite history
                .whenNotMatched(MergeOptions.WhenNotMatched.INSERT); // ✅ insert only

        List<Bson> pipeline = Arrays.asList(
                // 1) Match the ticket
                Aggregates.match(Filters.eq("_id", handledTicket.getTicketId())),

                // 2) Create the handled-ticket document shape
                // IMPORTANT: overwrite root _id with NEW handled id, so $merge inserts every time
                Aggregates.addFields(
                        new Field<>("_id", newHandledId),
                        new Field<>("handledDate", now),
                        new Field<>("ticketId", handledTicket.getTicketId()),
                        new Field<>("handledBy", handledTicket.getHandledBy()),
                        new Field<>("comment", handledTicket.getComment() == null ? "" : handledTicket.getComment()),
                        new Field<>("ticket", "$$ROOT") // snapshot of ticket
                ),

                // 3) Only keep handled-ticket fields (root _id is now newHandledId)
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "handledDate", "ticketId", "handledBy", "comment", "ticket")
                )),

                // 4) Merge into HandledTicket collection
                Aggregates.merge(handledTickets.getNamespace().getCollectionName(), mergeOptions)
        );

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

        ht.set_id(doc.getObjectId("_id"));
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

