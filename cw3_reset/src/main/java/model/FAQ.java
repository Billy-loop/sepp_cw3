package model;

import java.util.ArrayList;

public class FAQ {
    private ArrayList<FAQSection> faqsections;

    public FAQ(){
    }
    public ArrayList<FAQSection> getFaqsection(){
        return this.faqsections;
    }
}
