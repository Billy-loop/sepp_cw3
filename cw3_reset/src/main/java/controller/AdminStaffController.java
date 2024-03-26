package controller;

import external.AuthenticationService;
import external.EmailService;
import external.MockEmailService;
import model.*;
import view.View;

import java.util.*;

import static external.EmailService.STATUS_SUCCESS;

public class AdminStaffController extends StaffController{
    public  AdminStaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);

    }

    public void addPage(){
        String title =this.view.getInput("Enter page title");
        String content = this.view.getInput("Enter page content");
        boolean isPrivate = this.view.getYesNoInput("Should this page be private");
        Collection<Page> availablePages = this.sharedContext.getPages();
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
        if (status == EmailService.STATUS_SUCCESS){
            view.displaySuccess("Added page" + title);
        }
        else {
            view.displayWarning("Added page" + title + "but failed to send email notification");
        }
    }

    public void manageFAQ(){
        FAQ faq = this.sharedContext.getFAQ();
        while(true){
            this.view.displayFAQ(faq, false);
            this.view.displayInfo("[-1] return MENU");
            this.view.displayInfo("[-2] Add new topic");
            int op = Integer.parseInt(this.view.getInput("Choose a topic or action"));
            while(op== -1 || op == -2){
                if (op == -1){
                    return;
                }
                if (op == -2){
                    FAQSection currentSection = new FAQSection(this.view.getInput("Create a new topic"));
                    faq.getFaqsection().add(currentSection);
                }
                this.view.displayFAQ(faq,false);
                this.view.displayInfo("[-1] return MENU");
                this.view.displayInfo("[-2] Add new topic");
                op = Integer.parseInt(this.view.getInput("Choose a topic or action"));
            }

            while(true){
//                op = Integer.parseInt(this.view.getInput("Please choose a topic"));
                if (op>=0 && op<= faq.getFaqsection().size()){
                    break;
                }else{
                    this.view.displayError("invalid option:" + op);
                }
            }
            FAQSection currnetSection = faq.getFaqsection().get(op);
            this.view.displayFAQSection(currnetSection,false);
            this.view.displayInfo("[-1] to Return");
            this.view.displayInfo("[-2] to add Q-A pair");
            op = Integer.parseInt(this.view.getInput("Choose a topic or action"));
            if(op == -1){ // return to previous layer / cancel
                if(currnetSection.getParent() != null){
                    currnetSection = currnetSection.getParent();
                    this.view.displayInfo("Parent topic");
                }else {
                    this.view.displayInfo("You are at the top layer, return to FAQ");
                    return;
                }
            }
            if (op == -2){
                String question = this.view.getInput("Question:");
                String answer = this.view.getInput("Answer:");
//                FAQItem addQA = new FAQItem(question, answer);
                String emailtopic = currnetSection.getTopic();
                boolean isNewTopic = this.view.getYesNoInput("Added at a new subTopic?");
                if(isNewTopic){
                    String topic = this.view.getInput("new Subtopic name:");
                    emailtopic = topic;
                    FAQSection toAddSec = new FAQSection(topic);
                    toAddSec.addItem(question,answer);
                    currnetSection.addSubsection(toAddSec);
                }else{
                    currnetSection.addItem(question,answer);
                }
                // send emails
                String subject = "FAQ Topic " + emailtopic + " Updated";
                String content = currnetSection.printTopicItems(emailtopic);
                this.emailService.sendEmail(((AuthenticatedUser)this.sharedContext.getCurrentUser()).getEmail(), SharedContext.ADMIN_STAFF_EMAIL, subject, content);
                if(this.sharedContext.usersSubscribedToFAQTopic(emailtopic) != null){
                    for(String subscriber : this.sharedContext.usersSubscribedToFAQTopic(emailtopic)){
                        this.emailService.sendEmail(SharedContext.ADMIN_STAFF_EMAIL, subscriber, subject, content);
                    }
                }
                try{
                    currnetSection = currnetSection.getSubsections().get(op);
                    this.view.displayInfo("Sub topic");
                }catch(IndexOutOfBoundsException e){
                    this.view.displayInfo("Try again with a valid index");
                }
            }
        }
    }

    public void addFAQItem(FAQSection faqSection){
        String question;
        String answer;
        question = this.view.getInput("Please enter your question:");
        answer = this.view.getInput("Please add your answer:");
        faqSection.addItem(question,answer);
        this.view.displaySuccess("Successfully add faqItem");
    }

    public void viewAllPages(){


    }

    public void manageInquiries(){

    }

    public void redirectInquiry(Inquiry inquiry){
        String reciver = "";
        inquiry.setAssignedTo(reciver);

        String subject = "Redirected Inquiry",
                content = "Inquiry from " + inquiry.getInquirerEmail() + " has been redirected to you.";
        emailService.sendEmail(SharedContext.ADMIN_STAFF_EMAIL, reciver, subject, content);
    }
}
