package co.bongga.touristeando.models;

/**
 * Created by bongga on 2/11/17.
 */

public class Query {
    private String message;
    private long timestamp;
    private String so;

    public Query() {
    }

    public Query(String message, long timestamp, String so) {
        this.message = message;
        this.timestamp = timestamp;
        this.so = so;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so;
    }
}
