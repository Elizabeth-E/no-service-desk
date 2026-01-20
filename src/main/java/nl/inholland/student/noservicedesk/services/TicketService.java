package nl.inholland.student.noservicedesk.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
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

    public int getPastDeadlineCount() {
        int count = 0;

        for (Ticket ticket : ticketList) {
            if (isDeadlineExpired(ticket.getDeadline())) {
                count++;
            }
        }
        return count;
    }
    public void createTicket(Ticket ticket) {
        ticketRepository.insert(ticket);
    }

    public List<Ticket> getAllTicketsForUser(User user) {
        ticketList = ticketRepository.getTicketsByUser(user);
        return ticketList;
    }

    public void updateTicket(Ticket ticket) {
        try{
            if (ticket == null) {
                throw new IllegalArgumentException("Ticket cannot be null");
            }
            ticketRepository.update(ticket);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public void deleteTicket(Ticket ticket) throws JsonProcessingException {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }
        ticketRepository.deleteById(ticket.get_id());
    }
}
