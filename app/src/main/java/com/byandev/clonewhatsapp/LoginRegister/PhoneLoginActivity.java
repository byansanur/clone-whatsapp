package com.byandev.clonewhatsapp.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byandev.clonewhatsapp.MainActivity;
import com.byandev.clonewhatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

  private Button btSendRequestOtp, btVerifyOtp;
  private EditText etNohp, etOtp;
  private TextView textJudul, tvToLogin;

  private Context context;

  private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

  private String mVerificationId;
  private PhoneAuthProvider.ForceResendingToken mResendToken;
  private FirebaseAuth mAuth;

  private ProgressDialog loading;

  @Override
  public void setSupportProgressBarIndeterminate(boolean indeterminate) {
    super.setSupportProgressBarIndeterminate(indeterminate);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_phone_login);

    context = this;
    loading = new ProgressDialog(context);

    mAuth = FirebaseAuth.getInstance();

    btSendRequestOtp = findViewById(R.id.btRequestOtp);
    btVerifyOtp = findViewById(R.id.btSendVerify);
    etNohp = findViewById(R.id.etPhoneInput);
    etOtp = findViewById(R.id.etVerifyCode);
    textJudul = findViewById(R.id.tvActivity);
    tvToLogin = findViewById(R.id.tvToLogin);
    listener();


    btSendRequestOtp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {


        String pNumber = etNohp.getText().toString();
        if (TextUtils.isEmpty(pNumber)) {
          Toast.makeText(context, "Please enter your phone number first...", Toast.LENGTH_SHORT).show();
        } else {
          loading.setTitle("Phone verification");
          loading.setMessage("please wait, while we are authenticating your phone number...");
          loading.setCanceledOnTouchOutside(false);
          loading.show();
          PhoneAuthProvider.getInstance().verifyPhoneNumber(
              pNumber,        // Phone number to verify
              60,                 // Timeout duration
              TimeUnit.SECONDS,   // Unit of timeout
              PhoneLoginActivity.this,               // Activity (for callback binding)
              callbacks         // OnVerificationStateChangedCallbacks
          );
        }
      }
    });

    btVerifyOtp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        btSendRequestOtp.setVisibility(View.INVISIBLE);
        etNohp.setVisibility(View.INVISIBLE);

        String otpCode = etOtp.getText().toString();
        if (TextUtils.isEmpty(otpCode)) {
          Toast.makeText(context, "Write otp", Toast.LENGTH_SHORT).show();
        } else {
          loading.setTitle("Verification Code");
          loading.setMessage("please wait, while we are verification code...");
          loading.setCanceledOnTouchOutside(false);
          loading.show();
          PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
          signInWithPhoneAuthCredential(credential);
        }
      }
    });

    callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      @Override
      public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        signInWithPhoneAuthCredential(phoneAuthCredential);
      }

      @Override
      public void onVerificationFailed(@NonNull FirebaseException e) {
        loading.dismiss();
        Toast.makeText(context, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_SHORT).show();

        btSendRequestOtp.setVisibility(View.VISIBLE);
        etNohp.setVisibility(View.VISIBLE);
        btVerifyOtp.setVisibility(View.GONE);
        etOtp.setVisibility(View.GONE);

      }
      @SuppressLint("SetTextI18n")
      public void onCodeSent(@NonNull String verificationId,
                             @NonNull PhoneAuthProvider.ForceResendingToken token) {

        mVerificationId = verificationId;
        mResendToken = token;

        loading.dismiss();
        Toast.makeText(context, "Code send...", Toast.LENGTH_SHORT).show();

        btSendRequestOtp.setVisibility(View.GONE);
        etNohp.setVisibility(View.GONE);
        btVerifyOtp.setVisibility(View.VISIBLE);
        etOtp.setVisibility(View.VISIBLE);
        textJudul.setText("Input your code here...");

      }
    };

  }

  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              loading.dismiss();
              Toast.makeText(context, "Login successfully", Toast.LENGTH_SHORT).show();
              sendUserToMainActivity();
            } else {
              String err = Objects.requireNonNull(task.getException()).toString();
              Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  private void sendUserToMainActivity() {
    Intent a = new Intent(context, MainActivity.class);
    startActivity(a);
    finish();
  }

  private void listener() {

    tvToLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(context, LoginActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
      }
    });
  }

  @Override
  public void onBackPressed() {
//    finish();
  }
}
