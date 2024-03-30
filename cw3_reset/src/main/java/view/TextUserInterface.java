package view;

import model.*;

import java.util.Collection;
import java.util.Scanner;

public class TextUserInterface implements View {

    private final Scanner scanner;

    public TextUserInterface(){
        this.scanner = new Scanner(System.in);
    }
    @Override
    public String getInput(String message) {
        System.out.print(message + " ");
        return scanner.nextLine().trim();
    }

    @Override
    public boolean getYesNoInput(String info) {
        System.out.println(info);
        String y = scanner.nextLine().trim();
        return y.equals("yes");
    }

    @Override
    public void displayInfo(String info) {System.out.println(info);}

    @Override
    public void displaySuccess(String success) {System.out.println(success);}

    @Override
    public void displayWarning(String warning) {
        System.out.println("== WARNING ==");
        System.out.println(warning);
    }

    @Override
    public void displayError(String error) {
        System.out.println("== ERROR ==");
        System.out.println(error);
    }

    @Override
    public void displayException(Exception e) {
        System.out.println("EXCEPTION OCCURRED: ");
        System.out.println(e.getMessage());
    }

    @Override
    public void displayDivider() {
        System.out.println("---------------------------------");
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
        this.displayDivider();
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
        this.displayDivider();


    }

    @Override
    public void displayInquiry(Inquiry inquiry){
        this.displayDivider();

        System.out.println("CREATED AT: " + inquiry.getCreatedAt());
        System.out.println("SENDER: " + inquiry.getInquirerEmail());
        System.out.println("RECEIVER: " + inquiry.getAssignedTo());
        System.out.println("SUBJECT: " + inquiry.getSubject());
        System.out.println("CONTENT: " + inquiry.getContent());

        this.displayDivider();
    }

    @Override
    public void displaySearchResults(Collection<PageSearchResult> res) {
        for (PageSearchResult result: res){
            System.out.println(result.getFormattedContent());
        }
    }
}