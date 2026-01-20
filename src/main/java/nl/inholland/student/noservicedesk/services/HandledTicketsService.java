package nl.inholland.student.noservicedesk.services;

import nl.inholland.student.noservicedesk.Models.HandledTicket;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.database.HandledTicketRepository;

import java.util.List;

public class HandledTicketsService{

    private final HandledTicketRepository handledTicketRepository;

    public HandledTicketsService(HandledTicketRepository handledTicketRepository) {
        this.handledTicketRepository = handledTicketRepository;
    }

    public void insertHandledTicket(HandledTicket handledTicket) {
        handledTicketRepository.insertHandledTicket(handledTicket);
    }

    public List<HandledTicket> getHandledTicketHistory(Ticket ticket) {
        return handledTicketRepository.getByTicketId(ticket.get_id());
    }
}
