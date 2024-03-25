package view;

import java.util.Scanner;

public class TextUserInterface implements View {
    private Scanner input = new Scanner(Scanner.in);
    @Override
    public String getInput(String msg) {
        System.out.print(msg+" ");
        return input.nextLine().trim();
    }

    @Override
    public boolean getYesNoInput() {
        return true;
    }

    @Override
    public void displayInfo() {

    }

    @Override
    public void displaySuccess() {

    }

    @Override
    public void displayWarning() {

    }

    @Override
    public void displayError() {

    }

    @Override
    public void displayException() {

    }

    @Override
    public void displayDivider() {

    }

    @Override
    public void displayFAQ() {

    }

    @Override
    public void displayFAQSection() {

    }

    @Override
    public void displayInquiry() {

    }

    @Override
    public void displaySearchResults() {

    }


}
