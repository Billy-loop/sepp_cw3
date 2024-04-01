import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.RepeatedTest;

import external.MockEmailService;

public class MockEmailServiceTest {
    MockEmailService emailService;

    @BeforeEach
    public void setUp(){
        emailService = new MockEmailService();
    }

    @DisplayName("Checking All Valid Emails")
    @RepeatedTest(5)
    public void validEmailTest(RepetitionInfo rInfo){
        String[] validEmails = {"user@example.com", "john.doe123@subdomain.example.co.uk",
                                "user_123+456@email-provider.com", "test-123+456@sub.sub.example.com",
                                "user@example-domain123.net"};

        String subject = "Subject", content = "Loading Content..",
                sender = "admin_staff@hindeburg.ac.uk", recipient = validEmails[rInfo.getCurrentRepetition()-1];

        assertEquals(0, emailService.sendEmail(sender, recipient, subject, content));

    }

    @DisplayName("Checking Invalid Sender Emails")
    @RepeatedTest(5)
    public void invalidSenderEmail(RepetitionInfo rInfo){
        String[] invalidSenderEmails = {"email!@testmail.com", "test@email2@gomail.com",
                                        "@newmail.mail", "user123@domain!.com",
                                        "correct.mail@exam!ple.com"};

        String subject = "Subject", content = "Loading Content..",
                sender = invalidSenderEmails[rInfo.getCurrentRepetition()-1], recipient = "reciver01@hindeburg.ac.uk";

        assertEquals(1, emailService.sendEmail(sender, recipient, subject, content));

    }

    @DisplayName("Checking Invalid Recipient Emails")
    @RepeatedTest(5)
    public void invalidReceiverEmail(RepetitionInfo rInfo){
        String[] invalidReceiverEmails = {"testuser@.com", "this is definitely correct email",
                "may.be.this_will.work", "testuser2@.example.com",
                "https//user@example-domain123.net"};

        String subject = "Subject", content = "Loading Content..",
                sender = "admin_is_sender@hindeburg.ac.uk", recipient = invalidReceiverEmails[rInfo.getCurrentRepetition()-1];

        assertEquals(2, emailService.sendEmail(sender, recipient, subject, content));

    }
}
