import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ConsultFAQSystemTest {
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
    private void insertQAs(){
        /**
         * allTopics
         * -topic1
         * --subtopic1-1
         * ---QA1_1-1
         * --QA1-1
         * --QA1-2
         * -topic2
         * --QA2-1
         * --QA2-2
         * */
        FAQSection root = sharedContext.getFAQ().getfaqSection();
        FAQSection testTopicSection1 = new FAQSection("TestTopic1");
        FAQSection testTopicSection2 = new FAQSection("TestTopic2");
        testTopicSection1.setParent(root);
        testTopicSection2.setParent(root);
        FAQSection testTopicSection1_1 = new FAQSection("TestTopic1-1");
        testTopicSection1_1.setParent(testTopicSection1);
        testTopicSection1_1.getItems().add(new FAQItem("TestQuestion1_1-1", "TestAnswer1_1-1"));
        testTopicSection1.getSubSections().add(testTopicSection1_1);
        testTopicSection1.getItems().add(new FAQItem("TestQuestion1-1", "TestAnswer1-1"));
        testTopicSection1.getItems().add(new FAQItem("TestQuestion1-2", "TestAnswer1-2"));
        testTopicSection2.getItems().add(new FAQItem("TestQuestion2-1", "TestAnswer2-1"));
        testTopicSection2.getItems().add(new FAQItem("TestQuestion2-2", "TestAnswer2-2"));
        root.getSubSections().add(testTopicSection1);
        root.getSubSections().add(testTopicSection2);
    }
    /**
     * 10/The system shall allow students and guests (from now on referred to as inquirers)
     * to consult the FAQ, which shall contain question-answer pairs divided into topics.
     * (a) The system shall store the topics and question-answer pairs. These shall be
     * managed by admin staff (see section 5).
     * (b) The topics shall be grouped into a nested hierarchy. Question-answer pairs
     * shall be present at each level of this hierarchy, except at the very top of the
     * FAQ, which shall only contain topics.
     * */
    @Test
    public void consultFAQ_GuestOptionsWithoutFAQTest() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(), "welcome");
        assertTrue(outputStream.toString().contains("FAQ"));
        assertTrue(outputStream.toString().contains("Topic: root"));
        assertTrue(outputStream.toString().contains("[-1] Return to main menu"));
//        assertTrue(outputStream.toString().contains("FAQ\n" +
//                "Topic: root\n" +
//                "[-1] Return to main menu\n"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (c) When consulting the FAQ, inquirers shall first be provided with top-most
     * level of the hierarchy (which only contains topics). Inquirers shall then be
     * able to select one of them, in which case the system shall show them the
     * questions-answer pairs in that topic, as well as direct sub-topics (i.e., one level
     * down), if any. When not at in the highest level topic, the system shall also
     * show super-topics (i.e., one level up).
     * (d) Inquirers shall be able to go up and down the topic hierarchy, one level at a
     * time.
     * */
    @Test
    public void consultFAQ_GuestOptionsWithFAQTest() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(), "welcome");
