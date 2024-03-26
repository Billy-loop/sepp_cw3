import model.AuthenticatedUser;
import model.SharedContext;
import view.TextUserInterface;
import external.MockAuthenticationService;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import controller.GuestController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.*;

public class GuestControllerTest {
    private GuestController controller;
    private TextUserInterface view;
    private MockAuthenticationService authService;
    private SharedContext sharedContext;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException, ParseException {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        authService = new MockAuthenticationService();
        sharedContext = new SharedContext();
    }

    @Test
    public void testLoginSuccess_AdminStaff() {
        String username = "JackTheRipper";
        String password = "catch me if u can";
        String email = "jack.tr@hindenburg.ac.uk";
        String input = username + "\n" + password + "\n";
        String role = "AdminStaff";
        String sucessMessage = "successfully logged in";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new GuestController(sharedContext, view, authService, null);

        controller.login();
        assertTrue(outputStream.toString().contains(sucessMessage));
        assertNotNull(sharedContext.getCurrentUser());
        assertEquals(email, ((AuthenticatedUser)sharedContext.getCurrentUser()).getEmail());
        assertEquals(role, ((AuthenticatedUser)sharedContext.getCurrentUser()).getRole());
    }

    @Test
    public void testLoginSuccess_Student() {
        String username = "Barbie";
        String password = "I like pink muffs and I cannot lie";
        String email = "barb78916@hindenburg.ac.uk";
        String input = username + "\n" + password + "\n";
        String role = "Student";
        String sucessMessage = "successfully logged in";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new GuestController(sharedContext, view, authService, null);

        controller.login();
        assertTrue(outputStream.toString().contains(sucessMessage));
        assertNotNull(sharedContext.getCurrentUser());
        assertEquals(email, ((AuthenticatedUser)sharedContext.getCurrentUser()).getEmail());
        assertEquals(role, ((AuthenticatedUser)sharedContext.getCurrentUser()).getRole());
    }

    @Test
    public void testLoginFailure_WrongPassword() {
        String username = "Barbie";
        String password = "I dont like pink muffs and I cannot lie";
        String email = "barb78916@hindenburg.ac.uk";
        String input = username + "\n" + password + "\n";
        String role = "AdminStaff";
        String success = "successfully logged in";
        String fail = "LOGIN Failure";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new GuestController(sharedContext, view, authService, null);

        controller.login();
        assertTrue(outputStream.toString().contains("Wrong username or password"));
        assertNull(sharedContext.getCurrentUser());
    }

    @Test
    public void testLoginFailure_NonexistentUser() {
        String username = "JohnDoe";
        String password = "LoremIpsum";
        String email = "john@doe.com";
        String input = username + "\n" + password + "\n";
        String role = "AdminStaff";
        String fail = "LOGIN Failure";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new GuestController(sharedContext, view, authService, null);

        controller.login();
        assertTrue(outputStream.toString().trim().contains("Wrong username or password"));
        assertNull(sharedContext.getCurrentUser());
    }
}