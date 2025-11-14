package nl.inholland.student.noservicedesk.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.database.MongoDB;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TicketService {
    private final MongoDB db;
    private List<Ticket> ticketList;

    public TicketService(MongoDB db) {
        this.db = db;
    }

    public List<Ticket> getAllTickets(){
        ticketList = db.getAllTickets();
        return ticketList;
    }

    public boolean checkDeadline(String deadlineString) {
        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

            ZonedDateTime zdt = ZonedDateTime.parse(deadlineString, formatter);
            LocalDateTime deadline = zdt.toLocalDateTime();

            return deadline.isBefore(LocalDateTime.now());
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUnresolvedTicketCount(){
        int ticketsUnresolved = 0;

        for (Ticket ticket : ticketList) {
            if(!ticket.isIs_resolved()) {
                ticketsUnresolved++;
            }
        }
        return ticketsUnresolved;
    }

    /// TODO: this isnt actually working but its not giving errors. fix so its returning an accurate count
    public int getPastDeadlineCount(){
        int ticketsPastDue = 0;

        for (Ticket ticket : ticketList) {
            if(!checkDeadline(ticket.getDeadline())) {
                ticketsPastDue++;
            }
        }
        return ticketsPastDue;
    }

}
