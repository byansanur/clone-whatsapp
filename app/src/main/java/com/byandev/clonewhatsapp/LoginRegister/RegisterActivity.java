package com.byandev.clonewhatsapp.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byandev.clonewhatsapp.MainActivity;
import com.byandev.clonewhatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

  private Context context;
  private Button btRegister;
  private TextView tvLogin;
  private EditText etEmail, etPassword;

  private FirebaseAuth auth;
  private DatabaseReference reference;

  private ProgressDialog loading;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    context = this;

    auth = FirebaseAuth.getInstance();
    reference = FirebaseDatabase.getInstance().getReference();

    initFields();

  }

  private void initFields() {
    btRegister = findViewById(R.id.btRegister);
    tvLogin = findViewById(R.id.tvLogin);
    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    loading = new ProgressDialog(context);
    listener();
  }

  private void listener() {
    tvLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendUserToLoginActivity();
      }
    });
    btRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createAccount();
      }
    });
  }

  private void sendUserToLoginActivity() {
    Intent s = new Intent(context, LoginActivity.class);
    startActivity(s);
    finish();
  }

  private void sendUserToMainActivity() {
    Intent a = new Intent(context, MainActivity.class);
    a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(a);
    finish();
  }

  private void createAccount() {
    String email = etEmail.getText().toString();
    String password = etPassword.getText().toString();

    if (TextUtils.isEmpty(email)) {
      Toast.makeText(context, "input your email", Toast.LENGTH_SHORT).show();
      etEmail.setError("Input your email");
    } else if (TextUtils.isEmpty(password)) {
      Toast.makeText(context, "input your password", Toast.LENGTH_SHORT).show();
      etPassword.setError("Input your password");
    } else {
      loading.setTitle("Creating new Account");
      loading.setMessage("Please wait, while we are creating your account");
      loading.setCanceledOnTouchOutside(true);
      loading.show();
      auth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {

                  String deviceToken = FirebaseInstanceId.getInstance().getToken();

                String currentUserId = auth.getCurrentUser().getUid();
                reference.child("users").child(currentUserId).setValue("" );

                reference.child("users").child(currentUserId).child("device_token")
                    .setValue(deviceToken);

                loading.dismiss();
                Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                sendUserToMainActivity();
              }
              else {
                String message = task.getException().toString();
                Toast.makeText(context, "Error : "+message, Toast.LENGTH_SHORT).show();
                loading.dismiss();
              }
            }
          });
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }
}
