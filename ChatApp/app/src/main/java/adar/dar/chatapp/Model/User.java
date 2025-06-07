package adar.dar.chatapp.Model;


import com.google.firebase.Timestamp;

public class User {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String profileImageUrl;
    private String userId;
    String FCMtoken;

    public String getFCMtoken() {
        return FCMtoken;
    }

    public void setFCMtoken(String FCMtoken) {
        this.FCMtoken = FCMtoken;
    }

    public User() {
    }

    public User(String phone, String username, Timestamp createdTimestamp, String profileImageUrl, String userID) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.profileImageUrl = profileImageUrl;
        this.userId=userID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
