package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.Guest;
import model.SharedContext;
import view.TextUserInterface;
import view.View;

import java.util.EnumSet;

public class MenuController extends Controller{
    public MenuController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext,view,authenticationService,emailService);
    }

    public void mainMenu(){
        this.view.displayInfo("Welcome");
        if(this.sharedContext.getCurrentUser() instanceof Guest){
            if(handleGuestMainMenu()){ // login success
                switch( ((AuthenticatedUser)this.sharedContext.getCurrentUser()).getRole() ){
                    case "TeachingStaff":
                        handleTeachingStaffMainMenu();
                        break;
                    case "AdminStaff":
                        handleAdminStaffMainMenu();
                        break;
                    case "Student":
                        handleStudentMainMenu();
                        break;
                    default:
                        mainMenu();
                }
            }
        }else{
            System.out.println( "Role:"+  ((AuthenticatedUser)this.sharedContext.getCurrentUser()).getRole());
            switch( ((AuthenticatedUser)this.sharedContext.getCurrentUser()).getRole() ){
                case "TeachingStaff":
                    handleTeachingStaffMainMenu();
                    break;
                case "AdminStaff":
                    handleAdminStaffMainMenu();
                    break;
                case "Student":
                    handleStudentMainMenu();
                    break;
                default:
                    mainMenu();
            }
        }
        mainMenu();
    }

    private boolean handleGuestMainMenu(){
        GuestController guest = new GuestController(this.sharedContext, this.view, this.authenticationService, this.emailService);
        InquirerController inquirer = new InquirerController(this.sharedContext, this.view, this.authenticationService, this.emailService);
        this.view.displayInfo("Guest");
        int option = guest.selectFromMenu(EnumSet.allOf(GuestMainMenuOption.class), "[-1] Return to welcome page");
        if (option == GuestMainMenuOption.LOGIN.value){
            guest.login();
            return !(guest.sharedContext.getCurrentUser() instanceof Guest);
        }
        else if (option == GuestMainMenuOption.CONSULT_FAQ.value) {
            inquirer.consultFAQ();
        } else if (option == GuestMainMenuOption.SEARCH_PAGES.value) {
            inquirer.searchPages();
        } else if (option == GuestMainMenuOption.CONTACT_STAFF.value) {
            inquirer.contactStaff();
        }else{
            this.view.displayError("Bad number");
        }
        return false;
    }

    private boolean handleStudentMainMenu() {
        InquirerController inquirer = new InquirerController(this.sharedContext, this.view, this.authenticationService, this.emailService);
        AuthenticatedUserController student = new AuthenticatedUserController(this.sharedContext, this.view, this.authenticationService, this.emailService);
        this.view.displayInfo("Student");
        int option = student.selectFromMenu(EnumSet.allOf(StudentMainMenuOption.class), "[-1] Return to welcome page");
        if(option == StudentMainMenuOption.LOGOUT.value){
            student.logout();
            if(this.sharedContext.getCurrentUser() instanceof Guest){
                this.view.displaySuccess("Successfully Logout");
                return true;
            }
        }
        else if(option == StudentMainMenuOption.CONSULT_FAQ.value){
            inquirer.consultFAQ();
        }
        else if(option == StudentMainMenuOption.CONTACT_STAFF.value){
            inquirer.contactStaff();
        }
        else if(option == StudentMainMenuOption.SEARCH_PAGES.value){
            inquirer.searchPages();
        }else{
            this.view.displayError("Bad number");
            //throw new IllegalArgumentException();
        }
        return false;
    }
    private boolean handleTeachingStaffMainMenu(){
        TeachingStaffController teacher = new TeachingStaffController(this.sharedContext,this.view,this.authenticationService,this.emailService);
        AuthenticatedUserController teacherUser = new AuthenticatedUserController(this.sharedContext,this.view,this.authenticationService,this.emailService);
        this.view.displayInfo("TeachingStaff");
        int option = teacher.selectFromMenu(EnumSet.allOf(TeachingStaffMainMenuOption.class), "[-1] Return to welcome page");
        if(option == TeachingStaffMainMenuOption.LOGOUT.value){
            teacherUser.logout();
            if(this.sharedContext.getCurrentUser() instanceof Guest){
                this.view.displaySuccess("Successfully Logout");
                return true;
            }
        }
        else if(option == TeachingStaffMainMenuOption.MANAGE_RECEIVED_QUERIES.value){
            teacher.manageReceivedInquiries();
        }else if(option == -1){
            return true;
        }
        else{
            this.view.displayError("Bad number");
//           throw new IllegalArgumentException();
        }
        return false;
    }

    private boolean handleAdminStaffMainMenu(){
        AuthenticatedUserController adminStaffUser = new AuthenticatedUserController(this.sharedContext,this.view,this.authenticationService,this.emailService);
        AdminStaffController adminstaff = new AdminStaffController(this.sharedContext,this.view,this.authenticationService,this.emailService);
        this.view.displayInfo("Adminstaff");
        int option = adminstaff.selectFromMenu(EnumSet.allOf(AdminStaffMainMenuOption.class), "[-1] Return to welcome page");

        if(option == AdminStaffMainMenuOption.LOGOUT.value){
            adminStaffUser.logout();
            if(this.sharedContext.getCurrentUser() instanceof Guest){
                this.view.displaySuccess("Successfully Logout");
                return true;
            }
        }
        else if(option == AdminStaffMainMenuOption.ADD_PAGE.value){
            adminstaff.addPage();
        }
        else if(option == AdminStaffMainMenuOption.MANAGE_FAQ.value){
            adminstaff.manageFAQ();
        }
        else if(option == AdminStaffMainMenuOption.MANAGE_QUERIES.value){
            adminstaff.manageInquiries();
        } else if (option == AdminStaffMainMenuOption.SEE_ALL_PAGES.value) {
            adminstaff.viewAllPages();

        } else{
            this.view.displayError("Bad number");
//            throw new IllegalArgumentException();
        }
        return false;
    }
}
