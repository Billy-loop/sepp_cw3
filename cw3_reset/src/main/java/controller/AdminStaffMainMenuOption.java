package controller;

public enum AdminStaffMainMenuOption {
    LOGOUT(0),
    MANAGE_QUERIES(1),
    ADD_PAGE(2),
    SEE_ALL_PAGES(3),
    MANAGE_FAQ(4);

    public final int value;
    private AdminStaffMainMenuOption(int value){
        this.value = value;
    }
}
