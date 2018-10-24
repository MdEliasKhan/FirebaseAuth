package tech.iotait.firebaseauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText etEmail,etPass,etConfirmPass;
    private Button btnCreateAcc;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        // firebase auth insalization
        firebaseAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnCreateAcc = findViewById(R.id.btnCreateAcc);

        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                String cPass = etConfirmPass.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty() || cPass.isEmpty()){
                    Toast.makeText(UserRegistrationActivity.this, "Fill up all the fields", Toast.LENGTH_SHORT).show();
                }else if(!pass.equals(cPass)){
                    Toast.makeText(UserRegistrationActivity.this, "Password not matched", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(UserRegistrationActivity.this, "Registration Done Successfully", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(UserRegistrationActivity.this,MainActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(loginIntent);
                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(UserRegistrationActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
