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

    /**
     * Logs in the user by authenticating their credentials.
     * Prompts the user to input their username and password.
     * Sends the username and password to the authentication service for verification.
     * If the authentication service returns an error, displays the error message and indicates login failure.
     * If authentication is successful, parses the response containing user information,
     * sets the current user in the shared context to the authenticated user, and displays a success message.
     *
     * @throws ParseException if an error occurs during parsing of the authentication response
     */
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
