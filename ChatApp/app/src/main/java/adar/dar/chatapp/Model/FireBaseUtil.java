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
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }


    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference GetAllChatRooms() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static CollectionReference GetAllRequests() {
        return FirebaseFirestore.getInstance().collection("requests");
    }
    public static void deleteRequest(String requestId) {
        getRequestsReference(requestId).delete();
    }

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
                .addOnFailureListener(e -> future.completeExceptionally(e));
        return future;
    }


    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static DocumentReference getRequestsReference(String requestId) {
        return FirebaseFirestore.getInstance().collection("requests").document(requestId);
    }




    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }
    public static String getRequestId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String TimeStampToTime(Timestamp timestamp)
    {
        Date date = timestamp.toDate();
        Calendar messageCal = Calendar.getInstance();
        messageCal.setTime(date);
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static String LastmessageTimeStamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        Calendar messageCal = Calendar.getInstance();
        messageCal.setTime(date);

        Calendar now = Calendar.getInstance();

        // Check if the message is from today
        if (messageCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            return new SimpleDateFormat("HH:mm").format(date); // Return time (e.g., "14:30")
        }

        // Check if the message is from yesterday
        now.add(Calendar.DAY_OF_YEAR, -1);
        if (messageCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday";
        }

        // If the message is older than yesterday, return the date
        return new SimpleDateFormat("dd/MM/yyyy").format(date); // Return date (e.g., "25/03/2025")
    }
    public static void notifyMessageSent(String chatroomId, String messageText) {
        DocumentReference chatroomRef = getChatroomReference(chatroomId);

        // ✅ Update both last message time & last message content
        chatroomRef.update("lastMsgTime", Timestamp.now(), "lastMessage",  messageText)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Chat updated successfully.");
                });
    }
    public static void logout()
    {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference GetCurrentProfileReference(){
       return FirebaseStorage.getInstance().getReference().child("profile_pictures").child(FireBaseUtil.currentUserId() +".jpg");
    }
    public static StorageReference GetOthertProfileReference(String id){
        return FirebaseStorage.getInstance().getReference().child("profile_pictures").child(id +".jpg");
    }
//storageReference.child("profile_pictures/" +  + ".jpg");

}
