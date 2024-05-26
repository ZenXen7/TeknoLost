package com.example.teknolost;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileFragment extends Fragment {


    private TextView upName, upEmail;
    private boolean isPrivacyPolicyVisible = false;
    private boolean isFeedbackVisible = false;
    private boolean isAboutUsVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        upName = view.findViewById(R.id.upName);
        upEmail = view.findViewById(R.id.upEmail);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String curruser = currentUser.getUid();

            usersRef.child(curruser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullname").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);


                        upName.setText(fullName);
                        upEmail.setText(email);
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        view.findViewById(R.id.privacy_policy_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePrivacyPolicy(v);
            }
        });

        view.findViewById(R.id.feedback_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFeedback(v);
            }
        });

        view.findViewById(R.id.about_us_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAboutUs(v);
            }
        });

        return view;
    }

    private void togglePrivacyPolicy(View view) {
        TextView content = getView().findViewById(R.id.privacy_policy_content);
        isPrivacyPolicyVisible = !isPrivacyPolicyVisible;
        content.setVisibility(isPrivacyPolicyVisible ? View.VISIBLE : View.GONE);
        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, isPrivacyPolicyVisible ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down, 0);
    }

    private void toggleFeedback(View view) {
        TextView content = getView().findViewById(R.id.feedback_content);
        isFeedbackVisible = !isFeedbackVisible;
        content.setVisibility(isFeedbackVisible ? View.VISIBLE : View.GONE);
        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, isFeedbackVisible ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down, 0);
    }

    private void toggleAboutUs(View view) {
        TextView content = getView().findViewById(R.id.about_us_content);
        isAboutUsVisible = !isAboutUsVisible;
        content.setVisibility(isAboutUsVisible ? View.VISIBLE : View.GONE);
        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, isAboutUsVisible ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down, 0);
    }
}
