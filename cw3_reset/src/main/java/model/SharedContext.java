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

    public boolean registerForFAQUpdates(String s1, String s2){
        return false;
    }

    public boolean unregisterForFAQUpdates(String s1, String s2){
        return false;
    }

    public Collection<String> usersSubscribedToFAQTopic(String s1){
        return null;
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
