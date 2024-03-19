package controller;

import external.AuthenticationService;
import external.EmailService;
import model.Inquiry;
import model.SharedContext;
import view.View;

import java.util.Collection;

public class StaffController extends Controller {
    public StaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){

    }

    protected Collection<String> getInquiryTitles(Collection<Inquiry> inquiries){
        return null;
    }

    protected void respondToInquiry(Inquiry inquiry){

    }
}
