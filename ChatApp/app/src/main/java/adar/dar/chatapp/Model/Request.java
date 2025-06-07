package adar.dar.chatapp.Model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Request {
    String requestId;
    String senderId;
    String RecieverId;
    Timestamp createdTimestamp;

    public Request() {
    }

    public Request(String requestId, String senderId, String recieverId, Timestamp createdTimestamp) {
        this.requestId = requestId;
        this.senderId = senderId;
        RecieverId = recieverId;
        this.createdTimestamp = createdTimestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecieverId() {
        return RecieverId;
    }

    public void setRecieverId(String recieverId) {
        RecieverId = recieverId;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
