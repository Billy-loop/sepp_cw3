package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FAQSection {
    private String topic;
    private boolean isPrivate;
    private ArrayList<FAQSection> subSections;
    private ArrayList<FAQItem> faqItems;
    private FAQSection parent;
    public FAQSection(){
        this.topic = null;
        this.isPrivate = false;
        this.subSections = new ArrayList<FAQSection>();
        this.faqItems = new ArrayList<FAQItem>();
    }
    public FAQSection(String topic){
        this.topic = topic;
        this.isPrivate = false;
        this.subSections = new ArrayList<FAQSection>();
        this.faqItems = new ArrayList<FAQItem>();
    }
    public void addSubsection(FAQSection subSection){
        subSection.parent = this;
        this.subSections.add(subSection);
    }
    public void addItem(String Question, String Answer){
        this.faqItems.add(new FAQItem(Question, Answer));
    }
    public void addItem(FAQItem faqItem){
        this.faqItems.add(faqItem);
    }

    public ArrayList<FAQItem> getItems(){
        return this.faqItems;
    }

    public String getTopic(){return this.topic;}
    public ArrayList<FAQSection> getSubSections(){
        return this.subSections;
    }

    /**
     * Retrieves the topics of all subsections belonging to this FAQSection.
     *
     * @return an ArrayList containing the topics of all subsections
     */
    public ArrayList<String> getAllSubTopics(){
        ArrayList<String> subSectionsNames = new ArrayList<>();
        for(FAQSection subSection:this.subSections){
            subSectionsNames.add(subSection.getTopic());
        }
        return subSectionsNames;
    }

    /**
     * Retrieves the subsection with the specified topic from the list of subsections belonging to this FAQSection.
     *
     * @param topic the topic of the subsection to retrieve
     * @return the subsection with the specified topic, or null if no such subsection exists
     */
    public FAQSection getSubSectionWithTopic(String topic){
        for(FAQSection subSection:this.subSections){
            if(subSection.getTopic().equals(topic)){
                return subSection;
            }
        }
        return null;
    }
    public FAQSection getParent() {
        return this.parent;
    }

    /**
     * Retrieves the topics of super-sections (sections that contain this section) for the current FAQSection.
     * If this section is at the root, returns null.
     * If this section has no parent or the parent is the root, returns a list containing "root".
     * Otherwise, returns the topics of all super-sections.
     *
     * @return an ArrayList containing the topics of super-sections, or null if at root
     */
    public ArrayList<String> getSuperTopics(){
        if (this.topic == null){ // at root
            return null;
        }else if(this.parent == null || this.parent.getTopic() == null){ // root as parent
            return new ArrayList<>(List.of("root"));
        }else if(this.parent != null){
            ArrayList<FAQSection> superSections = this.parent.getParent().getSubSections();

            ArrayList<String> toReturn = new ArrayList<>();
            for (FAQSection superSection : superSections) {
                toReturn.add(superSection.getTopic());
            }
            return toReturn;
        }
        return null;
    }

    /**
     * Finds FAQ items with the specified target topic within this FAQSection or its sub-sections.
     *
     * @param target the topic to search for
     * @return an ArrayList containing the FAQ items with the specified topic, or null if not found
     */
    public ArrayList<FAQItem> findTopicItems(String target){
        if(this.topic!=null && this.topic.equals(target)){
            return this.faqItems;
        }
        ArrayList<FAQItem> toReturn = null;
        for(FAQSection subsection : this.subSections){
            toReturn = subsection.findTopicItems(target);
            if(toReturn != null){
                return toReturn;
            }
        }
        return toReturn;
    }

    /**
     * Retrieves and formats FAQ items with the specified target topic within this FAQSection or its subsections.
     *
     * @param target the topic to search for
     * @return a formatted string containing the FAQ items with the specified topic, or an empty string if not found
     */
    public String printTopicItems(String target) {
        Collection<FAQItem> toPrints = this.findTopicItems(target);
        String toReturn = "";
        for(FAQItem toPrint : toPrints){
            toReturn += "Q:" + toPrint.getQuestion() + ";A:" + toPrint.getAnswer()+";\n";
        }
        return toReturn;
    }
    public void setParent(FAQSection parent){
        this.parent = parent;
    }
}
