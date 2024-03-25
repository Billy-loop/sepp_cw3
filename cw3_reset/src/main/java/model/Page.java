package model;

public class Page {
    private boolean isPrivate;
    private String title;
    private String content;

    public Page(String title, String content, boolean isPrivate) {

        this.isPrivate = isPrivate;
        this.title = title;
        this.content = content;
    }
    public boolean getPrivate(){
        return this.isPrivate;
    }
    public String getTitle(){
        return this.title;
    }

    public String getContent(){
        return this.content;
    }

}

