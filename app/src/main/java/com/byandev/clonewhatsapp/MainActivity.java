package com.byandev.clonewhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.byandev.clonewhatsapp.Find.FindFriendsActivity;
import com.byandev.clonewhatsapp.LoginRegister.LoginActivity;
import com.byandev.clonewhatsapp.MenusAct.SettingsActivity;
import com.byandev.clonewhatsapp.Pager.TabsAccessorAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

  private Toolbar toolbar;
  private ViewPager viewPager;
  private TabLayout tabLayout;
  private TabsAccessorAdapter tabsAccessorAdapter;

  private FirebaseUser currentUser;
  private FirebaseAuth auth;
  private DatabaseReference reference;

  private Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    context = this;

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("WhatsApp");

    auth = FirebaseAuth.getInstance();
    currentUser = auth.getCurrentUser();
    reference = FirebaseDatabase.getInstance().getReference();

    viewPager = findViewById(R.id.frame_container);
    tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
    viewPager.setAdapter(tabsAccessorAdapter);

    tabLayout = findViewById(R.id.tabMode);
    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (currentUser == null) {
      SendUserToLoginActivity();
    } else  {
      VerifyUserExistence();
    }
  }

  private void VerifyUserExistence() {
    String currentUserId = auth.getCurrentUser().getUid();
    reference.child("users").child(currentUserId).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("aname").exists()) {
//          Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show();
        } else {
          SendUserToSettingsActivity();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void SendUserToLoginActivity() {
    Intent i = new Intent(context, LoginActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);
    finish();
  }

  private void SendUserToSettingsActivity() {
    Intent i = new Intent(context, SettingsActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);
    finish();
  }

    private void SendUserToFiendFriendsActivity() {
        Intent i = new Intent(context, FindFriendsActivity.class);
        startActivity(i);
    }

  private void requestNewGroup() {
    AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.AlertDialog);
    ad.setTitle("Enter Group Name :");
    final EditText groupName = new EditText(context);
    groupName.setHint("e.g Android dev indonesian");
    groupName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    ad.setView(groupName);
    ad.setPositiveButton("Create", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String groupNames = groupName.getText().toString();
        if (TextUtils.isEmpty(groupNames)) {
          Toast.makeText(context, "Input your name group", Toast.LENGTH_SHORT).show();
        } else {
          createNewGroup(groupNames);
        }
      }
    });
    ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    ad.show();
  }

  private void createNewGroup(final String groupNames) {
    reference.child("groups").child(groupNames).setValue("")
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              Toast.makeText(context, groupNames +" group is created", Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    super.onOptionsItemSelected(item);
    if (item.getItemId() == R.id.signOut) {
      auth.signOut();
      SendUserToLoginActivity();
    }
    if (item.getItemId() == R.id.settingsMenu) {
      SendUserToSettingsActivity();
    }
    if (item.getItemId() == R.id.findFriends) {
        SendUserToFiendFriendsActivity();
    }
    if (item.getItemId() == R.id.createGrpup) {
      requestNewGroup();
    }
    return true;
  }

}
