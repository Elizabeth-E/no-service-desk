package nl.inholland.student.noservicedesk.services;

import nl.inholland.student.noservicedesk.database.MongoConn;
import nl.inholland.student.noservicedesk.database.TicketRepository;
import nl.inholland.student.noservicedesk.database.UserRepository;

public class ServiceManager {

    public final UserService userService;
    public final TicketService ticketService;
    public final HandledTicketsService handledTicketsService;
    private final AuthService authService;

    public ServiceManager(MongoConn db) {
        //Create repos
        UserRepository userRepository = new UserRepository(db.users());
        TicketRepository ticketRepository = new TicketRepository(db.tickets());

        // Create all services here
        this.authService = new AuthService();
        authService.setUserRepository(userRepository);
        this.userService = new UserService(userRepository, authService);
        this.ticketService = new TicketService(ticketRepository);
        this.handledTicketsService = new HandledTicketsService(ticketRepository);

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
