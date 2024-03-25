package view;

import model.FAQ;
import model.FAQSection;
import model.Inquiry;
import model.PageSearchResult;

import java.util.Collection;

public interface View {

    String getInput(String input);


    boolean getYesNoInput(String input);



    void displayInfo(String info);

    void displaySuccess(String success);

    void displayWarning(String warning);

    void displayError(String error);

    void displayException(Exception e);

    void displayDivider();

    void displayFAQ(FAQ faq, Boolean boo);

    void displayFAQSection(FAQSection section, Boolean boo);

    void displayInquiry(Inquiry inquiry);

    void displaySearchResults(Collection<PageSearchResult>res);


}