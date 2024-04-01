package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class InquirerController extends Controller{
    public InquirerController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);

    }

    public void consultFAQ(){
        FAQSection currentSection = null;
        User currentUser = this.sharedContext.getCurrentUser();
        int optionNo = 0;

        while(!(currentSection==null && optionNo == -1)){
            //Display option
            if (currentSection == null) {
                FAQ faq = this.sharedContext.getFAQ();
                currentSection = faq.getfaqSection();
                this.view.displayFAQ(faq, currentUser instanceof Guest);
                this.view.displayInfo("[-1] Return to main menu");
            } else {
                this.view.displayFAQSection(currentSection, currentUser instanceof Guest);
                FAQSection parent = currentSection.getParent();
                if (parent == null){
                    this.view.displayInfo("[-1] Return to main menu");
                }
                else if(parent.getTopic() == null){
                    this.view.displayInfo("[-1] Return to All Topics");
                }else{
                    this.view.displayInfo("[-1] Return to " + parent.getTopic());
                }
//                this.view.displayInfo(
//                        (parent == null)?"[-1] Return to All Topics":("[-1] Return to " + parent.getTopic())
//                );
                manageFAQSubscriptionOptions(currentSection, currentUser);
            }
            //handle option
            optionNo = getUserOption();
            if (optionNo >= -1) {
                currentSection = handleFAQNavigation(optionNo, currentSection);
            } else if (optionNo == -2 || optionNo == -3){
                handleSubscriptionUpdates(optionNo, currentSection, currentUser);
            } else{
                this.view.displayInfo("Bad Number");
                currentSection = null;
            }
        }
    }

    private void manageFAQSubscriptionOptions(FAQSection currentSection, User currentUser){
        if(currentUser instanceof AuthenticatedUser){
            String topic = currentSection.getTopic();
            Collection<String> subscribers = this.sharedContext.usersSubscribedToFAQTopic(topic);
            if(subscribers != null && subscribers.contains(((AuthenticatedUser) currentUser).getEmail())){
                this.view.displayInfo("[-2] to stop receiving updates for this topic");
            }else{
                this.view.displayInfo("[-2] to request updates for this topic");
            }
        }else{ // guest
            this.view.displayInfo("[-2] to request updates for this topic");
            this.view.displayInfo("[-3] to stop receiving updates for this topic");
        }
    }

    private int getUserOption(){
        try{
            return Integer.parseInt(this.view.getInput("Please choose an option"));
        }catch(NumberFormatException e){
            this.view.displayException(e);
            return Integer.MAX_VALUE;
        }
    }

    private FAQSection handleFAQNavigation(int optionNo, FAQSection currentSection) {
        if (optionNo == -1) {
            if(currentSection.getTopic() != null){
                return (currentSection.getParent() == null) ? null : currentSection.getParent();
            }
            return null;
        } else {
            // Navigate to a subsection based on the option number.
            // Assumes option numbers start from 0 and correspond directly to the index of the subsection in the list.
            ArrayList<FAQSection> subSections = currentSection.getSubSections();
            if (optionNo >= 0 && optionNo < subSections.size()) {
                return subSections.get(optionNo);
            } else {
                // If the option number is out of range, return the current section, indicating no valid navigation occurred.
                this.view.displayInfo("Invalid option. Please try again.");
                return currentSection;
            }
        }
    }

    private void handleSubscriptionUpdates(int optionNo, FAQSection currentSection, User currentUser){
        if(currentUser instanceof AuthenticatedUser){
            String topic = currentSection != null ? currentSection.getTopic() : null;
            // if already subscribed
            if(optionNo == -3) {
                this.view.displayInfo("Bad Number");
                return;
            }
            if(topic != null && sharedContext.getFaqTopicsUpdateSubscribers().get(topic) != null && (sharedContext.getFaqTopicsUpdateSubscribers().get(topic).contains(((AuthenticatedUser) currentUser).getEmail()))){
                stopFAQUpdates(((AuthenticatedUser) currentUser).getEmail(), topic);
            }else {
                requestFAQUpdates(((AuthenticatedUser) currentUser).getEmail(), topic);
            }
        }else{ // currentUser Guest
            String topic = currentSection != null ? currentSection.getTopic() : null;
            if(optionNo == -2){
                requestFAQUpdates(null, topic);
            }else if(optionNo == -3){
                stopFAQUpdates(null, topic);
            }
        }
    }
    private void requestFAQUpdates(String email, String topic){
        if(email == null){
            email = this.view.getInput("Please enter your email address");
        }
        boolean success = this.sharedContext.registerForFAQUpdates(email, topic);
        if(success){
            this.view.displaySuccess("Successfully registered " + email + " for updates on " + topic);
        }else{
            this.view.displayFailure("Failed to register " + email + " for updates on " + topic + " Perhaps this email was already registered?");
        }
    }

    private void stopFAQUpdates(String email , String topic){
        if(email == null){
            email = this.view.getInput("Please enter your email address");
        }
        boolean success = this.sharedContext.unregisterForFAQUpdates(email, topic);
        if(success){
            this.view.displaySuccess("Successfully unregistered " + email + " for updates on " + topic + "");
        }else{
            this.view.displayFailure("Failed to unregister " + email + " for updates on " + topic + " Perhaps this email was not registered?");
        }
    }


    public void searchPages(){
        String query = this.view.getInput("Enter your search query");
        Collection<Page> availablePages = this.sharedContext.getPages();
        User currentUser = sharedContext.getCurrentUser();
        //Guest can not see the private page, filter the private page.
        if(currentUser instanceof Guest){
            ArrayList<Page> publicPage = new ArrayList<Page>();
            for (Page page : availablePages){
                if(!page.getPrivate()){
                    publicPage.add(page);
                }
            }
            availablePages = publicPage;
        }
        try{
            PageSearch pageSearch = new PageSearch(availablePages);
            ArrayList<PageSearchResult> res = (ArrayList<PageSearchResult>) pageSearch.search(query);
            if(res.size()>4){
                ArrayList<PageSearchResult> limitRes = new ArrayList<PageSearchResult>();
                int limit = 4;
                for(int i =0 ; i< res.size() && i < limit; i++){
                    limitRes.add(res.get(i));
                }
                res = limitRes;
            }
            this.view.displaySearchResults(res);
        }catch (IOException | ParseException e){
            this.view.displayException(e);
        }
    }

    public void contactStaff(){
        String email, subject, content;
        if(this.sharedContext.getCurrentUser() instanceof Guest){//Guest
            email = this.view.getInput("Your Email:");
        }else{
            email = ((AuthenticatedUser)this.sharedContext.getCurrentUser()).getEmail();
        }
        subject = this.view.getInput("Subject:");
        content = this.view.getInput("Content:");
        this.sharedContext.getInquiries().add(new Inquiry(subject, content, email));// Add into inquiries.
        this.emailService.sendEmail(email, SharedContext.ADMIN_STAFF_EMAIL, subject, content);
    }
}
