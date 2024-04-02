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

    /**
     * Adds a new page to the system.
     * This method prompts the user to input the title and path of the content file for the new page.
     * It reads the content from the specified file path and creates a new page with the given title, content, and privacy status.
     * If the page already exists, it prompts the user whether to overwrite it with the new page.
     * After adding the new page, it sends an email notification to the admin staff.
     *
     * @throws IllegalArgumentException if the content file specified by the user is not found.
     */
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
            availablePages.removeIf(y -> title.equals(y.getTitle()));
        }
        //add new page
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

    /**
     * Manages FAQ section by allowing users to navigate through the FAQ structure,
     * view FAQ items, and add new FAQ items.
     * This method displays the current FAQ section, along with its subsections and FAQ items.
     * It prompts the user to choose an option:
     *   - Choose a subsection to navigate deeper into the FAQ structure.
     *   - Choose -1 to return to the previous layer.
     *   - Choose -2 to add a new Question-Answer (Q-A) pair to the current FAQ section.
     *
     * @throws NumberFormatException if the input provided by the user is not a valid integer.
     */
    public void manageFAQ(){
        FAQSection currentFAQSection = this.sharedContext.getFAQ().getfaqSection();

        while(true){
            this.view.displayFAQSection(currentFAQSection, true);
            this.view.displayInfo("[-1] to Return");
            this.view.displayInfo("[-2] to add Q-A pair");
//            int op = Integer.parseInt(this.view.getInput("Navigate?"));
            int op = Integer.parseInt(this.view.getInput("Choose an option?"));
            if(op == -1){ // return
                if(currentFAQSection.getParent() != null){
                    currentFAQSection = currentFAQSection.getParent();
                    this.view.displayInfo("Parent topic");
                }else {
                    this.view.displayInfo("You are at the top layer, return to menu");
                    return;
                }
            } else if (op == -2) {// Add Q-A
                addFAQItem(currentFAQSection);
            }else{
                try{
                    currentFAQSection = currentFAQSection.getSubSections().get(op);
//                    this.view.displayInfo("Sub topic");
                }catch(IndexOutOfBoundsException e){
                    this.view.displayInfo("Try again with a valid index");
                }
            }
        }
    }

    /**
     * Adds a new FAQ item to the specified FAQ section.
     * Prompts the user to input a question and an answer for the new FAQ item.
     * If the FAQ section has a topic, it asks the user if they want to add the item to a new subtopic.
     * If yes, it prompts the user for a new subtopic name and adds the item under that subtopic.
     * If no, it adds the item directly to the FAQ section.
     * If the FAQ section is the root, it prompts the user for a new subtopic name and adds the item under that subtopic.
     *
     * @param faqSection the FAQ section to which the new FAQ item will be added.
     */
    public void addFAQItem(FAQSection faqSection){
        String question = this.view.getInput("Question:");
        String answer = this.view.getInput("Answer:");
        FAQItem toAddQA = new FAQItem(question, answer);
        String emailtopic = faqSection.getTopic();

        // ask for topic
        if(faqSection.getTopic() != null){ // not root
            boolean isNewTopic = this.view.getYesNoInput("Added at a new subTopic? Please enter yes or no");
            if(isNewTopic){
                String topic = this.view.getInput("new Subtopic name:");
                emailtopic = topic;
                FAQSection toAddSec;
                if(!faqSection.getAllSubTopics().contains(topic)){//Do not exist
                    toAddSec = new FAQSection(topic);
                    toAddSec.addItem(toAddQA);
                    faqSection.addSubsection(toAddSec);
                }else{//Exist
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
            if(!faqSection.getAllSubTopics().contains(topic)){//Topic does not exist
                toAddSec = new FAQSection(topic);
                toAddSec.addItem(toAddQA);
                faqSection.addSubsection(toAddSec);
            }else{//exist
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

    /**
     * Displays information about all the pages stored in the shared context.
     * If there are no pages, it informs the user that there are no pages.
     * Otherwise, it iterates through each page and displays its title and content.
     */
    public void viewAllPages(){
        if(this.sharedContext.getPages().isEmpty()){
            this.view.displayInfo("No Pages");
            return;
        }
        for(Page page : this.sharedContext.getPages()){
            this.view.displayInfo("title: "+page.getTitle() +" content:"+page.getContent());
        }
    }

    /**
     * Manages the inquiries by displaying unanswered inquiries, allowing the user to choose an inquiry to respond to or redirect.
     * Displays a list of unanswered inquiries along with their titles, allowing the user to select one for further action.
     * The user can choose to answer the inquiry themselves, redirect it, or cancel the operation.
     *
     * @throws NumberFormatException if the input provided by the user is not a valid integer.
     */
    public void manageInquiries(){
        this.view.displayInfo("Unanswered Inquires:");
        int index = 0;
        for(String title : this.getInquiryTitles(this.sharedContext.getInquiries())){
            this.view.displayInfo("[" +Integer.toString(index) + "]" + ": "+title);
            index ++;
        }
        this.view.displayInfo("[-1] to Return");
        int op = Integer.parseInt(this.view.getInput("Choose:"));
        if(op != -1){ // return
            try{//Choose inquiry to change
                Inquiry toChange = ((ArrayList<Inquiry>) this.sharedContext.getInquiries()).get(op);
                this.view.displayInfo("Subject:"+toChange.getSubject()+"\nContent:"+toChange.getContent());
                //
                op = Integer.parseInt(this.view.getInput("[0] Answer yourself\n[1] Redirect\n[-1] Cancel"));
                if(op == 0){
                    this.respondToInquiry(toChange);
                } else if (op == 1) {
                    redirectInquiry(toChange);
                } else if (op == -1) {
                    //DO nothing, return
                }else{
                    this.view.displayError("Bad Number");
                }

            }catch(IndexOutOfBoundsException e){
                this.view.displayInfo("Try again with a valid index");
            }
        }
    }

    /**
     * Redirects an inquiry to another user by assigning it to them and sending an email notification.
     *
     * @param inquiry The inquiry to be redirected.
     */
    public void redirectInquiry(Inquiry inquiry){
        String assignedTo;
        assignedTo = this.view.getInput("assignedToWho? enter email:");
        inquiry.setAssignedTo(assignedTo);
        String subject = "New Inquiry:"+inquiry.getSubject();
        String content = inquiry.getContent();
        this.emailService.sendEmail(((AuthenticatedUser)this.sharedContext.getCurrentUser()).getEmail(), assignedTo, subject, content);
    }
}
