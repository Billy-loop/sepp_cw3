import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.Inquiry;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ManageInquirySystemTests {
    private MenuController controller;
    private TextUserInterface view;
    private MockAuthenticationService authService;
    private MockEmailService emailService;
    private SharedContext sharedContext;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException, ParseException {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        authService = new MockAuthenticationService();
        emailService = new MockEmailService();
        sharedContext = new SharedContext();
    }

}

