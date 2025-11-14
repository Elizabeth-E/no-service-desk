package nl.inholland.student.noservicedesk.services;

import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.database.MongoDB;

import java.util.List;

public class TicketService {
    private final MongoDB db;

    public TicketService(MongoDB db) {
        this.db = db;
    }

    public List<Ticket> getAllTickets() {
        return db.getAllTickets();
    }
}
