package adar.dar.chatapp.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import adar.dar.chatapp.ChatActivity;
import adar.dar.chatapp.Model.ChatRoom;
import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.Request;
import adar.dar.chatapp.Model.User;
import adar.dar.chatapp.R;

public class GrAdapterRequest extends RecyclerView.Adapter<GrAdapterRequest.MyViewHolder>  {
    private List<Request> requests;
    Context context;
    public GrAdapterRequest(List<Request> requests, Context context) {
        this.requests = requests;
        this.context=context;

    }
    @NonNull
    @Override
    public GrAdapterRequest.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_layout, parent,false);
        return new GrAdapterRequest.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GrAdapterRequest.MyViewHolder holder, int position) {
        Request request = requests.get(position);
        FireBaseUtil.allUserCollectionReference().document(request.getSenderId()).get().addOnSuccessListener(task -> {
            if(task.exists())
            {
                User otherUser = task.toObject(User.class);
                if (otherUser != null) {
                    holder.username.setText(otherUser.getUsername());
                    holder.PhoneNumber.setText(otherUser.getPhone());
                    FireBaseUtil.GetOthertProfileReference(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            Uri uri = t.getResult();
                            Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.image);
                        }


                    });





                    holder.accept.setOnClickListener(view -> {
                        Intent intent=new Intent(context, ChatActivity.class);
                        intent.putExtra("userName", otherUser.getUsername());
                        intent.putExtra("userPhone", otherUser.getPhone());
                        intent.putExtra("userImage", otherUser.getProfileImageUrl());
                        intent.putExtra("token", otherUser.getFCMtoken());
                        intent.putExtra("Timestamp", otherUser.getCreatedTimestamp());
                        intent.putExtra("userID", otherUser.getUserId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        FireBaseUtil.deleteRequest(request.getRequestId());
                        context.startActivity(intent);
                    });
                    holder.ignore.setOnClickListener(v -> {
                        FireBaseUtil.deleteRequest(request.getRequestId());
                    });
                }
            }
        });






    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView PhoneNumber;

        ImageView image;
        Button accept;
        Button ignore;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            accept= itemView.findViewById(R.id.IRLsend);
            ignore=itemView.findViewById(R.id.IRLDelete);
            PhoneNumber=itemView.findViewById(R.id.IRLPhoneNumber);
            username=itemView.findViewById(R.id.IRLuser);
            image=itemView.findViewById(R.id.IRLprofile_image);


        }
    }
}
