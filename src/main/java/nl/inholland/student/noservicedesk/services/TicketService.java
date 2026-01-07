package nl.inholland.student.noservicedesk.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.database.TicketRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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

    public boolean checkDeadline(String deadlineString) {
        try {
            if (deadlineString == null) return false;
            String s = deadlineString.trim();
            if (s.isEmpty() || s.matches("\\d+")) return false;

            Instant deadline = parseToInstant(s);
            return deadline.isBefore(Instant.now());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setDeadlineFromCreatedAndDays(Ticket ticket, String daysAsString) {
        int daysToAdd;
        try {
            daysToAdd = Integer.parseInt(daysAsString.trim());
        } catch (NumberFormatException e) {
            daysToAdd = 0;
        }

        Instant created = parseToInstant(ticket.getDate_created());
        Instant deadline = created.plus(Duration.ofDays(daysToAdd));

        // store back as Mongo-style ISO string with Z
        ticket.setDeadline(deadline.toString());
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
    public void createTicket(Ticket ticket) throws JsonProcessingException {

        ticketRepository.insert(ticket);
    }

    private static Instant parseToInstant(String dateStr) {
        String s = dateStr.trim();

        // ISO with offset/Z (Mongo style)
        try {
            return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
        } catch (Exception ignored) {}

        // ISO without offset (your LocalDateTime.toString())
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .toInstant(ZoneOffset.UTC);
        } catch (Exception ignored) {}

        throw new IllegalArgumentException("Unparseable date: " + dateStr);
    }

}
