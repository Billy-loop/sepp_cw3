package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static external.EmailService.STATUS_SUCCESS;

public class AdminStaffController extends StaffController{
    public  AdminStaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);

    }

    public void addPage(){
        String title =this.view.getInput("Enter page title");
        String contentPath = this.view.getInput("Enter page path");
        String content = "";
        File contentFile = new File(contentPath);
        try {
            Scanner reader = new Scanner(contentFile);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                content += line;
            }
        }catch (FileNotFoundException e){
            view.displayError("File not there");
            return;
        }
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
        FAQSection currentFAQSection = this.sharedContext.getFAQ().getfaqSection();

        while(true){
            this.view.displayFAQSection(currentFAQSection, true);
            this.view.displayInfo("-1 to Return");
            this.view.displayInfo("-2 to add Q-A pair");
            int op = Integer.parseInt(this.view.getInput("Navigate?"));
            if(op == -1){ // return to privous layer / cancel
                if(currentFAQSection.getParent() != null){
                    currentFAQSection = currentFAQSection.getParent();
                    this.view.displayInfo("Parent topic");
                }else {
                    this.view.displayInfo("You are at the top layer, return to menu");
                    return;
                }
            }
            if(op == -2){ // Add Q-A
                addFAQItem(currentFAQSection);
            }
            try{
                currentFAQSection = currentFAQSection.getSubSections().get(op);
                this.view.displayInfo("Sub topic");
            }catch(IndexOutOfBoundsException e){
                this.view.displayInfo("Try again with a valid index");
            }
        }
    }

    public void addFAQItem(FAQSection faqSection){
        String question = this.view.getInput("Question:");
        String answer = this.view.getInput("Answer:");
        FAQItem toAddQA = new FAQItem(question, answer);
        String emailtopic = faqSection.getTopic();

        // ask for topic
        if(faqSection.getTopic() != null){ // not root
            boolean isNewTopic = this.view.getYesNoInput("Added at a new subTopic?");
            if(isNewTopic){
                String topic = this.view.getInput("new Subtopic name:");
                emailtopic = topic;
                FAQSection toAddSec;
                if(!faqSection.getAllSubTopics().contains(topic)){
                    toAddSec = new FAQSection(topic);
                    toAddSec.addItem(toAddQA);
                    faqSection.addSubsection(toAddSec);
                }else{
                    this.view.displayWarning("Topic already exist, inserting to old one.");
                    toAddSec = faqSection.getSubSectionWithTopic(topic);
                    toAddSec.addItem(toAddQA);
                }
            }else{
                faqSection.addItem(toAddQA);
            }
        }else { // root
            String topic = this.view.getInput("new Subtopic name:");
            emailtopic = topic;
            FAQSection toAddSec;
            if(!faqSection.getAllSubTopics().contains(topic)){
                toAddSec = new FAQSection(topic);
                toAddSec.addItem(toAddQA);
                faqSection.addSubsection(toAddSec);
            }else{
                this.view.displayWarning("Topic already exist, inserting to old one.");
                toAddSec = faqSection.getSubSectionWithTopic(topic);
                toAddSec.addItem(toAddQA);
            }
        }

        // send emails to subscribers
        String subject = "FAQ Topic " + emailtopic + " Updated";
        String content = faqSection.printTopicItems(emailtopic);
        this.emailService.sendEmail(((AuthenticatedUser)this.sharedContext.getCurrentUser()).getEmail(), SharedContext.ADMIN_STAFF_EMAIL, subject, content);
        if(this.sharedContext.usersSubscribedToFAQTopic(emailtopic) != null){
            for(String subscriber : this.sharedContext.usersSubscribedToFAQTopic(emailtopic)){
                this.emailService.sendEmail(SharedContext.ADMIN_STAFF_EMAIL, subscriber, subject, content);
            }
        }
    }

    public void viewAllPages(){
        if(this.sharedContext.getPages().isEmpty()){
            this.view.displayInfo("No Pages");
            return;
        }
        for(Page page : this.sharedContext.getPages()){
            this.view.displayInfo("title: "+page.getTitle() +" content:"+page.getContent());
        }
    }

    public void manageInquiries(){

    }

    public void redirectInquiry(Inquiry inquiry){

    }
}
