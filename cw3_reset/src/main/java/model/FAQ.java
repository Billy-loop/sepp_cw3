package model;


public class FAQ {
    /**
     * @param pair Question-Answer pair
     * */
    private FAQSection faqSection;
    public FAQ(){
        this.faqSection = new FAQSection();
    }

    public FAQSection addFAQsection(FAQSection section){return null;}

    public FAQSection getfaqSection(){
        return this.faqSection;
    }
}
