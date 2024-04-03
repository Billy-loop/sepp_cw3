import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ManageFAQPageSystemTests {
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
     * 13/Admin staff shall be given the capability to manage and update the FAQ for the
     * system
     * (a) Admin staff shall be able to browse the current FAQ and its topic hierarchy
     * in the same way inquirers can: by starting from the top topic and being able
     * to move up and down in the topic hierarchy.
     * (b) At each level of the topic hierarchy, admin staff shall be given the option to
     * add a new question-answer pair.
     * Admin staff shall optionally be able to choose to create a subtopic for the new
     * question-answer pair (this is required if they are at the root of the FAQ).
     * */
    @Test
    public void manageFAQ_BrowseTest() {
        String input = "4\n0\n-1\n1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals("Welcome", outputStream.toString());
        assertTrue(outputStream.toString().contains("TestTopic1")&&outputStream.toString().contains("TestTopic2")&&outputStream.toString().contains("[-2] to add Q-A pair"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (c) Admin staff shall optionally be able to choose to create a subtopic for the new
     * question-answer pair (this is required if they are at the root of the FAQ).
     * (f) Upon a successful addition, an email shall be sent from the current admin
     * staff’s email to the general admin staff email notifying that an FAQ topic was
     * updated and includes all question-answer pairs in that topic.
     * */
    @Test
    public void manageFAQ_AddQARootTest() {
        String input = "4\n-2\nTestQuestion\nTestAnswer\nnewTestTopic\n2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals("Welcome", outputStream.toString());
        assertTrue(outputStream.toString().contains("Topic: newTestTopic"));
        assertTrue(outputStream.toString().contains( "SuperTopics root"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion-A:TestAnswer"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void manageFAQ_AddQANotRootTest1() {
        String input = "4\n0\n-2\nTestQuestion\nTestAnswer\nNo\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        assertTrue(outputStream.toString().contains("Topic: TestTopic1"));
        assertTrue(outputStream.toString().contains("SuperTopics root"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion1-1-A:TestAnswer1-1"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion1-2-A:TestAnswer1-2"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion-A:TestAnswer"));

        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to jack.tr@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("FAQ Topic TestTopic1 Updated"));

        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void manageFAQ_AddQANotRootTest2() {
        String input = "4\n0\n-2\nTestQuestion\nTestAnswer\nYes\nnewTestTopic\n1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals("Welcome", outputStream.toString());

        assertTrue(outputStream.toString().contains("Topic: newTestTopic"));
        assertTrue(outputStream.toString().contains("SuperTopics TestTopic1 TestTopic2"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion-A:TestAnswer"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (d) If an admin staff member tries to add an FAQ topic that already exists (two
     * FAQ topics are considered the same if they share the same title and place in
     * the hierarchy), the system shall produce a warning, but keep the old topic
     * and insert the new question-answer pair into it.
     * */
    @Test
    public void manageFAQ_AddQASameTitleTest() {
        String input = "4\n-2\nTestQuestion\nTestAnswer\nTestTopic1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        insertQAs();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals("Welcome", outputStream.toString());
        assertTrue(outputStream.toString().contains("Topic already exist, inserting to old one."));

        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to jack.tr@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("FAQ Topic TestTopic1 Updated"));
        assertTrue(outputStream.toString().contains("Q:TestQuestion1-1;A:TestAnswer1-1;"));
        assertTrue(outputStream.toString().contains("Q:TestQuestion1-2;A:TestAnswer1-2;"));
        assertTrue(outputStream.toString().contains("Q:TestQuestion;A:TestAnswer;"));
        assertFalse(outputStream.toString().contains("-Subtopic [2]:TestTopic1"));
        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (e) Admin staff shall then be prompted to provide the question-answer pair, which
     * shall then be added to the current topic/subtopic. There shall be no checking
     * for duplicates, and it shall not be possible to delete/change them, only to add
     * new ones.
     * (g) An email shall then be sent from the general admin staff email to all subscribers
     * that have requested updates for the topic with all Q&As in the topic.
     * */
    @Test
    public void manageFAQ_AddQANotRootWithSubscribersTest() {
        String input = "4\n0\n-2\nTestQuestion\nTestAnswer\nNo\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        insertQAs();
        sharedContext.getFaqTopicsUpdateSubscribers().put("TestTopic1", new ArrayList<>(List.of("barb78916@hindenburg.ac.uk", "captainobvious@hindeburg.ac.uk")));
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals("Welcome", outputStream.toString());
        assertTrue(outputStream.toString().contains("Topic: TestTopic1"));
        assertTrue(outputStream.toString().contains("SuperTopics root"));
        assertTrue(outputStream.toString().contains("-Subtopic [0]:TestTopic1-1"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion1-1-A:TestAnswer1-1"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion1-2-A:TestAnswer1-2"));
        assertTrue(outputStream.toString().contains("--Q: TestQuestion-A:TestAnswer"));

        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to jack.tr@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to barb78916@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to captainobvious@hindeburg.ac.uk"));

        assertTrue(outputStream.toString().contains("FAQ Topic TestTopic1 Updated"));
        assertTrue(outputStream.toString().contains("Q:TestQuestion1-1;A:TestAnswer1-1;"));
        assertTrue(outputStream.toString().contains("Q:TestQuestion1-2;A:TestAnswer1-2;"));
        assertTrue(outputStream.toString().contains("Q:TestQuestion;A:TestAnswer;"));
        assertFalse(outputStream.toString().contains("-Subtopic [2]:TestTopic1"));


        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * 14/Admin staff shall be given the capability to manage and update the raw text
     * versions of the university’s Webpages for the system
     * (a) Admin staff shall be given the option browse the webpages. If selected, the
     * system shall provide the list of existing webpage names and an option to add
     * a new page. The staff shall be able to select any of the existing pages to view
     * the full page contents.
     * (b) If admin staff chooses to add a new page, they shall be prompted to provide
     * its title, a raw non-empty text file in .txt format, and whether the webpage is
     * public or private.
     * */
    @Test
    public void BrowsePageTest() {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        insertPages();
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }
//        assertEquals("Welcome", outputStream.toString());
        assertTrue(outputStream.toString().contains("title: Test1 content:Test1"));
        assertTrue(outputStream.toString().contains("title: Test7 content:Test2"));
        assertTrue(outputStream.toString().contains("title: Test3 content:Test1"));
        assertTrue(outputStream.toString().contains("title: Test4 content:Test1"));
        assertTrue(outputStream.toString().contains("title: Test5 content:Test1"));
        assertTrue(outputStream.toString().contains("title: Test6 content:Test1"));
        assertTrue(outputStream.toString().contains("title: Test2 content:Test2"));
        assertTrue(outputStream.toString().contains("title: Test8 content:Test2"));
        assertTrue(outputStream.toString().contains("title: Test9 content:Test2"));
        assertTrue(outputStream.toString().contains("title: Test10 content:Test2"));


        assertNotNull(sharedContext.getCurrentUser());
    }
    @Test
    public void addPageNoOverwriteTest() throws IOException {
        File customDir = new File(System.getProperty("user.home"));
        if (!customDir.exists()) {
            boolean wasCreated = customDir.mkdirs();
            if (!wasCreated) {
                System.out.println("Could not create the custom directory.");
                throw new IOException();
            }
        }

        File tempFile = File.createTempFile("TestTitle", ".txt", customDir);
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Test1\nTest2\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String input = "2\nTestTitle\n"+tempFile.getAbsolutePath()+"\nNo\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        System.out.println("Temporary file created at: " + tempFile.getAbsolutePath());
        tempFile.delete();

        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to jack.tr@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("TestTitle"));
        assertTrue(outputStream.toString().contains("Test1Test2"));

        assertNotNull(sharedContext.getCurrentUser());
    }
    /**
     * (c) If an admin staff member tries to add a page with the same title as an existing
     * page, the system shall ask if the user wants to overwrite the existing page, and
     * if confirmed, overwrite the old page with the new one. This is effectively the
     * editing functionality (directly removing or adding pages shall not be possible).
     * (d) Upon a successful addition, an email shall be sent from the current admin
     * staff’s email to the general admin staff email notifying that a webpage was
     * updated and includes the entirety of the changed webpage (i.e. the contents
     * of its raw text file).
     * */
    @Test
    public void addPageOverwriteTest() throws IOException {
        File customDir = new File(System.getProperty("user.home"));
        if (!customDir.exists()) {
            boolean wasCreated = customDir.mkdirs();
            if (!wasCreated) {
                System.out.println("Could not create the custom directory.");
                throw new IOException();
            }
        }
        insertPages();
        assertEquals(10, sharedContext.getPages().size());

        File tempFile = File.createTempFile("TestTitle", ".txt", customDir);
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Test1\nTest2\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String input = "2\nTest1\n"+tempFile.getAbsolutePath()+"\nNo\nYes\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface(System.in);
        sharedContext.setCurrentUser(new AuthenticatedUser("jack.tr@hindenburg.ac.uk", "AdminStaff"));
        controller = new MenuController(sharedContext, view, authService, emailService);

        try {
            controller.mainMenu();
        }catch(NoSuchElementException e){
            System.out.println("Skip rest");
        }

        System.out.println("Temporary file created at: " + tempFile.getAbsolutePath());
        tempFile.delete();

        assertTrue(outputStream.toString().contains("PageTest1already exists Overwrite with new page?"));
        assertTrue(outputStream.toString().contains("Email from jack.tr@hindenburg.ac.uk to jack.tr@hindenburg.ac.uk"));
        assertTrue(outputStream.toString().contains("Test1"));
        assertTrue(outputStream.toString().contains("Test1Test2"));
        assertEquals(10, sharedContext.getPages().size());
        assertNotNull(sharedContext.getCurrentUser());
    }

}
