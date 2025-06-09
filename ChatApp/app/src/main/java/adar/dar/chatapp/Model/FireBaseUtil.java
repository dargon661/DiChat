package adar.dar.chatapp.Model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FireBaseUtil {

    // Returns the current authenticated user's UID
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Reference to the current user's Firestore document
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    // Reference to the entire "users" collection
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // Reference to the "chatrooms" collection
    public static CollectionReference GetAllChatRooms() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    // Reference to the "requests" collection
    public static CollectionReference GetAllRequests() {
        return FirebaseFirestore.getInstance().collection("requests");
    }

    // Delete a friend request by ID
    public static void deleteRequest(String requestId) {
        getRequestsReference(requestId).delete();
    }

    // Asynchronously fetches all users except the current user
    public static CompletableFuture<List<User>> getAllUsers() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        allUserCollectionReference().get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user.getUserId().equals(currentUserId()))
                            continue;
                        userList.add(user);
                    }
                    future.complete(userList);
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    // Reference to a specific chatroom by ID
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // Reference to a specific request by ID
    public static DocumentReference getRequestsReference(String requestId) {
        return FirebaseFirestore.getInstance().collection("requests").document(requestId);
    }

    // Generates a unique chatroom ID for two users (deterministic order)
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Generates a unique request ID for two users (same logic as chatroom ID)
    public static String getRequestId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Returns reference to the "chats" subcollection of a specific chatroom
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // Returns a reference to the other user in the chatroom
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    // Converts a Timestamp into a readable date (dd/MM/yyyy)
    public static String TimeStampToTime(Timestamp timestamp) {
        Date date = timestamp.toDate();
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    // Returns a formatted string based on how recent the message timestamp is
    public static String LastmessageTimeStamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        Calendar messageCal = Calendar.getInstance();
        messageCal.setTime(date);

        Calendar now = Calendar.getInstance();

        // If message is from today, return just the time
        if (messageCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            return new SimpleDateFormat("HH:mm").format(date);
        }

        // If message is from yesterday
        now.add(Calendar.DAY_OF_YEAR, -1);
        if (messageCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday";
        }

        // Otherwise return the full date
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    // Updates the last message and timestamp in a chatroom
    public static void notifyMessageSent(String chatroomId, String messageText) {
        DocumentReference chatroomRef = getChatroomReference(chatroomId);
        chatroomRef.update("lastMsgTime", Timestamp.now(), "lastMessage",  messageText)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Chat updated successfully.");
                });
    }

    // Signs the user out
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // Returns a reference to the current user's profile picture in Firebase Storage
    public static StorageReference GetCurrentProfileReference() {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pictures")
                .child(currentUserId() + ".jpg");
    }

    // Returns a reference to another user's profile picture in Firebase Storage
    public static StorageReference GetOthertProfileReference(String id) {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pictures")
                .child(id + ".jpg");
    }
}
