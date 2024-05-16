package com.example.teknolost;



import android.os.Bundle;

<<<<<<< HEAD
import androidx.fragment.app.Fragment;

=======
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
>>>>>>> 09c2c91cd7d5941aaa004da7168b70d3dbf33af3
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

<<<<<<< HEAD
=======
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

>>>>>>> 09c2c91cd7d5941aaa004da7168b70d3dbf33af3

public class HomeFragment extends Fragment {

    private TextView userheading;

<<<<<<< HEAD

=======
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
>>>>>>> 09c2c91cd7d5941aaa004da7168b70d3dbf33af3
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        userheading = (TextView) v.findViewById(R.id.userHomeTag);

<<<<<<< HEAD
=======
        String currUserId = user.getUid();
        Log.d("firebase", "Accessing path: users/" + currUserId + "/fullname");

//        myRef.child("users").child(currUserId).child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                }
//            }
//        });

        myRef.child("users").child(currUserId).child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // Retrieve the fullname value
                        String fullname = dataSnapshot.getValue(String.class);
                        userheading.setText(fullname);
                        Log.d("firebase", "User fullname: " + fullname);

                        // Now you can use 'fullname' as needed (e.g., display it, store it, etc.)
                    } else {
                        Log.d("firebase", "Fullname data does not exist");
                    }
                } else {
                    Log.e("firebase", "Error getting fullname data", task.getException());
                }
            }
        });



>>>>>>> 09c2c91cd7d5941aaa004da7168b70d3dbf33af3


        return v;


    }
}