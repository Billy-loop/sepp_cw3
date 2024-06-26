package controller;

import external.AuthenticationService;
import external.EmailService;
import model.Guest;
import model.SharedContext;
import view.View;

public class AuthenticatedUserController extends Controller{
    public AuthenticatedUserController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);
    }

    /**
     * After logout, user will be a guest.
     */

    public void logout(){
        this.sharedContext.setCurrentUser(new Guest());
    }
}
