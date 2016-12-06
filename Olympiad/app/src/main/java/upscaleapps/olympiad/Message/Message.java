package upscaleapps.olympiad.Message;


import java.util.Date;

public class Message {

    private String from;
    private String text;
    private long timestamp;

    public Message(String from, String text) {
        this.from = from;
        this.text = text;
        timestamp = new Date().getTime();
    }

    public Message() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
