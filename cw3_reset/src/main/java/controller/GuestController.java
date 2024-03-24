package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import view.View;


public class GuestController extends Controller {
    public GuestController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);
    }

    public void login() {
        String username = view.getInput("Enter your username: ");
        String password = view.getInput("Enter your password: ");
        String response = authenticationService.login(username, password);
        if (response.contains("error")){
            view.displayError(response);
            view.displayInfo("LOGIN Failure");
            return;
        }
        try{
            JSONParser parser = new JSONParser();
            JSONObject user = (JSONObject) parser.parse(response);
            String email = (String) user.get("email");

            String role = (String) user.get("role");

            AuthenticatedUser currnetUser = new AuthenticatedUser(email, role);

            sharedContext.setCurrentUser(currnetUser);
            view.displaySuccess("successfully logged in");

        } catch (IllegalArgumentException | ParseException e) {
            view.displayException(e);
            view.displayInfo("LOGIN Failure");
        }
    }
}
