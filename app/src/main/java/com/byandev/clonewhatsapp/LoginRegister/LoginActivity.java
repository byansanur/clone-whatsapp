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
import android.widget.TextView;
import android.widget.Toast;

import com.byandev.clonewhatsapp.MainActivity;
import com.byandev.clonewhatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

//  private FirebaseUser currentUser;
  private Button btLogin, btLoginPhone;
  private EditText etEmail, etPassword;
  private TextView tvRegister, tvForget;
  private Context context;
  private FirebaseAuth auth;
  private ProgressDialog loading;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    context = this;
    auth = FirebaseAuth.getInstance();
//    currentUser = auth.getCurrentUser();

    initializeFields();
    listener();
  }

  private void initializeFields() {
    btLogin = findViewById(R.id.btLogin);
    btLoginPhone = findViewById(R.id.btLoginWithPhone);
    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    tvRegister = findViewById(R.id.tvRegister);
    tvForget = findViewById(R.id.tvForgetPassword);
    loading = new ProgressDialog(context);
  }

  private void listener() {
    tvRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent a = new Intent(context, RegisterActivity.class);
        startActivity(a);
      }
    });
    btLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        userLogin();
      }
    });
  }

  private void userLogin() {
    String email = etEmail.getText().toString();
    String password = etPassword.getText().toString();
    if (TextUtils.isEmpty(email)) {
      Toast.makeText(context, "input your email", Toast.LENGTH_SHORT).show();
      etEmail.setError("Input your email");
    } else if (TextUtils.isEmpty(password)) {
      Toast.makeText(context, "input your password", Toast.LENGTH_SHORT).show();
      etPassword.setError("Input your password");
    } else {
      loading.setTitle("Sign in");
      loading.setMessage("Please wait, while we are process to your logged in account");
      loading.setCanceledOnTouchOutside(true);
      loading.show();
      auth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
             if (task.isSuccessful()) {
               loading.dismiss();
               Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show();
               sendUserToMainActivity();
             } else {
               loading.dismiss();
               String message = task.getException().toString();
               Toast.makeText(context, "Error : "+message, Toast.LENGTH_SHORT).show();
             }
            }
          });
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

//  @Override
//  protected void onStart() {
//    super.onStart();
//    if (currentUser != null) {
//      SendUserToMainActivity();
//    }
//  }

  private void sendUserToMainActivity() {
    Intent a = new Intent(context, MainActivity.class);
    a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(a);
    finish();
  }
}