//        assertTrue(outputStream.toString().contains("FAQ\n" +
//                "Topic: root\n" +
//                "-Subtopic [0]:TestTopic1\n" +
//                "-Subtopic [1]:TestTopic2\n"));
        assertTrue(outputStream.toString().contains("FAQ"));
        assertTrue(outputStream.toString().contains("Topic: root"));
        assertTrue(outputStream.toString().contains("-Subtopic [0]:TestTopic1"));
        assertTrue(outputStream.toString().contains("-Subtopic [1]:TestTopic2"));

        assertFalse(outputStream.toString().contains("TestQuestion1-1"));
        assertFalse(outputStream.toString().contains("TestQuestion1-2"));
        assertFalse(outputStream.toString().contains("TestQuestion2-1"));
        assertFalse(outputStream.toString().contains("TestQuestion2-2"));
        assertFalse(outputStream.toString().contains("TestQuestion1_1-1"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultFAQ_GuestSelectTest() {
        String input = "1\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(),"welcome");
//        assertTrue(outputStream.toString().contains("Topic: TestTopic1\n" +
//                "SuperTopics root \n" +
//                "-Subtopic [0]:TestTopic1-1\n" +
//                "--Q: TestQuestion1-1-A:TestAnswer1-1\n" +
//                "--Q: TestQuestion1-2-A:TestAnswer1-2\n"));
        assertTrue(outputStream.toString().contains("SuperTopics root"));
        assertTrue(outputStream.toString().contains("Topic: TestTopic1"));
        assertTrue(outputStream.toString().contains("-Subtopic [0]:TestTopic1-1"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion1-1-A:TestAnswer1-1"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion1-2-A:TestAnswer1-2"));


        assertFalse(outputStream.toString().contains("TestQuestion2-1"));
        assertFalse(outputStream.toString().contains("TestQuestion2-2"));
        assertFalse(outputStream.toString().contains("TestQuestion1_1-1"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultFAQ_GuestMoveDownTest1() {
        String input = "1\n0\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(),"welcome");
//        assertTrue(outputStream.toString().contains("Topic: TestTopic1-1\n" +
//                "SuperTopics TestTopic1 TestTopic2 \n" +
//                "--Q: TestQuestion1_1-1-A:TestAnswer1_1-1"));
        assertTrue(outputStream.toString().contains("SuperTopics TestTopic1 TestTopic2"));
        assertTrue(outputStream.toString().contains("Topic: TestTopic1-1"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion1_1-1-A:TestAnswer1_1-1"));
        assertTrue(outputStream.toString().contains("[-1] Return to TestTopic1"));
        assertTrue(outputStream.toString().contains("[-2] to request updates for this topic"));
        assertTrue(outputStream.toString().contains("[-3] to stop receiving updates for this topic"));

//        assertTrue(outputStream.toString().contains("TestQuestion1-1"));
//        assertTrue(outputStream.toString().contains("TestQuestion1-2"));
//        assertFalse(outputStream.toString().contains("TestQuestion2-1"));
//        assertFalse(outputStream.toString().contains("TestQuestion2-2"));
//        assertTrue(outputStream.toString().contains("TestQuestion1_1-1"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultFAQ_GuestMoveUpTest() {
        String input = "1\n0\n0\n-1\n-1\n1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(),"Welcome");
//        assertTrue(outputStream.toString().contains("SuperTopics root \n" +
//                "Topic: TestTopic2\n" +
//                "--Q: TestQuestion2-1-A:TestAnswer2-1\n" +
//                "--Q: TestQuestion2-2-A:TestAnswer2-2\n" +
//                "[-1] Return to All Topics\n" +
//                "[-2] to request updates for this topic\n" +
//                "[-3] to stop receiving updates for this topic"));
        assertTrue(outputStream.toString().contains("SuperTopics root"));
        assertTrue(outputStream.toString().contains("Topic: TestTopic2"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion2-1-A:TestAnswer2-1"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion2-2-A:TestAnswer2-2"));
        assertTrue(outputStream.toString().contains("[-1] Return to All Topics"));
        assertTrue(outputStream.toString().contains("[-2] to request updates for this topic"));
        assertTrue(outputStream.toString().contains("[-3] to stop receiving updates for this topic"));

//        assertTrue(outputStream.toString().contains("TestQuestion1-1"));
//        assertTrue(outputStream.toString().contains("TestQuestion1-2"));
//        assertTrue(outputStream.toString().contains("TestQuestion2-1"));
//        assertTrue(outputStream.toString().contains("TestQuestion2-2"));
//        assertTrue(outputStream.toString().contains("TestQuestion1_1-1"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * 11/Inquirers shall be able to request updates for a particular FAQ topic that will notify
     * them if it later gets changed by admin staff.
     * (a) Request updates shall be presented to inquirers as another option in addition
     * to navigation options when browsing the FAQ. When updates are made to
     * this topic, e.g., question-answer pairs are added, edited, or deleted (but not
     * sub-topics or super-topics), the system shall automatically email the list of
     * topic question-answer pairs to all inquirers who requested updates on the
     * topic.
     *
     *
     * (d) While browsing the FAQ, guests shall always be given the options to request
     * updates or to stop receiving updates for the current topic they are at.
     * (e) If a guest selects the option to stop receiving updates but has not previous
     * requested updates for that topic, the system shall ask for their email address.
     * If the entered email is not in the list of subscribers to updates, the system
     * shall show a warning and do nothing.
     * */
    @Test
    public void consultFAQ_GuestRequestUpdateTest() {
        String input = "1\n0\n-2\ntest@test.com\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);
        insertQAs();
        assertEquals(0, sharedContext.getFaqTopicsUpdateSubscribers().size());
        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(), "Welcome");
        assertTrue(outputStream.toString().contains("[-2] to request updates for this topic"));
        assertTrue(outputStream.toString().contains("[-3] to stop receiving updates for this topic"));
        assertTrue(outputStream.toString().contains("Successfully registered test@test.com for updates on TestTopic1"));
        assertEquals(1, sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").size());
        assertTrue(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").contains("test@test.com"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultFAQ_GuestStopUpdateTest() {
        String input = "1\n0\n-3\ntest@test.com\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);
        insertQAs();
        assertEquals(0, sharedContext.getFaqTopicsUpdateSubscribers().size());
        sharedContext.getFaqTopicsUpdateSubscribers().put("TestTopic1",new ArrayList<>(List.of("test@test.com", "barb78916@hindenburg.ac.uk")));
        assertEquals(2, sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").size());
        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(), "Welcome");
        assertTrue(outputStream.toString().contains("[-2] to request updates for this topic"));
        assertTrue(outputStream.toString().contains("[-3] to stop receiving updates for this topic"));
        assertTrue(outputStream.toString().contains("Successfully unregistered test@test.com for updates on TestTopic1"));
        assertEquals(1, sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").size());
        assertFalse(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").contains("test@test.com"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultFAQ_GuestStopWrongUpdateTest() {
        String input = "1\n0\n-3\ntest@test.com\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        controller = new MenuController(sharedContext, view, authService, emailService);
        insertQAs();
        assertEquals(0, sharedContext.getFaqTopicsUpdateSubscribers().size());
        sharedContext.getFaqTopicsUpdateSubscribers().put("TestTopic1",new ArrayList<>(List.of("barb78916@hindenburg.ac.uk")));
        assertEquals(1, sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").size());
        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals(outputStream.toString(), "Welcome");
        assertTrue(outputStream.toString().contains("[-2] to request updates for this topic"));
        assertTrue(outputStream.toString().contains("[-3] to stop receiving updates for this topic"));
        assertTrue(outputStream.toString().contains("Failed to unregister test@test.com for updates on TestTopic1 Perhaps this email was not registered?"));
        assertFalse(outputStream.toString().contains("Successfully unregistered test@test.com for updates on TestTopic1"));
        assertEquals(1, sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").size());
        assertFalse(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").contains("test@test.com"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void consultFAQ_StudentRequestUpdateTest() {
        String input = "1\n0\n-2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("barb78916@hindenburg.ac.uk","Student"));
        controller = new MenuController(sharedContext, view, authService, emailService);
        insertQAs();
        assertEquals(0, sharedContext.getFaqTopicsUpdateSubscribers().size());
        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
        assertTrue(outputStream.toString().contains("[-2] to request updates for this topic"));
        assertTrue(outputStream.toString().contains("Successfully registered barb78916@hindenburg.ac.uk for updates on TestTopic1"));
//        System.out.println(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1"));
//        assertEquals(outputStream.toString(), "Welcome");
        assertEquals(1, sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").size());
        assertTrue(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").contains("barb78916@hindenburg.ac.uk"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (b) While browsing the FAQ, logged-in students shall be given the option to stop
     * getting updates instead of the option to request updates if they are on a topic
     * that they had previously requested updates for.
     * (c) The system shall give a confirmation to logged-in students when they request
     * updates. This confirmation shall specify the topic on which they requested
     * updates, as well as their email address where they would receive these updates.
     * */
    @Test
    public void consultFAQ_StudentStopUpdateTest() {
        String input = "1\n0\n-2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("barb78916@hindenburg.ac.uk","Student"));
        controller = new MenuController(sharedContext, view, authService, emailService);
        insertQAs();
        assertEquals(0, sharedContext.getFaqTopicsUpdateSubscribers().size());
        sharedContext.getFaqTopicsUpdateSubscribers().put("TestTopic1",new ArrayList<>(List.of("barb78916@hindenburg.ac.uk")));
        assertEquals(1, sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").size());
        assertTrue(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").contains("barb78916@hindenburg.ac.uk"));
        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
        assertTrue(outputStream.toString().contains("[-2] to request updates for this topic"));
        assertTrue(outputStream.toString().contains("[-2] to stop receiving updates for this topic"));
        assertTrue(outputStream.toString().contains("Successfully unregistered barb78916@hindenburg.ac.uk for updates on TestTopic1"));
//        System.out.println(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1"));
//        assertEquals(outputStream.toString(), "Welcome");
        assertFalse(sharedContext.getFaqTopicsUpdateSubscribers().get("TestTopic1").contains("barb78916@hindenburg.ac.uk"));
        assertNotNull(sharedContext.getCurrentUser());
    }
}