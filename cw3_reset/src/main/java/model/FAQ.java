package model;

import java.util.Map;


public class FAQ {
    /**
     * @param pair Question-Answer pair
     * */
    private FAQSection faqSection;
    public FAQ(){
        this.faqSection = new FAQSection();
    }

    public FAQSection getfaqSection(){
        return this.faqSection;
    }
}
