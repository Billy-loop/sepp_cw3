import controller.MenuController;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.Guest;
import model.SharedContext;
import view.TextUserInterface;
import external.MockAuthenticationService;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSystemTests {
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
    /**
     * 1/When first accessing the system, the user shall see options to log in, consult the
     * FAQ, consult webpages, consult a member of staff.
     * 2/When not logging in, the user shall be considered a ”guest.”
     * 3/The system shall interact with the user via a simple text user interface.
     * */
    @Test
    public void firstAccessing_GuestOptionsTest() {
        String input = "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("Welcome"));

        assertTrue(outputStream.toString().contains("Guest"));
        assertTrue(outputStream.toString().contains("[0]LOGIN"));
        assertTrue(outputStream.toString().contains( "[1]CONSULT_FAQ"));
        assertTrue(outputStream.toString().contains("[2]SEARCH_PAGES"));
        assertTrue(outputStream.toString().contains("[3]CONTACT_STAFF"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * 6/Students, admin staff, and teaching staff shall be able to log into the system via
     * the external records system EASE.
     * 7/After log-in, students shall see a ”successfully logged in” message, as well as options
     * to log out, consult the FAQ, consult webpages, and consult a member of staff.
     * 8/When a user wishes to log in, the system shall connect to EASE via its API function
     * to login with just a username and password. The function will return user data
     * (name, email, role) if the username and password are correct.
     * */
    @Test
    public void firstAccessing_LoginOptions_AsStudentTest() {
        String input = "0\nBarbie\nI like pink muffs and I cannot lie\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

//        assertEquals(outputStream.toString(),("Welcome"));
//        assertTrue(outputStream.toString().contains("successfully logged in"));
//        assertTrue(outputStream.toString().contains("[0] to LOGOUT\n" +
//                "[1] to CONSULT_FAQ\n" +
//                "[2] to SEARCH_PAGES\n" +
//                "[3] to CONTACT_STAFF\n" +
//                "[-1] Return to welcome page\n"));
        assertTrue(outputStream.toString().contains("Student"));
        assertTrue(outputStream.toString().contains("[0]LOGOUT"));
        assertTrue(outputStream.toString().contains( "[1]CONSULT_FAQ"));
        assertTrue(outputStream.toString().contains("[2]SEARCH_PAGES"));
        assertTrue(outputStream.toString().contains("[3]CONTACT_STAFF"));
//        assertTrue(outputStream.toString().contains("[-1] Return to welcome page"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void firstAccessing_LoginOptions_AsAdminTest() {
        String input = "0\nJackTheRipper\ncatch me if u can\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("successfully logged in"));

        assertTrue(outputStream.toString().contains("Adminstaff"));
        assertTrue(outputStream.toString().contains("[0]LOGOUT"));
        assertTrue(outputStream.toString().contains("[1]MANAGE_QUERIES"));
        assertTrue(outputStream.toString().contains("[2]ADD_PAGE"));
        assertTrue(outputStream.toString().contains("[3]SEE_ALL_PAGES"));
        assertTrue(outputStream.toString().contains("[4]MANAGE_FAQ"));
//        assertTrue(outputStream.toString().contains("[-1] Return to welcome page"));

        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void firstAccessing_LoginOptions_AsTeachingStuffTest() {
        String input = "0\nJSON Derulo\nDesrouleaux\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("TeachingStaff"));
        assertTrue(outputStream.toString().contains("[0]LOGOUT"));
        assertTrue(outputStream.toString().contains( "[1]MANAGE_RECEIVED_QUERIES"));
//        assertTrue(outputStream.toString().contains("[-1] Return to welcome page"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * If not, it will return an error message.
     * */
    @Test
    public void firstAccessing_LoginFailTest() {
        String input = "0\nBarbie\ncatch me if u can\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

//        assertEquals(outputStream.toString(),("Welcome"));
        assertTrue(outputStream.toString().contains("{\"error\":\"Wrong username or password\"}"));
        assertNotNull(sharedContext.getCurrentUser());
    }

    /**
     * 9/Students and staff shall be able to log out of the system. This shall not use EASE.
     * */
    @Test
    public void firstAccessing_Logout_AsStudentTest() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("barb78916@hindenburg.ac.uk","Student"));
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

//        assertEquals(outputStream.toString(),("Welcome"));
        assertTrue(outputStream.toString().contains("Successfully Logout"));
        assertTrue(sharedContext.getCurrentUser() instanceof Guest);
    }
    @Test
    public void firstAccessing_Logout_AsAdminTest() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk","AdminStaff"));
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

//        assertEquals(outputStream.toString(),("Welcome"));
        assertTrue(outputStream.toString().contains("Successfully Logout"));
        assertTrue(sharedContext.getCurrentUser() instanceof Guest);
    }
    @Test
    public void firstAccessing_Logout_AsTeachingStuffTest() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("json.d@hindenburg.ac.uk","TeachingStaff"));
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

//        assertEquals(outputStream.toString(),("Welcome"));
        assertTrue(outputStream.toString().contains("Successfully Logout"));
        assertTrue(sharedContext.getCurrentUser() instanceof Guest);
    }


}