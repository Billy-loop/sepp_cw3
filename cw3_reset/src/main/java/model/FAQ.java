package model;

import java.util.ArrayList;

public class FAQ {
    private ArrayList<FAQSection> faqsections;

    public FAQ(){
        this.faqsections = new ArrayList<>();
    }
    public ArrayList<FAQSection> getFaqsection(){
        return this.faqsections;
    }

    public void addSubsection(FAQSection section){
        faqsections.add(section);
    }
}
