package controller;

public enum GuestMainMenuOption {
    LOGIN(0),
    CONSULT_FAQ(1),
    SEARCH_PAGES(2),
    CONTACT_STAFF(3);

    public final int value;
    private GuestMainMenuOption(int value) {
        this.value = value;
    }

}
