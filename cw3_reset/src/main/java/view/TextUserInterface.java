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
//        Scanner scanner = new Scanner(System.in);
        System.out.println(info);
        String y = scanner.nextLine();
        String processedInput = y.toLowerCase().trim();
        return processedInput.equals("y") || processedInput.equals("yes") || processedInput.equals("Yes") || processedInput.equals("Y");
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
        this.displayInfo("FAQ");
        if(faq != null){
            displayFAQSection(faq.getfaqSection(), false);
        }
    }

    @Override
    public void displayFAQSection(FAQSection faqSection, Boolean includeQuestions) {
        System.out.println("Topic: " + (faqSection.getTopic()==null?"root":faqSection.getTopic()));
//        System.out.println(faqSection.getSuperTopics());
        if(faqSection.getTopic()!=null){
            System.out.print("SuperTopics ");
            for(String superTopic : faqSection.getSuperTopics()){
                System.out.print(superTopic + " ");
            }
            System.out.println();
        }
        int index = 0;
        for(FAQSection subSection : faqSection.getSubSections()){
            System.out.println("-Subtopic [" + index + "]:" + subSection.getTopic());
            index ++;
        }
        if(includeQuestions){
            for(FAQItem faqItem : faqSection.getItems()){
                System.out.println("--Q: " + faqItem.getQuestion() + "-A:" + faqItem.getAnswer());
            }
        }
    }

    @Override
    public void displayInquiry(Inquiry inquiry){

    }

    @Override
    public void displaySearchResults(Collection<PageSearchResult> res) {
        System.out.println("Search Results:");
        for (PageSearchResult result: res){
            System.out.println(result.getFormattedContent());
        }
    }

    @Override
    public void displayFailure(String fail){System.out.println(fail);}
}
