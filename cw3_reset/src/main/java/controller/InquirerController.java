package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class InquirerController extends Controller{
    public InquirerController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);

    }

    public void consultFAQ(){
        FAQSection currentSection = null;
        User currentUser =this.sharedContext.getCurrentUser();
        if(currentUser instanceof AuthenticatedUser){
            String userEmail = ((AuthenticatedUser) currentUser).getEmail();
        }
        int optionNo = 0;
        while(currentSection != null || optionNo != -1){
            //Display the FAQ
            if (currentSection == null){
                FAQ faq = this.sharedContext.getFAQ();
                this.view.displayFAQ(faq,currentUser instanceof Guest);
                this.view.displayInfo("[-1] to return to main menu");

            } else{
                this.view.displayFAQSection(currentSection, currentUser instanceof Guest);
                FAQSection parent = currentSection.getParent();
                if(parent==null){
                    this.view.displayInfo("[-1] to return to FAQ");
                }else{
                    String topic = parent.getTopic();
                    this.view.displayInfo("[-1] to return to topic");
                }
                if(currentUser instanceof Guest){
                    this.view.displayInfo("[-2] to request for this topic");
                    this.view.displayInfo("[-3] to stop receiving update for this topic");
                }else{
                    String topic = currentSection.getTopic();
                    Collection <String> subscribes = this.sharedContext.usersSubscribedToFAQTopic(topic);
                    String subscribeEmail =  ((AuthenticatedUser) currentUser).getEmail();
                    if(subscribes.contains(subscribeEmail)){
                        this.view.displayInfo("[-2] to stop receiving updates for this topic");
                    }else{
                        this.view.displayInfo("[-2] to request to updates this topic");
                    }
                }
            }
            String option = this.view.getInput("Please choose an option");
            // Processing option
            try{
                optionNo = Integer.parseInt(option);
                String topic = currentSection.getTopic();

                if((currentSection != null) && (currentUser instanceof Guest) && (optionNo == -2)){
                    this.requestFAQUpdates(null ,topic);
                }

                if((currentSection != null) && (currentUser instanceof Guest) && (optionNo == -3)){
                    this.stopFAQUpdates(null, topic);
                }

                if((currentSection != null) && (optionNo == -2)){
                    Collection <String> subscribes = this.sharedContext.usersSubscribedToFAQTopic(topic);
                    String currentUserEmail =  ((AuthenticatedUser) currentUser).getEmail();
                    if(subscribes.contains(currentUserEmail)){
                        this.stopFAQUpdates(currentUserEmail,topic);
                    }else{
                        this.requestFAQUpdates(currentUserEmail,topic);
                    }
                }

                if(currentSection != null && optionNo == -1){
                    currentSection = currentSection.getParent();
                }

                if(optionNo != -1){
                    ArrayList<FAQSection> sections = new ArrayList<FAQSection>();
                    if(currentSection == null){
                        FAQ faq = this.sharedContext.getFAQ();
                        sections = faq.getFaqsection();
                        //Get all public FAQsections
                        ArrayList<FAQSection> publicSections = new ArrayList<>();
                        for(FAQSection subSection : sections){
                            boolean isPrivate = subSection.getPrivate();
                            if(!isPrivate){
                                publicSections.add(subSection);
                            }
                        }
                        if (currentUser instanceof Guest){
                            sections = publicSections;
                        }
                    }else{
                        sections = currentSection.getSubsections();
                        //Get all public FAQsections
                        ArrayList<FAQSection> publicSections = new ArrayList<>();
                        for(FAQSection subSection : sections){
                            boolean isPrivate = subSection.getPrivate();
                            if(!isPrivate){
                                publicSections.add(subSection);
                            }
                        }
                        if (currentUser instanceof Guest){
                            sections = publicSections;
                        }
                    }

                    int sectionLength = sections.size();
                    if((optionNo<0) && (optionNo >= sectionLength)){
                        this.view.displayError("invalid option:" + option);
                    }
                    else{
                        currentSection = sections.get(optionNo);
                    }
                }
            }catch (NumberFormatException e){
                this.view.displayError("Invalid option" + option);
                this.consultFAQ();
            }
        }
    }

    public void searchPages(){
        String query = view.getInput("Enter your search query");
        Collection<Page> availablePages = this.sharedContext.getPages();
        User currentUser = sharedContext.getCurrentUser();
        if(currentUser instanceof Guest){
            ArrayList<Page> publicPage = new ArrayList<Page>();
            for (Page page : availablePages){
                if(!page.getPrivate()){
                    publicPage.add(page);
                }
            }
            availablePages = publicPage;
        }
        try{
            PageSearch pageSearch = new PageSearch(availablePages);
            ArrayList<PageSearchResult> res = (ArrayList<PageSearchResult>) pageSearch.search(query);
            if(res.size()>4){
                ArrayList<PageSearchResult> limitRes = new ArrayList<PageSearchResult>();
                int limit = 4;
                for(int i =0 ; i< res.size() && i < limit; i++){
                    limitRes.add(res.get(i));
                }
                res = limitRes;
            }
            this.view.displaySearchResults(res);
        }catch (IOException | ParseException e){
            this.view.displayException(e);
        }
    }

    public void contactStaff(){

    }

    private void requestFAQUpdates(String email, String topic){
        if(email == null){
            email = this.view.getInput("Please enter your email address");
        }
        boolean success = this.sharedContext.registerForFAQUpdates(email, topic);
        if (success){
            this.view.displaySuccess("Successfully registered" + email + "for updates on" + topic);
        }else{
            this.view.displayInfo("Failed to register " + email + "for updates on" + topic + ",perhaps this email was already registered?");
        }


    }

    private void stopFAQUpdates(String email , String topic){
        if(email == null){
            email = this.view.getInput("Please enter your email address");
        }
        boolean success = this.sharedContext.unregisterForFAQUpdates(email, topic);

    }
}
