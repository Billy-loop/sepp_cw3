package view;

import java.util.Collection;

public interface View {

    String getInput();


    boolean getYesNoInput();



    void displayInfo();

    void displaySuccess();

    void displayWarning();

    void displayError();

    void displayException();

    void displayDivider();

    void displayFAQ();

    void displayFAQSection();

    void displayInquiry();

    void displaySearchResults();


}
