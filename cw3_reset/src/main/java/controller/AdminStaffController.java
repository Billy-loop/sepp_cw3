package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import view.View;

import java.util.*;

import static external.EmailService.STATUS_SUCCESS;

public class AdminStaffController extends StaffController{
    public  AdminStaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);

    }

    public void addPage(){
        String title =view.getInput("Enter page title");
        String content = view.getInput("Enter page content");
        boolean isPrivate = view.getYesNoInput("Should this page be private");
        Collection<Page> availablePages = sharedContext.getPages();
        //Find the exist Title.
        ArrayList <String> titles = new ArrayList<String>();
        for (Page page: availablePages){
            titles.add(page.getTitle());
        }
        boolean exist = titles.contains(title);
        if (exist){
            boolean overwrite = view.getYesNoInput("Page" + title + "already exists Overwrite with new page?");
            if(!overwrite){
                view.displayInfo("Cancelled adding new page");
                return;
            }
        }
        Page newPage = new Page(title,content,isPrivate);
        this.sharedContext.addPage(newPage);
        AuthenticatedUser currentUser = (AuthenticatedUser) this.sharedContext.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();

        int status = emailService.sendEmail(currentUserEmail, SharedContext.ADMIN_STAFF_EMAIL,title, content);
        if (status == STATUS_SUCCESS){
            view.displaySuccess("Added page" + title);
        }
        else {
            view.displayWarning("Added page" + title + "but failed to send email notification");
        }
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
