package com.example.teknolost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText email,pass;
    private Button btnLogin;
    private TextView txtRedirectReg,message;
    private FirebaseAuth mAuth;
    static String currentUser;
    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        logo = findViewById(R.id.logobackground);
        message = findViewById(R.id.messagebox);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginemail);
        pass = findViewById(R.id.loginpassword);
        txtRedirectReg = findViewById(R.id.goRegister);
        btnLogin = findViewById(R.id.btnLogin);

        logo.setAlpha(0.3f);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logEmail = email.getText().toString().trim();
                String logPass = pass.getText().toString().trim();

                if(!logEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(logEmail).matches()){
                    if(!logPass.isEmpty()){
                        mAuth.signInWithEmailAndPassword(logEmail,logPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                message.setText("Login Successfully");
                                Toast.makeText(Login.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                currentUser = String.valueOf(user);
                                System.out.println(currentUser);
                                startActivity(new Intent(Login.this,Main.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                message.setText("Invalid Credentials");
                                Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        message.setText("Password cannot be empty");
                        pass.setError("Password cannot be empty");
                    }
                } else if(logEmail.isEmpty()){
                    message.setText("Email cannot be empty");
                    email.setError("Email cannot be empty");
                } else {
                    message.setText("Invalid Email");
                    email.setError("Invalid Email");
                }
            }
        });

        txtRedirectReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }
}