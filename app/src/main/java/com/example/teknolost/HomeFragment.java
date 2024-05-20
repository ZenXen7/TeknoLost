package com.example.teknolost;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements ItemAdapter.OnItemClickListener {

    private TextView userheading;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ArrayList<Items> itemsList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
//
//        userheading = v.findViewById(R.id.userHomeTag);
        recyclerView = v.findViewById(R.id.itemRecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemsList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), itemsList, this);
        recyclerView.setAdapter(itemAdapter);

        String currUserId = user.getUid();
        Log.d("firebase", "Accessing path: users/" + currUserId + "/fullname");

//        myRef.child("users").child(currUserId).child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DataSnapshot dataSnapshot = task.getResult();
//                    if (dataSnapshot.exists()) {
//                        String fullname = dataSnapshot.getValue(String.class);
//                        userheading.setText(fullname);
//                        Log.d("firebase", "User fullname: " + fullname);
//                    } else {
//                        Log.d("firebase", "Fullname data does not exist");
//                    }
//                } else {
//                    Log.e("firebase", "Error getting fullname data", task.getException());
//                }
//            }
//        });

        myRef.child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Items item = dataSnapshot.getValue(Items.class);
                    if (item.getStatus() != null && item.getStatus().equals("pending")) {
                        itemsList.add(item);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("firebase", "Error getting items data", error.toException());
            }
        });

        return v;
    }

    @Override
    public void onItemClick(int position) {
        Items clickedItem = itemsList.get(position);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("item_data", clickedItem);
        startActivity(intent);
    }
}
