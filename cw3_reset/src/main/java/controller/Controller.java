package controller;

import external.AuthenticationService;
import external.EmailService;
import model.SharedContext;
import view.View;

import java.util.Collection;

public class Controller {
    protected SharedContext sharedContext;
    protected View view;
    protected AuthenticationService authenticationService;
    protected EmailService emailService;

    protected Controller(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService) {
        this.sharedContext = sharedContext;
        this.view = view;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }

    /**
     * Displays a menu of options to the user and prompts them to choose one.
     *
     * @param options a collection of options to be displayed in the menu
     * @param instruction is message, please choose an option
     * @param <T> the type of the options, must be an Enum
     * @return the index of the selected option in the collection, or Integer.MAX_VALUE if the input is invalid
     */
    protected <T extends Enum<T>> int selectFromMenu(Collection<T> options, String instruction){
        int index = 0;
        for (T option : options){//Iterate option in MenuOption
            this.view.displayInfo("[" + index +"]" + option);
            index++;
        }
        this.view.displayInfo(instruction);
        try{//Choose option
            int chosen = Integer.parseInt(this.view.getInput("Choose:"));
            if(chosen >= index){
                this.view.displayInfo("Please Press Correct option");
                return Integer.MAX_VALUE;
            }
            return chosen;
        }catch (NumberFormatException e){
            return Integer.MAX_VALUE;
        }
    }
}
