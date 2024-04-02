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

    /**
     * Allows the user to consult FAQ.
     * Displays the FAQ sections and options for navigation and subscription.
     * Handles user input for navigation within the FAQ sections and subscription options.
     *
     * @throws NumberFormatException if an invalid option number is entered
     */
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

                manageFAQSubscriptionOptions(currentSection, currentUser);
            }
            //handle option

            try{
                optionNo = Integer.parseInt(this.view.getInput("Please choose an option"));
            }catch (NumberFormatException e){
                this.view.displayException(e);
                optionNo = Integer.MAX_VALUE;
            }

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

    /**
     * Manages the subscription options for the current FAQ section based on the current user's type.
     * If the current user is authenticated, displays an option to either stop or request updates for the FAQ topic.
     * If the current user is a guest, displays options to request or stop receiving updates for the FAQ topic.
     *
     * @param currentSection the current FAQ section being viewed
     * @param currentUser the current user (either authenticated or guest)
     */
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

    /**
     * Handles navigation within the FAQ section based on the provided option number and current FAQ section.
     * If the option number is -1, it returns the parent section if available.
     * Otherwise, it navigates to the subsection corresponding to the option number.
     *
     * @param optionNo the option number representing the user's choice
     * @param currentSection the current FAQ section the user is navigating within
     * @return the FAQ section to navigate to, or null if no valid navigation occurred
     */
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


    /**
     * Handles subscription updates based on the provided option number, current FAQ section, and current user.
     * If the current user is authenticated, it either subscribes or unsubscribes the user from updates on the current FAQ section topic.
     * If the current user is a guest, it subscribes or unsubscribes the user based on the provided option.
     *
     * @param optionNo the option number representing the user's choice (-2 for subscribe, -3 for unsubscribe)
     * @param currentSection the current FAQ section the user is viewing
     * @param currentUser the current user (either authenticated or guest)
     */
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

    /**
     * Requests to receive updates for a specific FAQ topic for a given email address.
     * If the email address is null, prompts the user to input their email address.
     * Attempts to register the email address for updates on the specified FAQ topic.
     * Displays a success message if registration is successful, otherwise displays an error message.
     *
     * @param email the email address to register for updates (if Guest, prompts the user to input)
     * @param topic the FAQ topic to request updates for
     */
    private void requestFAQUpdates(String email, String topic){
        if(email == null){
            email = this.view.getInput("Please enter your email address");
        }
        boolean success = this.sharedContext.registerForFAQUpdates(email, topic);
        if(success){
            this.view.displaySuccess("Successfully registered " + email + " for updates on " + topic);
        }else{
            this.view.displayError("Failed to register " + email + " for updates on " + topic + " Perhaps this email was already registered?");
        }
    }

    /**
     * Stops receiving updates for a specific FAQ topic for a given email address.
     * If the email address is null, prompts the user to input their email address.
     * Attempts to unregister the email address for updates on the specified FAQ topic.
     * Displays a success message if unregistration is successful, otherwise displays an error message.
     *
     * @param email the email address to unregister for updates (if null, prompts the user to input)
     * @param topic the FAQ topic to stop receiving updates for
     */
    private void stopFAQUpdates(String email , String topic){
        if(email == null){
            email = this.view.getInput("Please enter your email address");
        }
        boolean success = this.sharedContext.unregisterForFAQUpdates(email, topic);
        if(success){
            this.view.displaySuccess("Successfully unregistered " + email + " for updates on " + topic + "");
        }else{
            this.view.displayError("Failed to unregister " + email + " for updates on " + topic + " Perhaps this email was not registered?");
        }
    }


    /**
     * Allows the user to search for pages based on a query.
     * Retrieves the search query from the user and searches for matching pages.
     * Filters out private pages if the current user is a guest.
     * Displays up to 4 search results to the user.
     *
     * @throws IOException if an I/O error occurs while searching for pages
     * @throws ParseException if an error occurs while parsing search results
     */
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
            // Perform the search and limit the results to a maximum of 4
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

    /**
     * Allows the user to contact staff members.
     * Retrieves the user's email address if they are a guest; otherwise, uses the email address of the authenticated user.
     * Prompts the user to input the subject and content of the inquiry.
     * Adds the inquiry to the list of inquiries in the shared context.
     * Sends an email notification to the staff members.
     */
    public void contactStaff(){
        String email, subject, content;
        // If the current user is a guest, prompt for their email address;
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
