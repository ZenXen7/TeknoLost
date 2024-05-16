package com.example.teknolost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText user, pass, fname, cpass;
    private Button btnRegister;
    private TextView txtRedirect;
    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        cpass = findViewById(R.id.confirmpass);
        fname = findViewById(R.id.fullname);
        txtRedirect = findViewById(R.id.btnRedirectLogin);
        btnRegister = findViewById(R.id.btnReg);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
        // Reload UI
    }

    private void startSignIn() {
        String fullname = fname.getText().toString();
        String emaill = user.getText().toString();
        String pword = pass.getText().toString();
        String cpword = cpass.getText().toString();

        if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(emaill) || TextUtils.isEmpty(pword) || TextUtils.isEmpty(cpword)) {
            Toast.makeText(Register.this, "Empty Fields. Please input to register", Toast.LENGTH_SHORT).show();
        } else if (!pword.equals(cpword)) {
            Toast.makeText(Register.this, "Passwords do not match. Please try again", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(emaill, pword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    signUp(user.getUid(), fullname, emaill, pword, cpword);
                                    Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "createUserWithEmail:success");
                                    updateUI(user);
                                    startActivity(new Intent(Register.this, Login.class));
                                    finish();
                                }
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        // Update UI after sign up
    }

    public void signUp(String userId, String name, String email, String password, String cpassword) {
        Users user = new Users(userId, name, email, password, cpassword);
        myRef.child("users").child(userId).setValue(user);
    }
}