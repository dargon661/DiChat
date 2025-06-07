package adar.dar.chatapp.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.Message;
import adar.dar.chatapp.ChatActivity;

import adar.dar.chatapp.R;

public class GrAdapterChat extends RecyclerView.Adapter<GrAdapterChat.MyViewHolder>  {
    private List<Message> messages;
    Context context;
    public GrAdapterChat(List<Message> messages, Context context) {
        this.messages = messages;
        this.context=context;

    }
    @NonNull
    @Override
    public GrAdapterChat.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_layout, parent,false);
        return new  GrAdapterChat.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GrAdapterChat.MyViewHolder holder, int position) {
        Message msg = messages.get(position);

        if(msg.getSenderId().equals(FireBaseUtil.currentUserId()))
        {
            holder.reciever.setVisibility(View.GONE);
            holder.sender.setVisibility(View.VISIBLE);
            holder.senderText.setText(msg.getMessage());
        }
        else
        {
            holder.reciever.setVisibility(View.VISIBLE);
            holder.sender.setVisibility(View.GONE);
            holder.recieverText.setText(msg.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sender;
        LinearLayout reciever;
        TextView senderText;
        TextView recieverText;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sender=itemView.findViewById(R.id.IMLsender);
            reciever=itemView.findViewById(R.id.IMLreciever);
            senderText=itemView.findViewById(R.id.IMLsenderTextView);
            recieverText=itemView.findViewById(R.id.IMLrecieverTextView);



        }
    }
}




