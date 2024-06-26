package model;
import java.time.LocalDateTime;
public class Inquiry {
    private String inquirerEmail;
    private String subject;
    private String content;
    private String assignedTo;

    private LocalDateTime time;
    public Inquiry(String subject, String content, String inquirerEmail){
        this.subject = subject;
        this.content = content;
        this.inquirerEmail = inquirerEmail;
        this.assignedTo = null;
        this.time = LocalDateTime.now();
    }
    public String getInquirerEmail(){
        return this.inquirerEmail;
    }
    public String getSubject(){
        return this.subject;
    }
    public String getContent(){
        return this.content;
    }
    public String getAssignedTo(){
        return this.assignedTo;
    }
    public void setAssignedTo(String assignedTo){
        this.assignedTo = assignedTo;
    }
    public void setContent(String content){
        this.content = content;
    }

    public LocalDateTime getTime(){
        return this.time;
    }
}
