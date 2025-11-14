package nl.inholland.student.noservicedesk.services;

import nl.inholland.student.noservicedesk.database.MongoDB;

public class ServiceManager {
    private final MongoDB db;

    public final UserService userService;
    public final TicketService ticketService;
    public final HandledTicketsService handledTicketsService;

    public ServiceManager(MongoDB db) {
        this.db = db;
        db.connectDB();

        // Create all services here
        this.userService = new UserService(db);
        this.ticketService = new TicketService(db);
        this.handledTicketsService = new HandledTicketsService(db);
    }

    public UserService getUserService() {
        return userService;
    }

    public TicketService getTicketService() {
        return ticketService;
    }


    public HandledTicketsService getHandledTicketsService() {
        return handledTicketsService;
    }
}
