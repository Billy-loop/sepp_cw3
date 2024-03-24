package model;

public class FAQSection {
    private String topic;
    private boolean isPrivate;

    public FAQSection(String topic){
        this.topic = topic;
    }
    public void addSubsection(FAQSection section){

    }
    public void additem(String topic, String content){

    }

    public FAQSection getParent(){
        return new FAQSection(this.topic);
    }

    public String getTopic(){
        return this.topic;
    }
}
