package model;

import java.util.ArrayList;
import java.util.Collection;

public class FAQSection {
    private String topic;
    private boolean isPrivate;
    private ArrayList<FAQSection> subsections;

    private FAQSection parent;

    public FAQSection(String topic){
        this.topic = topic;
    }
    public void addSubsection(FAQSection section){

    }
    public void additem(String topic, String content){

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
}
