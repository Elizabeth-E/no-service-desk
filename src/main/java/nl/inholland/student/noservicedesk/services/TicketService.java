package nl.inholland.student.noservicedesk.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.database.TicketRepository;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TicketService {
    private TicketRepository ticketRepository;
    private List<Ticket> ticketList;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets(){
        ticketList = ticketRepository.getAllTickets();
        return ticketList;
    }

    public boolean isDeadlineExpired(Instant deadline) {
        return deadline != null && deadline.isBefore(Instant.now());
    }

    public void setDeadlineFromCreatedAndDays(Ticket ticket, String daysAsString) {
        int daysToAdd = parseDays(daysAsString);

        Instant created = ticket.getDate_created();
        if (created == null) created = Instant.now(); // or throw

        ticket.setDeadline(created.plus(daysToAdd, ChronoUnit.DAYS));
    }

    private int parseDays(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    public int getUnresolvedTicketCount(){
        int ticketsUnresolved = 0;

        for (Ticket ticket : ticketList) {
            if(!ticket.getIs_resolved()) {
                ticketsUnresolved++;
            }
        }
        return ticketsUnresolved;
    }

    /// TODO: this isnt actually working but its not giving errors. fix so its returning an accurate count
    public int getPastDeadlineCount() {
        int count = 0;

        for (Ticket ticket : ticketList) {
            if (isDeadlineExpired(ticket.getDeadline())) {
                count++;
            }
        }
        return count;
    }
    public void createTicket(Ticket ticket) throws JsonProcessingException {
        ticketRepository.insert(ticket);
    }
}
