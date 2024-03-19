package controller;

import external.AuthenticationService;
import external.EmailService;
import model.FAQSection;
import model.Inquiry;
import model.SharedContext;
import view.View;

public class AdminStaffController extends StaffController{
    public  AdminStaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);

    }

    public void addPage(){

    }

    public void manageFAQ(){

    }

    public void addFAQItem(FAQSection faqSection){

    }

    public void ViewAllPages(){

    }

    public void manageInquiries(){

    }

    public void redirectInquiry(Inquiry inquiry){

    }
}
