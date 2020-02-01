package com.byandev.clonewhatsapp.MenusAct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byandev.clonewhatsapp.MainActivity;
import com.byandev.clonewhatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

  private Button updateAccount;
  private EditText etUserName, etStatus;
  private LinearLayout ll1;
  private CircleImageView userImage;
  private Toolbar toolbar;
  private String currentuserID;
  private FirebaseAuth auth;
  private DatabaseReference reference;
  private Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    context = this;

    auth = FirebaseAuth.getInstance();
    currentuserID = auth.getCurrentUser().getUid();
    reference = FirebaseDatabase.getInstance().getReference();

    initialize();

    ll1.setVisibility(View.GONE);

    listener();

    retriveUserInformation();
  }


  private void initialize() {
    updateAccount = findViewById(R.id.btnUpdateProfile);
    etUserName = findViewById(R.id.etUname);
    etStatus = findViewById(R.id.etStatus);
    userImage = findViewById(R.id.imgeProfile);
    ll1 = findViewById(R.id.ll1);
    toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Profile");
  }

  private void listener() {
    updateAccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateAccMethod();
      }
    });
  }

  private void updateAccMethod() {
    String userName = etUserName.getText().toString();
    String status = etStatus.getText().toString();
    if (TextUtils.isEmpty(userName)) {
      Toast.makeText(context, "Please write your username...", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(status)) {
      Toast.makeText(context, "Please write your status...", Toast.LENGTH_SHORT).show();
    } else {
      HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("uid", currentuserID);
        profileMap.put("uname", userName );
        profileMap.put("status", status);
      reference.child("users").child(currentuserID).setValue(profileMap)
          .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if (task.isSuccessful()) {
                Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                sendUserToMainActivity();
              } else {
                String message = task.getException().toString();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
              }
            }
          });
    }
  }


  private void retriveUserInformation() {
    reference.child("users").child(currentuserID)
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if ((dataSnapshot.exists()) &&
                (dataSnapshot.hasChild("uname") &&
                    (dataSnapshot.hasChild("image")))) {
              String retriveUserName = dataSnapshot.child("uname").getValue().toString();
              String retriveStatus = dataSnapshot.child("status").getValue().toString();
              String retriveProfileImage = dataSnapshot.child("image").getValue().toString();

              etUserName.setText(retriveUserName);
              etStatus.setText(retriveStatus);


            } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("uname"))) {
              String retriveUserName = dataSnapshot.child("uname").getValue().toString();
              String retriveStatus = dataSnapshot.child("status").getValue().toString();

              etUserName.setText(retriveUserName);
              etStatus.setText(retriveStatus);

            } else {
              ll1.setVisibility(View.VISIBLE);
              Toast.makeText(context, "Please set & update your profile information", Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void sendUserToMainActivity() {
    Intent a = new Intent(context, MainActivity.class);
    a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(a);
    finish();
  }


}
