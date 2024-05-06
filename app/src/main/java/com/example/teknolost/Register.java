package com.example.teknolost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {


    private EditText user,pass;
    private Button btnRegister;
    private TextView txtRedirect;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        txtRedirect = findViewById(R.id.btnRedirectLogin);
        btnRegister = findViewById(R.id.btnReg);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usern = user.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if(usern.isEmpty()){
                    user.setError("Email cannot be empty");
                }

                if(password.isEmpty()){
                    pass.setError("Password cannot be empty");
                } else{
                    mAuth.createUserWithEmailAndPassword(usern,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "Register Succesfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                            } else {
                                Toast.makeText(Register.this, "Register Not Succes" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                txtRedirect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Register.this, Login.class));
                    }
                });

            }
        });



        txtRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }
}