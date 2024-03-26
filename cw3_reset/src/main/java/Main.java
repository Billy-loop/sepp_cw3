
import controller.MenuController;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import view.TextUserInterface;
import view.View;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    private SharedContext sharedContext;
    private AuthenticationService authenticationService;
    private View view;
    private EmailService emailService;
    private MenuController menu;

    public Main() throws URISyntaxException, IOException, ParseException, NullPointerException {
        sharedContext = new SharedContext();
        authenticationService = new MockAuthenticationService();
        emailService = new MockEmailService();
        view = new TextUserInterface(System.in);
        menu = new MenuController(sharedContext, view, authenticationService, emailService);
        menu.mainMenu();
    }
    public static void main(String[] args) throws URISyntaxException, IOException, ParseException,NullPointerException {
        Main main = new Main();
    }
}
