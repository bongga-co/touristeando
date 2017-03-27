package co.bongga.touristeando.models;

/**
 * Created by bongga on 2/3/17.
 */

public class HelpFeedback {
    private String name;
    private String email;
    private String message;
    private String source;

    public HelpFeedback() {
    }

    public HelpFeedback(String name, String email, String message, String source) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
