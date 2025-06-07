package adar.dar.chatapp.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

import adar.dar.chatapp.ChatActivity;
import adar.dar.chatapp.Model.ChatRoom;
import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.Request;
import adar.dar.chatapp.Model.User;
import adar.dar.chatapp.R;

public class GrAdapter extends RecyclerView.Adapter<GrAdapter.MyViewHolder>  {
    private List<User> users;
    Context context;
    public GrAdapter(List<User> users, Context context) {
        this.users = users;
        this.context=context;

    }
    @NonNull
    @Override
    public GrAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_layout, parent,false);
        return new GrAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GrAdapter.MyViewHolder holder, int position) {
        User item = users.get(position);
        holder.username.setText(item.getUsername());
        holder.PhoneNumber.setText(item.getPhone());

        holder.sendRequest.setOnClickListener(v -> {
            String userID = item.getUserId();
            String requestId = FireBaseUtil.getRequestId(FireBaseUtil.currentUserId(), userID);

            FireBaseUtil.getRequestsReference(requestId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final Request[] requestHolder = new Request[1];

                    Request request = task.getResult().toObject(Request.class);
                    if (request == null) {
                        requestHolder[0] = new Request(requestId, FireBaseUtil.currentUserId(), userID, com.google.firebase.Timestamp.now());

                        FireBaseUtil.GetAllChatRooms().get().addOnSuccessListener(querySnapshot2 -> {
                            boolean exists = false;

                            for (DocumentSnapshot document : querySnapshot2.getDocuments()) {
                                ChatRoom chatRoom = document.toObject(ChatRoom.class);
                                if (chatRoom.getRoomId().equals(requestId)) {
                                    exists = true;
                                    break; // Stop loop early
                                }
                            }

                            if (!exists) {
                                FireBaseUtil.getRequestsReference(requestId).set(requestHolder[0]); // ✅ Use requestHolder[0]
                                int gray2 = ContextCompat.getColor(context, R.color.gray2);
                                holder.sendRequest.setBackgroundTintList(ColorStateList.valueOf(gray2));
                                holder.sendRequest.setText("Sent");
                            }
                        }).addOnFailureListener(e -> e.printStackTrace());
                    }
                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView PhoneNumber;

        ImageView image;
        Button sendRequest;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sendRequest= itemView.findViewById(R.id.IULsend);
            PhoneNumber=itemView.findViewById(R.id.IULPhoneNumber);
            username=itemView.findViewById(R.id.IULuser);
            image=itemView.findViewById(R.id.IULprofile_image);
            sendRequest.setOnClickListener(v -> {
                sendRequest.setBackgroundColor(itemView.getResources().getColor(R.color.white));

            });

        }
    }
}
