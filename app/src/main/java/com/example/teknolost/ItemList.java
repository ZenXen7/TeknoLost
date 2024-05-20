package com.example.teknolost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ItemList extends AppCompatActivity implements ItemAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    ArrayList<Items> itemsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemsList, this);
        recyclerView.setAdapter(itemAdapter);

        FirebaseDatabase.getInstance().getReference().child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemsList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Items item = dataSnapshot.getValue(Items.class);
                            if (item.getStatus() != null && item.getStatus().equals("default")) {
                                itemsList.add(item);
                                item.setItemId(dataSnapshot.getKey());

                            }
                        }
                        itemAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors.
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Items clickedItem = itemsList.get(position);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("item_data", clickedItem);
        intent.putExtra("itemId", clickedItem.getItemId()); // Pass the itemId here




        startActivity(intent);
    }
}
