package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.Inquiry;
import model.SharedContext;
import view.View;

import java.util.ArrayList;

public class TeachingStaffController extends StaffController{
    public TeachingStaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);
    }

    public void manageReceivedInquiries(){
        int index = 0;
        int assignedIndex = 0;
        for(Inquiry inquiry: this.sharedContext.getInquiries()){
            if(inquiry.getAssignedTo() != null && inquiry.getAssignedTo().equals(((AuthenticatedUser)this.sharedContext.getCurrentUser()).getEmail())){
//                this.view.displayInfo(Integer.toString(index));
                System.out.print("["+index+"]");
                this.view.displayInquiry(inquiry);
                assignedIndex ++;
            }
            index ++;
        }
        this.view.displayInfo("You have "+assignedIndex+" inquires to be answered.");
        if(assignedIndex == 0){
            return;
        }
        int op = Integer.parseInt(this.view.getInput("Answer to which question(index)?"));
        if(op >= index){
            this.view.displayInfo("Bad Number");
            return;
        }
        Inquiry toAnswer = ((ArrayList<Inquiry>)this.sharedContext.getInquiries()).get(op);
        this.respondToInquiry(toAnswer);
    }
}
