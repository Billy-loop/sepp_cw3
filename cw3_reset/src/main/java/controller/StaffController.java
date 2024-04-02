package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.Inquiry;
import model.SharedContext;
import view.View;

import java.util.ArrayList;
import java.util.Collection;

public class StaffController extends Controller {
    public StaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);
    }

    /**
     * Retrieves the titles of inquiries from the given collection of inquiries.
     * If an inquiry is assigned to a staff member, the title is suffixed with "[Assigned]".
     *
     * @param inquiries the collection of inquiries to extract titles from
     * @return a collection of inquiry titles
     */
    protected Collection<String> getInquiryTitles(Collection<Inquiry> inquiries){
        ArrayList<String> titles = new ArrayList<String>();
        for(Inquiry inquiry : inquiries){
            if (inquiry.getAssignedTo() == null){
                titles.add(inquiry.getSubject());
            }else{
                titles.add(inquiry.getSubject() + ":" + "[Assigned]");
            }
        }
        return titles;
    }

    /**
     * Responds to an inquiry by adding the provided response to the inquiry's content.
     * If the current user is authenticated, an email notification with the response is sent to the inquirer's email address.
     *
     * @param inquiry the inquiry to respond to
     */
    protected void respondToInquiry(Inquiry inquiry){
        String respond = this.view.getInput("Enter Answer:");
        inquiry.setContent(inquiry.getContent() + "\nAnswer: " + respond );
        if (sharedContext.getCurrentUser() instanceof AuthenticatedUser){
            this.emailService.sendEmail(((AuthenticatedUser)sharedContext.getCurrentUser()).getEmail(),
                    inquiry.getInquirerEmail(),
                    "Answer to "+inquiry.getSubject(), inquiry.getContent());
        }

        sharedContext.getInquiries().remove(inquiry);
    }
}
