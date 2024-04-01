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

    protected Collection<String> getInquiryTitles(Collection<Inquiry> inquiries){
        ArrayList<String> toReturn = new ArrayList<String>();
        for(Inquiry inquiry : inquiries){
            toReturn.add(inquiry.getSubject()+(inquiry.getAssignedTo()==null?"":"[Assigned]"));
        }
        return toReturn;
    }

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
