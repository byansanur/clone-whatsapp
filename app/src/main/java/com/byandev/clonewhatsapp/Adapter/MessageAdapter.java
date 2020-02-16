package com.byandev.clonewhatsapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.byandev.clonewhatsapp.Models.Messages;
import com.byandev.clonewhatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> usMessageList;
    private Context context;
    private FirebaseAuth auth;
    private DatabaseReference usRef;

    public MessageAdapter(List<Messages> usMessageList, Context context) {
        this.usMessageList = usMessageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.custom_message_layout, parent, false);

        auth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderID = auth.getCurrentUser().getUid();
        Messages messages = usMessageList.get(position);

        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        usRef = FirebaseDatabase.getInstance().getReference().child("users").child(fromUserId);
        usRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("imaage")) {
                    String  receiverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get()
                        .load(receiverImage)
                        .placeholder(R.drawable.ic_profile_default_foreground)
                        .into(holder.receiverImageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (fromMessageType.equals("text")) {
            holder.receiverMessage.setVisibility(View.INVISIBLE);
            holder.receiverImageProfile.setVisibility(View.INVISIBLE);
            holder.senderMessage.setVisibility(View.INVISIBLE);


            if (fromUserId.equals(messageSenderID)) {
                holder.senderMessage.setVisibility(View.VISIBLE);

                holder.senderMessage.setBackgroundResource(R.drawable.sender_message_layout);
                holder.senderMessage.setTextColor(Color.BLACK);
                holder.senderMessage.setText(messages.getMessage());
            } else {
                holder.receiverImageProfile.setVisibility(View.VISIBLE);
                holder.receiverMessage.setVisibility(View.VISIBLE);

                holder.receiverMessage.setBackgroundResource(R.drawable.reciver_message_layout);
                holder.receiverMessage.setTextColor(Color.BLACK);
                holder.receiverMessage.setText(messages.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return usMessageList.size();
    }

    public class MessageViewHolder extends  RecyclerView.ViewHolder{

        public TextView senderMessage, receiverMessage;
        public CircleImageView receiverImageProfile;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.sender_message);
            receiverMessage = itemView.findViewById(R.id.receiver_message);
            receiverImageProfile = itemView.findViewById(R.id.imgeProfile);
        }
    }
}
