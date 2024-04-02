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

    /**
     * Displays the main menu options based on the role of the current user.
     * If the current user is a guest, displays the welcome message and handles the guest main menu.
     * If the login is successful, navigates to the appropriate main menu based on the user's role (TeachingStaff, AdminStaff, or Student).
     * If the current user is already authenticated, directly navigates to the main menu based on their role.
     */
    public void mainMenu(){
        if(this.sharedContext.getCurrentUser() instanceof Guest){
            this.view.displayInfo("Welcome");
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
//            System.out.println( "Role:"+  ((AuthenticatedUser)this.sharedContext.getCurrentUser()).getRole());
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

    /**
     * Handles the main menu options for guest users.
     * Displays the guest main menu options and performs actions based on the user's choice.
     * Options include login, consulting FAQ, searching pages, and contacting staff.
     *
     * @return true if the user successfully logs in, false otherwise
     */
    private boolean handleGuestMainMenu(){
        GuestController guest = new GuestController(this.sharedContext, this.view, this.authenticationService, this.emailService);
        InquirerController inquirer = new InquirerController(this.sharedContext, this.view, this.authenticationService, this.emailService);
        this.view.displayDivider();
        this.view.displayInfo("Guest");
        this.view.displayDivider();

        int option = guest.selectFromMenu(EnumSet.allOf(GuestMainMenuOption.class), "Please choose an option");
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
            this.view.displayError("Bad number, please choose a correct option");
//            this.view.displayDivider();
        }
        return false;
    }

    /**
     * Handles the main menu options for student users.
     * Displays the student main menu options and performs actions based on the user's choice.
     * Options include logout, consulting FAQ, contacting staff, and searching pages.
     *
     * @return true if the user successfully logs out and becomes a guest, false otherwise
     */
    private boolean handleStudentMainMenu() {
        InquirerController inquirer = new InquirerController(this.sharedContext, this.view, this.authenticationService, this.emailService);
        AuthenticatedUserController student = new AuthenticatedUserController(this.sharedContext, this.view, this.authenticationService, this.emailService);

        this.view.displayDivider();
        this.view.displayInfo("Student");
        this.view.displayDivider();

        int option = student.selectFromMenu(EnumSet.allOf(StudentMainMenuOption.class), "Please choose an option");
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
            this.view.displayError("Bad number, please choose a correct option");
//            this.view.displayDivider();
            //throw new IllegalArgumentException();
        }
        return false;
    }

    /**
     * Handles the main menu options for teaching staff users.
     * Displays the teaching staff main menu options and performs actions based on the user's choice.
     * Options include logout and managing received inquiries.
     *
     * @return true if the user successfully logs out and becomes a guest, false otherwise
     */
    private boolean handleTeachingStaffMainMenu(){
        TeachingStaffController teacher = new TeachingStaffController(this.sharedContext,this.view,this.authenticationService,this.emailService);
        AuthenticatedUserController teacherUser = new AuthenticatedUserController(this.sharedContext,this.view,this.authenticationService,this.emailService);
        this.view.displayDivider();
        this.view.displayInfo("TeachingStaff");
        this.view.displayDivider();

        int option = teacher.selectFromMenu(EnumSet.allOf(TeachingStaffMainMenuOption.class), "Please choose an option");
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
            this.view.displayError("Bad number, please choose a correct option");
//            this.view.displayDivider();
//           throw new IllegalArgumentException();
        }
        return false;
    }

    /**
     * Handles the main menu options for admin staff users.
     * Displays the admin staff main menu options and performs actions based on the user's choice.
     * Options include logout, adding a page, managing FAQ, managing inquiries, and viewing all pages.
     *
     * @return true if the user successfully logs out and becomes a guest, false otherwise
     */
    private boolean handleAdminStaffMainMenu(){
        AuthenticatedUserController adminStaffUser = new AuthenticatedUserController(this.sharedContext,this.view,this.authenticationService,this.emailService);
        AdminStaffController adminstaff = new AdminStaffController(this.sharedContext,this.view,this.authenticationService,this.emailService);

        this.view.displayDivider();
        this.view.displayInfo("Adminstaff");
        this.view.displayDivider();

        int option = adminstaff.selectFromMenu(EnumSet.allOf(AdminStaffMainMenuOption.class), "Please choose an option");

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
            this.view.displayError("Bad number, please choose a correct option");
//            this.view.displayDivider();
//            throw new IllegalArgumentException();
        }
        return false;
    }
}
