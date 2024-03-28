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

    public boolean unregisterForFAQUpdates(String email, String topic){
        // TODO this might have exceptions...
        return this.faqTopicsUpdateSubscribers.get(topic).remove(email);
    }

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
    public boolean pageExist(String title){
        for(Page y : this.pages){
            if(title.equals(y.getTitle())){
                return true;
            }
        }
        return false;
    }
    public FAQ getFAQ() {
        return this.faq;
    }
    public Collection<Inquiry> getUnAnsweredInquires(){
        ArrayList<Inquiry> toReturn = new ArrayList<>();
//        System.out.println(this.inquiries.size());
        for(Inquiry inquiry:this.inquiries){
//            System.out.println(inquiry.getSubject()+"  " + inquiry.getAssignedTo());
            if(inquiry.getAssignedTo() == null){
                toReturn.add(inquiry);
            }
        }
        return toReturn;
    }

    public Map<String, Collection<String>> getFaqTopicsUpdateSubscribers() {
        return faqTopicsUpdateSubscribers;
    }


}
