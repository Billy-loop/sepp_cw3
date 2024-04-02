package model;

import external.AuthenticationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SharedContext {
    public static String ADMIN_STAFF_EMAIL = "jack.tr@hindenburg.ac.uk";
    private Map<String, Collection<String>> faqTopicsUpdateSubscribers;
    private User currentUser;
    private Collection<Page> pages;
    private FAQ faq;
    private Collection<Inquiry> inquiries;

    public SharedContext() {
        this.faqTopicsUpdateSubscribers = new HashMap<String, Collection<String>>();
        this.currentUser = new Guest();
        this.pages = new ArrayList<Page>();
        this.faq = new FAQ();
        this.inquiries = new ArrayList<Inquiry>();
    }

    public void addPage(Page page){
        this.pages.add(page);
    }

    /**
     * Registers an email address for updates on a specific FAQ topic.
     *
     * @param email the email address to register
     * @param topic the FAQ topic for which updates are requested
     * @return true if the registration is successful, false otherwise
     */
    public boolean registerForFAQUpdates(String email, String topic) {
        if(this.faqTopicsUpdateSubscribers.get(topic) == null){
            ArrayList<String> toAdd = new ArrayList<>();
            toAdd.add(email);
            this.faqTopicsUpdateSubscribers.put(topic, toAdd);
        }else {
            this.faqTopicsUpdateSubscribers.get(topic).add(email);
        }
        return true;
    }

    /**
     * Unregisters an email address from receiving updates on a specific FAQ topic.
     *
     * @param email the email address to unregister
     * @param topic the FAQ topic for which updates were previously requested
     * @return true if the email address is successfully removed from the subscribers list, false otherwise
     */
    public boolean unregisterForFAQUpdates(String email, String topic){
        return this.faqTopicsUpdateSubscribers.get(topic).remove(email);
    }

    /**
     * Retrieves a collection of email addresses subscribed to updates on a specific FAQ topic.
     *
     * @param topic the FAQ topic for which subscribers are requested
     * @return a collection of email addresses subscribed to updates on the specified FAQ topic,
     *         or null if there are no subscribers for the topic
     */
    public Collection<String> usersSubscribedToFAQTopic(String topic){
        return this.faqTopicsUpdateSubscribers.get(topic);
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }
    public User getCurrentUser(){
        return this.currentUser;
    }
    public Collection<Page> getPages(){
        return this.pages;
    }

    public FAQ getFAQ() {
        return this.faq;
    }


    public Collection<Inquiry> getInquiries(){
        return this.inquiries;
    }

    public Map<String, Collection<String>> getFaqTopicsUpdateSubscribers() {
        return faqTopicsUpdateSubscribers;
    }


}
