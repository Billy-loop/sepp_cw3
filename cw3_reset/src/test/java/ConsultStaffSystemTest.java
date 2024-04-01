import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultStaffSystemTest {
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

    @Test
    public void ConsultStaffGuestTest(){
        String input = "3\ntest@test.com\ntestSubject\ntestContent";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
        assertTrue(outputStream.toString().contains("Email from test@test.com to jack.tr@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("testSubject"));
        assertTrue(outputStream.toString().contains("testContent"));
    }

    @Test
    public void ConsultStaffStudentTest(){
        String input = "3\nTestSubject\nTestContent\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("barb78916@hindenburg.ac.uk","Student"));
        controller = new MenuController(sharedContext,view,authService,emailService);

        try{
            controller.mainMenu();
        }catch (NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("Email from barb78916@hindenburg.ac.uk to jack.tr@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("TestSubject"));
        assertTrue(outputStream.toString().contains("TestContent"));
    }
}
