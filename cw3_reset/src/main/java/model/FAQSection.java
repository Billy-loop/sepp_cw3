package model;

import java.util.ArrayList;
import java.util.Collection;

public class FAQSection {
    private String topic;
    private boolean isPrivate;
    private ArrayList<FAQSection> subsections;
    private FAQSection parent;
    private ArrayList<FAQItem> items;

    public FAQSection(String topic){
        this.topic = topic;
        this.isPrivate = true;
        this.subsections = new ArrayList<>();
        this.parent = null;
        this.items = new ArrayList<>();
    }

    public void additem(String topic, String content){
        FAQItem faq = new FAQItem()
        items.add(faq);
    }

    public boolean getPrivate(){
        return this.isPrivate;
    }

    public FAQSection getParent(){
        return this.parent;
    }

    public String getTopic(){
        return this.topic;
    }

    public ArrayList<FAQSection> getSubsections(){
        return this.subsections;
    }

    public ArrayList<FAQItem> getItems(){
        return this.items;
    }
}
