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
        int optNo = 0;
        while(currentSection != null && optNo != -1){
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
                }

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

    }

    private void stopFAQUpdates(String a){

    }
}
