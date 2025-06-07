package adar.dar.chatapp.Model;

import java.util.List;
import com.google.firebase.Timestamp;

public class ChatRoom {
    String RoomId;
    List<String> UserIds;
    Timestamp lastMsgTime;
    String lastMsgUserId;
    String lastMessage;

    public ChatRoom() {
    }

    public ChatRoom(String roomId, List<String> userIds) {
        RoomId = roomId;
        UserIds = userIds;
    }

    public ChatRoom(String roomId, List<String> userIds, Timestamp lastMsgTime, String lastMsgUserId) {
        RoomId = roomId;
        UserIds = userIds;
        this.lastMsgTime = lastMsgTime;
        this.lastMsgUserId = lastMsgUserId;
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public List<String> getUserIds() {
        return UserIds;
    }

    public void setUserIds(List<String> userIds) {
        UserIds = userIds;
    }

    public Timestamp getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(Timestamp lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMsgUserId() {
        return lastMsgUserId;
    }

    public void setLastMsgUserId(String lastMsgUserId) {
        this.lastMsgUserId = lastMsgUserId;
    }
}
