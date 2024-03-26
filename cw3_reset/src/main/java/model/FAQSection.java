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
        this.items = new ArrayList<>();
    }
    public void addSubsection(FAQSection section){
        section.parent = this;
        this.subsections.add(section);
    }
    public void addItem(String question, String answer){
        FAQItem newItem = new FAQItem(question,answer);
        this.items.add(newItem);
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
    public ArrayList<FAQItem> findTopicItems(String target){
        if(this.topic!=null && this.topic.equals(target)){
            return this.items;
        }
        ArrayList<FAQItem> toReturn = null;
        for(FAQSection subsection : this.subsections){
            toReturn = subsection.findTopicItems(target);
            if(toReturn != null){
                return toReturn;
            }
        }
        return toReturn;
    }

    public String printTopicItems(String target) {
        Collection<FAQItem> toPrints = this.findTopicItems(target);
        String toReturn = "";
        for(FAQItem toPrint : toPrints){
            toReturn += "Q:" + toPrint.getQuestion() + ";A:" + toPrint.getAnswer()+";\n";
        }
        return toReturn;
    }
}