package adar.dar.chatapp.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {
    String message;
    String senderId;

    @ServerTimestamp
    Date timestamp;

    public Message() {
    }

    public Message(String message, String senderId, Date timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }


    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getTimestamp() {
        return timestamp;
    }


}
