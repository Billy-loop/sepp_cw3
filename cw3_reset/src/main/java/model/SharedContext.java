package model;

import java.util.Collection;
import java.util.Map;

public class SharedContext {
    public static String ADMIN_STAFF_EMAIL;
    private Map<String, Collection<String>> faqTopicsUpdateSubscribers;
    private User currentUser;
    private Collection<Page> pages;
    private FAQ faq;

    public SharedContext() {
    }

    public void addPage(Page page){
        this.pages.add(page);
    }

    public boolean registerForFAQUpdates(String email, String topic){
        this.faqTopicsUpdateSubscribers.get(topic).add(email);
        return true;
    }

    public boolean unregisterForFAQUpdates(String email, String topic){
        this.faqTopicsUpdateSubscribers.get(topic).remove(email);
        return true;
    }

    public Collection<String> usersSubscribedToFAQTopic(String topic){
        return this.faqTopicsUpdateSubscribers.get(topic);
    }

    public void setCurrentUser(User currnetUser){
        this.currentUser = currnetUser;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

    public Collection<Page> getPages(){
        return pages;
    }

    public FAQ getFAQ(){return faq;}
}
