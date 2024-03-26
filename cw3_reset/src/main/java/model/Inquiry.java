package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Inquiry {
    private LocalDateTime createdAt;
    private String inquirerEmail;
    private String subject;
    private String content;
    private String assignedTo;

    public Inquiry(String inquirerEmail, String subject, String content){
        this.createdAt = LocalDateTime.now();
        this.inquirerEmail = inquirerEmail;
        this.subject = subject;
        this.content = content;
        this.assignedTo = SharedContext.ADMIN_STAFF_EMAIL;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public String getInquirerEmail() {
        return inquirerEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}