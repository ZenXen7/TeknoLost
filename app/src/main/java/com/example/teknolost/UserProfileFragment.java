package com.example.teknolost;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileFragment extends Fragment {

    private EditText upName, upEmail;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        upName = view.findViewById(R.id.upName);
        upEmail = view.findViewById(R.id.upEmail);
        Button btnSave = view.findViewById(R.id.updateUserProfile);

        // Get current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get reference to the user's node in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Retrieve user information from the database and set as hint in EditText fields
        userRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String fullname = dataSnapshot.child("fullname").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                upName.setHint(fullname);
                upEmail.setHint(email);
            }
        });

        // Save changes button logic
        btnSave.setOnClickListener(v -> {
            String newName = upName.getText().toString().trim();
            String newEmail = upEmail.getText().toString().trim();

            // Show progress dialog
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Updating");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Retrieve current user information from the database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String currentName = dataSnapshot.child("fullname").getValue(String.class);
                        String currentEmail = dataSnapshot.child("email").getValue(String.class);

                        // Update only the fields that have been modified
                        if (!newName.isEmpty() && !newName.equals(currentName)) {
                            userRef.child("fullname").setValue(newName);
                            upName.setHint(newName);  // Update hint
                        }

                        if (!newEmail.isEmpty() && !newEmail.equals(currentEmail)) {
                            userRef.child("email").setValue(newEmail);
                            upEmail.setHint(newEmail);  // Update hint
                        }

                        upName.setText("");
                        upEmail.setText("");


                        // Dismiss progress dialog
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    progressDialog.dismiss();  // Dismiss progress dialog in case of error
                }
            });
        });




        return view;
    }
}
