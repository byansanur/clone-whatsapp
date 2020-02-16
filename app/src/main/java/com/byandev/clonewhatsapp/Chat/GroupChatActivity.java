package com.byandev.clonewhatsapp.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.byandev.clonewhatsapp.MainActivity;
import com.byandev.clonewhatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity {

  private Toolbar toolbar;
  private ImageButton fabSend;
  private EditText etMessageInput;
  private ScrollView scrollView;
  private TextView displayTextMessage;

  private String currentGroupName, currentUserId, currentUsername, currentDate, currentTime;
  private Context context;

  private FirebaseAuth auth;
  private DatabaseReference refUser, refGroupName, groupMessageKeyRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_chat);

    context = this;
    currentGroupName = getIntent().getExtras().get("groupName").toString();

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowCustomEnabled(true);
    Objects.requireNonNull(getSupportActionBar()).setTitle(currentGroupName);
    fabSend = findViewById(R.id.fabSend);
    etMessageInput = findViewById(R.id.etInputMessageGroup);
    displayTextMessage = findViewById(R.id.chatDisplay);
//    displayTextMessage.setVisibility(View.GONE);
    scrollView = findViewById(R.id.pull);

    auth = FirebaseAuth.getInstance();
    currentUserId = auth.getCurrentUser().getUid();
    refUser = FirebaseDatabase.getInstance().getReference().child("users");
    refGroupName = FirebaseDatabase.getInstance().getReference().child("groups").child(currentGroupName);

    getUserInfo();

    listener();


  }

  @Override
  protected void onStart() {
    super.onStart();
    refGroupName.addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
          DisplayMessages(dataSnapshot);
        }
      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
          DisplayMessages(dataSnapshot);
        }
      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }



  private void listener() {
    fabSend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        saveMessageInfoToDatabase();
        etMessageInput.setText("");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

      }
    });
  }



  private void getUserInfo() {
    refUser.child(currentUserId).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          currentUsername = dataSnapshot.child("aname").getValue().toString();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void saveMessageInfoToDatabase() {

    String message = etMessageInput.getText().toString();
    String messageKey = refGroupName.push().getKey();

    if (TextUtils.isEmpty(message)) {
      Toast.makeText(context, "Please write the message...", Toast.LENGTH_SHORT).show();
    } else  {
      Calendar calendar = Calendar.getInstance();
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
      currentDate = dateFormat.format(calendar.getTime());

      Calendar time = Calendar.getInstance();
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
      currentTime = timeFormat.format(time.getTime());

      HashMap<String, Object> groupMessage = new HashMap<>();
      refGroupName.updateChildren(groupMessage);

      groupMessageKeyRef = refGroupName.child(messageKey);

      HashMap<String, Object> messageInfoMap = new HashMap<>();
        messageInfoMap.put("aname", currentUsername);
        messageInfoMap.put("bmessage", message);
        messageInfoMap.put("cdate", currentDate);
        messageInfoMap.put("dtime", currentTime);
      groupMessageKeyRef.updateChildren(messageInfoMap);

    }
  }

  private void DisplayMessages(DataSnapshot dataSnapshot) {
    Iterator iterator = dataSnapshot.getChildren().iterator();

    while (iterator.hasNext()) {
      String chatUaname = (String) ((DataSnapshot)iterator.next()).getValue();
      String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
      String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
      String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

      displayTextMessage.append(chatUaname + " :\n" + chatMessage + "\n" + chatDate + "    " + chatTime + "\n\n");

      scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }
  }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent a = new Intent(context, MainActivity.class);
//        startActivity(a);
//        finish();
//    }
}
