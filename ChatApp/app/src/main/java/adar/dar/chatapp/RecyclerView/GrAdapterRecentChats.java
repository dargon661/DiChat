package adar.dar.chatapp.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Objects;

import adar.dar.chatapp.ChatActivity;
import adar.dar.chatapp.Model.ChatRoom;
import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.User;
import adar.dar.chatapp.R;

public class GrAdapterRecentChats extends RecyclerView.Adapter<GrAdapterRecentChats.MyViewHolder> {
    private List<ChatRoom> chatRooms;
    private Context context;

    public GrAdapterRecentChats(List<ChatRoom> chatRooms, Context context) {
        this.chatRooms = chatRooms;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);

        // ✅ Show last message and time IMMEDIATELY

        if(Objects.equals(chatRoom.getLastMsgUserId(), FireBaseUtil.currentUserId())) {
            holder.lastMessage.setText("You: "+chatRoom.getLastMessage());
        } else
            holder.lastMessage.setText(chatRoom.getLastMessage());

        holder.MessageTime.setText(FireBaseUtil.LastmessageTimeStamp(chatRoom.getLastMsgTime()));

        // ✅ Fetch user details ASYNCHRONOUSLY (won't block UI)
        FireBaseUtil.getOtherUserFromChatroom(chatRoom.getUserIds()).get().addOnSuccessListener(task -> {
            if (task.exists()) {
                User otherUser = task.toObject(User.class);

                if (otherUser != null) {
                    holder.username.setText(otherUser.getUsername());
                    FireBaseUtil.GetOthertProfileReference(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                        if(t.isSuccessful())
                        {
                            Uri uri= t.getResult();
                            Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.image);
                        }


                    });

                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("userName", otherUser.getUsername());
                        intent.putExtra("token", otherUser.getFCMtoken());
                        intent.putExtra("userPhone", otherUser.getPhone());
                        intent.putExtra("userImage", otherUser.getProfileImageUrl());
                        intent.putExtra("Timestamp", otherUser.getCreatedTimestamp());
                        intent.putExtra("userID", otherUser.getUserId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public void updateList(List<ChatRoom> newList) {
        this.chatRooms = newList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, lastMessage, MessageTime;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessage = itemView.findViewById(R.id.IUCLlastMessage);
            MessageTime = itemView.findViewById(R.id.IUCLtime);
            username = itemView.findViewById(R.id.IUCLuser);
            image = itemView.findViewById(R.id.IUCLprofile_image);
        }
    }
}


