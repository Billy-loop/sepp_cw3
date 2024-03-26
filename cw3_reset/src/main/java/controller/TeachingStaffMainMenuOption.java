package controller;

public enum TeachingStaffMainMenuOption {
    LOGOUT(0),
    MANAGE_RECEIVED_QUERIES(1);
    public final int value;
    private TeachingStaffMainMenuOption(int value){
        this.value = value;
    }
}
