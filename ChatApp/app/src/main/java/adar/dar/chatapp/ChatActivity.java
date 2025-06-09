package adar.dar.chatapp;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.Message;
import adar.dar.chatapp.Model.User;
import adar.dar.chatapp.Model.ChatRoom;
import adar.dar.chatapp.RecyclerView.GrAdapter;
import adar.dar.chatapp.RecyclerView.GrAdapterChat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    EditText msg;
    RecyclerView Chat;
    ImageView profilePic;
    ImageButton send;

    User secondUser;
    List<Message> messageList;
    String chatroomId;
    ChatRoom chatRoom;
    ImageButton backArrow;
    TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        InitChatRoom();

        InitViews();

    }


    private void InitChatRoom() {
        String userName= getIntent().getExtras().getString("userName");

        String userID= getIntent().getExtras().getString("userID");
        String userImage= getIntent().getExtras().getString("userImage");
        String userPhone= getIntent().getExtras().getString("userPhone");
        String Timestamp= getIntent().getExtras().getString("Timestamp");
        secondUser=new User(userPhone,userName,null,userImage,userID);
        secondUser.setFCMtoken(getIntent().getExtras().getString("token"));


        chatroomId= FireBaseUtil.getChatroomId(FireBaseUtil.currentUserId(),secondUser.getUserId());

        FireBaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoom=task.getResult().toObject(ChatRoom.class);
                if(chatRoom==null) {
                    chatRoom = new ChatRoom(chatroomId, Arrays.asList(FireBaseUtil.currentUserId(), secondUser.getUserId()), com.google.firebase.Timestamp.now(), "");
                    FireBaseUtil.getChatroomReference(chatroomId).set(chatRoom);
                }
            }

        });




    }

    private void InitViews() {
        backArrow = findViewById(R.id.ACAback);
        username = findViewById(R.id.ACusername);
        Chat = findViewById(R.id.ACmessages);
        send = findViewById(R.id.ACsend);
        msg = findViewById(R.id.ACmsg);
        profilePic=findViewById(R.id.ACprofile_image);
        messageList = new ArrayList<>();
        MessageList();

        send.setOnClickListener(this);
        username.setText(secondUser.getUsername());
        FireBaseUtil.GetOthertProfileReference(secondUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
            if(t.isSuccessful())
            {
                Uri uri= t.getResult();
                Glide.with(this).load(uri).apply(RequestOptions.circleCropTransform()).into(profilePic);
            }


        });
        backArrow.setOnClickListener(v -> onBackPressed());


    }


    @Override
    public void onClick(View view) {
        String message=msg.getText().toString().trim();
        if(message.isEmpty())
            return;
        SendMessage(message);
        FireBaseUtil.notifyMessageSent(chatroomId, message);
    }






    public void MessageList() {
        FireBaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Message> newMessages = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            newMessages.add(message);
                        }

                        // Only update if messages changed
                        if (!newMessages.equals(messageList)) {
                            messageList.clear();
                            messageList.addAll(newMessages);
                            ShowMessages();
                        }
                    }
                });
    }


    public void ShowMessages() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from the bottom
        Chat.setLayoutManager(layoutManager);

        GrAdapterChat grAdapter = new GrAdapterChat(messageList, this);
        Chat.setAdapter(grAdapter);

        // Scroll to the last message
        if (messageList.size() > 0) {
            Chat.scrollToPosition(messageList.size() - 1);
        }
    }


    private void SendMessage(String message) {
        chatRoom.setLastMsgTime(Timestamp.now());
        chatRoom.setLastMsgUserId(FireBaseUtil.currentUserId());
        chatRoom.setLastMessage(message);
        FireBaseUtil.getChatroomReference(chatroomId).set(chatRoom);

        Message messageModel = new Message(message, secondUser.getUserId(), null);

        FireBaseUtil.getChatroomMessageReference(chatroomId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    msg.setText("");
                    Chat.scrollToPosition(messageList.size() - 1);
                    SendNotification(message);

                }
            }
        });
    }
    void SendNotification(String message) {
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User currentUser = task.getResult().toObject(User.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject messageObject = new JSONObject();

                    messageObject.put("token", secondUser.getFCMtoken()); // target

                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", currentUser.getUsername());
                    notificationObject.put("body", message);
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("userId", currentUser.getUserId());
                    messageObject.put("notification", notificationObject);
                    messageObject.put("data", dataObject);
                    jsonObject.put("message", messageObject);
                    if (secondUser.getFCMtoken() == null || secondUser.getFCMtoken().isEmpty()) {
                        Log.e("FCM", "Token is missing for user " + secondUser.getUserId());
                    }
                    // Get access token dynamically
                    String accessToken = new AccessToken().getAccessToken();
                    if (accessToken != null) {
                        CallApi(jsonObject, accessToken);
                    } else {
                        Log.e("FCM", "Failed to get access token");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    void CallApi(JSONObject jsonObject, String accessToken) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/v1/projects/dichat-10c0b/messages:send";

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("FCM", "Response: " + responseBody);
            }
        });
    }





}