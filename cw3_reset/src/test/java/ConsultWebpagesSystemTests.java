import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.Page;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConsultWebpagesSystemTests {
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
    public void insertPages(){
        sharedContext.addPage(new Page("Test1","Test1", false));
        sharedContext.addPage(new Page("Test7","Test2", false));
        sharedContext.addPage(new Page("Test3","Test1", false));
        sharedContext.addPage(new Page("Test4","Test1", false));
        sharedContext.addPage(new Page("Test5","Test1", false));
        sharedContext.addPage(new Page("Test6","Test1", false));

        sharedContext.addPage(new Page("Test2","Test2", false));
        sharedContext.addPage(new Page("Test8","Test2", true));
        sharedContext.addPage(new Page("Test9","Test2", true));
        sharedContext.addPage(new Page("Test10","Test2", false));
    }
    /**
     * 12/Inquirers shall have the option to perform a keyword search on the university’s
     * webpages using the Lucene library.
     * (a) The system shall store the raw text file versions of the webpages. These shall
     * be managed by admin staff (see section 5).
     * (b) When consulting webpages, the system shall prompt the inquirer for a search
     * query (which could be keywords and special symbols).
     * */
    @Test
    public void consultWebPage_GuestEmptySearchTest() {
        String input = "2\nTest\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
//        sharedContext.setCurrentUser(new AuthenticatedUser("barb78916@hindenburg.ac.uk","Student"));
        controller = new MenuController(sharedContext, view, authService, emailService);
        sharedContext = new SharedContext();

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("Search Results:"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultWebPage_StudentEmptySearchTest() {
        String input = "2\nTest\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("barb78916@hindenburg.ac.uk","Student"));
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
        assertTrue(outputStream.toString().contains("Search Results:"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (c) The system shall return to the inquirer up to 4 of the most relevant paragraphs
     * along with names of the webpages containing those paragraphs (provided that
     * any match). When there are more than 4 paragraphs and/or documents to
     * choose from, any of the choices are fine.
     * (d) Pages (or any text from those pages) marked as private by admin staff shall
     * not be returned to guests when performing a keyword search, but shall freely
     * appear to logged-in users (students and staff).
     */
    @Test
    public void consultWebPage_GuestNotEmptySearchTest1() {
        String input = "2\nTest1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
//        sharedContext.setCurrentUser(new AuthenticatedUser("barb78916@hindenburg.ac.uk","Student"));
        insertPages();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
        assertFalse(outputStream.toString().contains("Document<stored,indexed,tokenized<title:Test2> stored,indexed,tokenized<content:Test1>>"));
//        assertEquals(outputStream.toString(), "Welcome");
        assertTrue(outputStream.toString().contains("Test1"));
        assertFalse(outputStream.toString().contains("Test2"));

        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultWebPage_GuestNotEmptySearchTest2() {
        String input = "2\nTest2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        insertPages();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(), "Welcome");
        assertFalse(outputStream.toString().contains("Test1:"));
        assertTrue(outputStream.toString().contains("Test2:"));
        assertFalse(outputStream.toString().contains("Test8"));
        assertFalse(outputStream.toString().contains("Test9"));

        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultWebPage_StudentNotEmptySearchTest2() {
        String input = "2\nTest2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        insertPages();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(), "Welcome");
        assertTrue(
                outputStream.toString().contains("Test2")  |
                        outputStream.toString().contains("Test8")   |
                        outputStream.toString().contains("Test9")   |
                        outputStream.toString().contains("Test10")
        );
        assertFalse(outputStream.toString().contains("Test1:"));

        assertNotNull(sharedContext.getCurrentUser());
    }
}