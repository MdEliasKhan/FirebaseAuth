package tech.iotait.firebaseauth;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static android.media.MediaExtractor.MetricsConstants.FORMAT;

public class UserRegWithPhnActivity extends AppCompatActivity {

    private EditText etPhoneNum,etVarificationCode;
    private Button btnSendVarification;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private int btnType = 0;

    private TextView tvWaitingTime;

    private Button btnResendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration_with_phone);

        etPhoneNum = findViewById(R.id.etPhoneNum);
        etVarificationCode = findViewById(R.id.etVarificationCode);
        btnSendVarification = findViewById(R.id.btnSendVarification);
        tvWaitingTime = findViewById(R.id.tvWaitingTime);
        btnResendCode = findViewById(R.id.btnResendCode);

        mAuth = FirebaseAuth.getInstance();


        // VARIFICATION CODE SENT BUTTON AND VERIFY CODE BUTTON
        btnSendVarification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvWaitingTime.setVisibility(View.VISIBLE);
                if(btnType == 0){
                    // this is for sending varification code to user mobile num
                    etVarificationCode.setVisibility(View.VISIBLE);
                    btnSendVarification.setEnabled(false);

                    String num = "+880" + etPhoneNum.getText().toString().trim();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            num,
                            60,
                            TimeUnit.SECONDS,
                            UserRegWithPhnActivity.this,
                            mCallbacks);


                    // coutdown for resend code
                    new CountDownTimer(45000, 1000) { // adjust the milli seconds here

                        public void onTick(long millisUntilFinished) {
                            int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                            tvWaitingTime.setText("Resend code after " + seconds + " seconds");
                        }

                        public void onFinish() {
                            tvWaitingTime.setText("Resend Code Now");
                            btnResendCode.setVisibility(View.VISIBLE);
                        }
                    }.start();
                }else{

                    // This is for varifing the verification code
                    btnSendVarification.setEnabled(false);
                    String varificationCode = etVarificationCode.getText().toString().trim();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,varificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });


        // Resend button functionality
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnResendCode.setVisibility(View.INVISIBLE);

                String num = "+880" + etPhoneNum.getText().toString().trim();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        num,
                        60,
                        TimeUnit.SECONDS,
                        UserRegWithPhnActivity.this,
                        mCallbacks);


                // coutdown for resend code
                new CountDownTimer(45000, 1000) { // adjust the milli seconds here

                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;

                        tvWaitingTime.setText("Resend code after " + seconds + " seconds");
                    }
                    public void onFinish() {
                        tvWaitingTime.setText("Resend Code Now");
                        btnResendCode.setVisibility(View.VISIBLE);
                    }
                }.start();

            }
        });

        
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(UserRegWithPhnActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                Toast.makeText(UserRegWithPhnActivity.this, "Code has been sent successfully", Toast.LENGTH_SHORT).show();
                mVerificationId = s;
                mResendToken = forceResendingToken;
                btnSendVarification.setEnabled(true);
                btnSendVarification.setText("Verify Code");
                btnType = 1;
            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(UserRegWithPhnActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(UserRegWithPhnActivity.this,HomeActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(UserRegWithPhnActivity.this, "Varification code is invalied", Toast.LENGTH_SHORT).show();
                                btnSendVarification.setEnabled(true);
                            }
                        }
                    }
                });
    }
}
