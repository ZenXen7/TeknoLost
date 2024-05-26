package com.example.teknolost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
        Button buttonLogout  = view.findViewById(R.id.buttonLogout);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");


        // Get current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String curruser = currentUser.getUid();


        usersRef.child(curruser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullname").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    // Now you have the user's full name and email
                    // You can set them in your EditText fields or wherever needed
                    upName.setText(fullName);
                    upEmail.setText(email);
                } else {
                    // User not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();

            }
        });




        return view;
    }
}
