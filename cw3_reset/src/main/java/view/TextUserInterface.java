package view;

import model.*;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

public class TextUserInterface implements View {

    private Scanner scanner;

    public TextUserInterface(InputStream input){
        this.scanner = new Scanner(input);
    }
    @Override
    public String getInput(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    @Override
    public boolean getYesNoInput(String info) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(info);
        String y = scanner.nextLine();
        return y.equals("yes");
    }

    @Override
    public void displayInfo(String info) {System.out.println(info);}

    @Override
    public void displaySuccess(String success) {System.out.println(success);}

    @Override
    public void displayWarning(String warning) {System.out.println(warning);}

    @Override
    public void displayError(String error) {System.out.println(error);}

    @Override
    public void displayException(Exception e) {e.printStackTrace(System.out);}

    @Override
    public void displayDivider() {
        System.out.println("---------------------------");
    }

    @Override
    public void displayFAQ(FAQ faq, Boolean isGuest) {
        this.displayDivider();
        System.out.println("FAQ:");
        int index = 0;
        for(FAQSection section : faq.getFaqsection()){
            if (isGuest && section.getPrivate()){
                continue;
            }else{
                System.out.println("-Subtopic " + index + ":" + section.getTopic());
                index ++;
            }
        }
        this.displayDivider();
    }

    @Override
    public void displayFAQSection(FAQSection section, Boolean isGuest) {
        System.out.println("Topic: " + section.getTopic());
        int index = 0;
        for(FAQSection subSection : section.getSubsections()){
            boolean isPrivate = subSection.getPrivate();
            if(isGuest && isPrivate){
                continue;
            }else{
                System.out.println("-Subtopic " + index + ":" + subSection.getTopic());
                index ++;
            }
        }
        for(FAQItem faqItem : section.getItems()){
            System.out.println("--Q: " + faqItem.getQuestion() + "-A:" + faqItem.getAnswer());
        }


    }

    @Override
    public void displayInquiry(Inquiry inquiry){

    }

    @Override
    public void displaySearchResults(Collection<PageSearchResult> res) {
        for (PageSearchResult result: res){
            System.out.println(result.getFormattedContent());
        }
    }
}
