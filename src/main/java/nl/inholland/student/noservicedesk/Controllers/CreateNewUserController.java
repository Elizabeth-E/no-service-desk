package nl.inholland.student.noservicedesk.Controllers;

import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.UserService;

public class CreateNewUserController {
    private ServiceManager serviceManager;
    private UserService userService;
    private MainViewController mainViewController;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
