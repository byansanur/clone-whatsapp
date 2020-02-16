package com.byandev.clonewhatsapp.Find;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.byandev.clonewhatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String receiverUserID, currentState, sendertUserId;

    private CircleImageView profileUser;
    private TextView tvUsername, tvUserstatus;
    private FloatingActionButton fabSend;
    private Button btCancleChat;

    private DatabaseReference reference, chatRequest, contactRef, notifRef;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("users");
        chatRequest = FirebaseDatabase.getInstance().getReference().child("chat request");
        contactRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        notifRef = FirebaseDatabase.getInstance().getReference().child("notification");

        receiverUserID = Objects.requireNonNull(getIntent().getExtras().get("visit_user_id")).toString();
        sendertUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        profileUser = findViewById(R.id.imgProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvUserstatus = findViewById(R.id.tvUserStatus);
        fabSend = findViewById(R.id.fabSend);
        btCancleChat = findViewById(R.id.btCancleChat);
        currentState = "new";

        retriveUserInfo();
    }

    private void retriveUserInfo() {
        reference.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {
                    String userImage = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                    String userName = Objects.requireNonNull(dataSnapshot.child("aname").getValue()).toString();
                    String userStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();

                    Picasso.get()
                        .load(userImage)
                        .placeholder(R.drawable.ic_profile_default_foreground)
                        .into(profileUser);

                    tvUsername.setText(userName);
                    tvUserstatus.setText(userStatus);

                    chatRequest();

                } else {

                    String userName = Objects.requireNonNull(dataSnapshot.child("aname").getValue()).toString();
                    String userStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();

                    tvUsername.setText(userName);
                    tvUserstatus.setText(userStatus);

                    chatRequest();
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chatRequest() {
        chatRequest.child(sendertUserId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(receiverUserID)) {
                        String req_type = dataSnapshot.child(receiverUserID).child("req_type").getValue().toString();
                        if (req_type.equals("sent")) {
                            currentState = "req_type";
                            fabSend.setImageDrawable(getDrawable(R.drawable.ic_clear_white_24dp));
                        }
                        else if (req_type.equals("received")) {
                            currentState = "req_received";
                            fabSend.setImageDrawable(getDrawable(R.drawable.ic_check_black_24dp));
                            btCancleChat.setVisibility(View.VISIBLE);
                            btCancleChat.setEnabled(true);
                            btCancleChat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelChat();
                                }
                            });
                        }
                    } else {
                        contactRef.child(sendertUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiverUserID)) {
                                        currentState = "friends";
                                        fabSend.setImageDrawable(getDrawable(R.drawable.ic_clear_white_24dp));

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        if (!sendertUserId.equals(receiverUserID)) {
            fabSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabSend.setEnabled(false);
                    if (currentState.equals("new")) {
                        sendChatRequest();
                    }
                    if (currentState.equals("req_sent")) {
                        cancelChat();
                    }
                    if (currentState.equals("req_received")) {
                        acceptChatRequest();
                    }
                    if (currentState.equals("friends")) {
                        removeSpesificContacts();
                    }
                }
            });
        } else {
            fabSend.setVisibility(View.INVISIBLE);
        }
    }

    private void removeSpesificContacts() {
        contactRef.child(sendertUserId).child(receiverUserID)
            .removeValue()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        contactRef.child(receiverUserID).child(sendertUserId)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        fabSend.setEnabled(true);
                                        currentState = "req_sent";
                                        fabSend.setImageDrawable(getDrawable(R.drawable.ic_send_black_24dp));

                                        btCancleChat.setVisibility(View.INVISIBLE);
                                        btCancleChat.setEnabled(true);
                                    }
                                }
                            });
                    }
                }
            });
    }

    private void acceptChatRequest() {
        contactRef.child(sendertUserId).child(receiverUserID)
            .child("Contacts").setValue("Saved")
            .addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        contactRef.child(receiverUserID).child(sendertUserId)
                            .child("Contacts").setValue("Saved")
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        chatRequest.child(sendertUserId).child(receiverUserID)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        chatRequest.child(receiverUserID).child(sendertUserId)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                                            {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    fabSend.setEnabled(true);
                                                                    currentState = "friends";
                                                                    fabSend.setImageDrawable(getDrawable(R.drawable.ic_clear_white_24dp));

                                                                    btCancleChat.setVisibility(View.INVISIBLE);
                                                                    btCancleChat.setEnabled(false);
                                                                }
                                                            });
                                                    }
                                                }
                                            });
                                    }
                                }
                            });
                    }
                }
            });
    }

    private void cancelChat() {

        chatRequest.child(sendertUserId).child(receiverUserID)
            .removeValue()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        chatRequest.child(receiverUserID).child(sendertUserId)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        fabSend.setEnabled(true);
                                        currentState = "req_sent";
                                        fabSend.setImageDrawable(getDrawable(R.drawable.ic_send_black_24dp));

                                        btCancleChat.setVisibility(View.INVISIBLE);
                                        btCancleChat.setEnabled(true);
                                    }
                                }
                            });
                    }
                }
            });
    }

    private void sendChatRequest() {
        chatRequest.child(sendertUserId).child(receiverUserID)
            .child("req_type").setValue("sent")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        chatRequest.child(receiverUserID).child(sendertUserId)
                            .child("req_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        HashMap<String , String> chatNotifMap = new HashMap<>();
                                        chatNotifMap.put("from", sendertUserId);
                                        chatNotifMap.put("type", "request");
                                        notifRef.child(receiverUserID).push()
                                            .setValue(chatNotifMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        fabSend.setEnabled(true);
                                                        currentState = "req_sent";
                                                        fabSend.setImageDrawable(getDrawable(R.drawable.ic_clear_white_24dp));
                                                    }
                                                }
                                            });
                                    }
                                }
                            });
                    }
                }
            });
    }
}
