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

    /**
     * 16/Staff shall be able to answer inquiries in the system.
     * (a) All admin staff shall be able to view the list of unanswered inquiries’ subject
     * lines, with the option of looking at any of them in detail.
     * */
    @Test
    public void AdminViewInquiryTest() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        controller = new MenuController(sharedContext, view, authService, emailService);

        Inquiry inquiry0 = new Inquiry("TestSub0","TestCon0","Test@com");
        Inquiry inquiry1 = new Inquiry("TestSub1","TestCon1","Test@com");
        Inquiry inquiry2 = new Inquiry("TestSub2","TestCon2","Test@com");
        Inquiry inquiry3 = new Inquiry("TestSub3","TestCon3","Test@com");
        Inquiry inquiry4 = new Inquiry("TestSub4","TestCon4","Test@com");

        sharedContext.getInquiries().add(inquiry0);
        sharedContext.getInquiries().add(inquiry1);
        sharedContext.getInquiries().add(inquiry2);
        sharedContext.getInquiries().add(inquiry3);
        sharedContext.getInquiries().add(inquiry4);

        inquiry0.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry1.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry2.setAssignedTo("bananaman@hindeburg.ac.uk");

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("Unanswered Inquires:"));
        assertTrue(outputStream.toString().contains("[0]: TestSub0:[Assigned]"));
        assertTrue(outputStream.toString().contains("[1]: TestSub1:[Assigned]"));
        assertTrue(outputStream.toString().contains("[2]: TestSub2:[Assigned]"));
        assertTrue(outputStream.toString().contains("[3]: TestSub3"));
        assertTrue(outputStream.toString().contains("[4]: TestSub4"));
        assertTrue(outputStream.toString().contains("[-1] to Return"));

        assertEquals(5, sharedContext.getInquiries().size());
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (b) All admin staff shall be able to reassign any inquiry to someone else, including
     * a particular member of teaching staff, by providing their email address. In
     * this case, the system shall email the member of teaching staff, notify them
     * about the inquiry, mention its subject line, and ask them to log in to the
     * Self Service Portal to review the inquiry. This shall be kept hidden from the
     * original inquirer.
     * */
    @Test
    public void AdminReassignInquiryTest() {
        String input = "1\n0\n1\njson.d@hindenburg.ac.uk\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));

        Inquiry inquiry0 = new Inquiry("TestSub0","TestCon0","Test@com");
        Inquiry inquiry1 = new Inquiry("TestSub1","TestCon1","Test@com");
        Inquiry inquiry2 = new Inquiry("TestSub2","TestCon2","Test@com");
        Inquiry inquiry3 = new Inquiry("TestSub3","TestCon3","Test@com");
        Inquiry inquiry4 = new Inquiry("TestSub4","TestCon4","Test@com");

        sharedContext.getInquiries().add(inquiry0);
        sharedContext.getInquiries().add(inquiry1);
        sharedContext.getInquiries().add(inquiry2);
        sharedContext.getInquiries().add(inquiry3);
        sharedContext.getInquiries().add(inquiry4);

        inquiry0.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry1.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry2.setAssignedTo("bananaman@hindeburg.ac.uk");

        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to json.d@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("New Inquiry:TestSub0"));
        assertTrue(outputStream.toString().contains("TestCon0"));

        assertNotNull(inquiry0.getAssignedTo());

    }
    /**
     * (c) All admin staff shall be able to provide an answer to any inquiry by providing
     * text as a response to the inquirer.
     * (d) All teaching staff shall be able to view and provide an answer to an inquiry
     * reassigned to them by providing text as a response to the inquirer.
     * */
    @Test
    public void AdminAnswerInquiryTest() {
        String input = "1\n4\n0\nTestAnswer\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("drunkpanda@hindeburg.ac.uk", "AdminStaff"));

        Inquiry inquiry0 = new Inquiry("TestSub0","TestCon0","Test@com");
        Inquiry inquiry1 = new Inquiry("TestSub1","TestCon1","Test@com");
        Inquiry inquiry2 = new Inquiry("TestSub2","TestCon2","Test@com");
        Inquiry inquiry3 = new Inquiry("TestSub3","TestCon3","Test@com");
        Inquiry inquiry4 = new Inquiry("TestSub4","TestCon4","Test@com");

        sharedContext.getInquiries().add(inquiry0);
        sharedContext.getInquiries().add(inquiry1);
        sharedContext.getInquiries().add(inquiry2);
        sharedContext.getInquiries().add(inquiry3);
        sharedContext.getInquiries().add(inquiry4);

        inquiry0.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry1.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry2.setAssignedTo("bananaman@hindeburg.ac.uk");

        controller = new MenuController(sharedContext, view, authService, emailService);
        assertEquals(5, sharedContext.getInquiries().size());

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertEquals(4, sharedContext.getInquiries().size());

    }

    @Test
    public void TeachingStaffAnswerEmptyInquiriesTest() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("bananaman@hindeburg.ac.uk", "TeachingStaff"));

        Inquiry inquiry0 = new Inquiry("TestSub0","TestCon0","Test@com");
        Inquiry inquiry1 = new Inquiry("TestSub1","TestCon1","Test@com");
        Inquiry inquiry2 = new Inquiry("TestSub2","TestCon2","Test@com");
        Inquiry inquiry3 = new Inquiry("TestSub3","TestCon3","Test@com");
        Inquiry inquiry4 = new Inquiry("TestSub4","TestCon4","Test@com");

        sharedContext.getInquiries().add(inquiry0);
        sharedContext.getInquiries().add(inquiry1);
        sharedContext.getInquiries().add(inquiry2);
        sharedContext.getInquiries().add(inquiry3);
        sharedContext.getInquiries().add(inquiry4);

        controller = new MenuController(sharedContext, view, authService, emailService);
        assertEquals(5, sharedContext.getInquiries().size());
        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("You have 0 inquires to be answered."));
        assertEquals(5, sharedContext.getInquiries().size());

    }

    @Test
    public void TeachingStaffAnswerInquiriesTest() {
        String input = "1\n0\nTestAnswer\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("bananaman@hindeburg.ac.uk", "TeachingStaff"));

        Inquiry inquiry0 = new Inquiry("TestSub0","TestCon0","Test@com");
        Inquiry inquiry1 = new Inquiry("TestSub1","TestCon1","Test@com");
        Inquiry inquiry2 = new Inquiry("TestSub2","TestCon2","Test@com");
        Inquiry inquiry3 = new Inquiry("TestSub3","TestCon3","Test@com");
        Inquiry inquiry4 = new Inquiry("TestSub4","TestCon4","Test@com");

        sharedContext.getInquiries().add(inquiry0);
        sharedContext.getInquiries().add(inquiry1);
        sharedContext.getInquiries().add(inquiry2);
        sharedContext.getInquiries().add(inquiry3);
        sharedContext.getInquiries().add(inquiry4);

        inquiry0.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry1.setAssignedTo("bananaman@hindeburg.ac.uk");
        inquiry2.setAssignedTo("bananaman@hindeburg.ac.uk");

        controller = new MenuController(sharedContext, view, authService, emailService);
        assertEquals(5, sharedContext.getInquiries().size());
        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("You have 3 inquires to be answered."));
        assertEquals(4, sharedContext.getInquiries().size());

    }
}

