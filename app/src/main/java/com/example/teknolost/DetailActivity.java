package com.example.teknolost;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity extends AppCompatActivity {

    private TextView dataTitle, dataDesc, dataLang, dataDate;
    private ImageView itemImage;

    private ImageButton backButton;

    private Button btnRequest;
    private String currentUserId;
    private String itemUploaderId;
    private String itemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dataTitle = findViewById(R.id.ItemName);
        dataDesc = findViewById(R.id.itemDescription);
        dataLang = findViewById(R.id.Location);
        dataDate = findViewById(R.id.itemDate);
        itemImage = findViewById(R.id.itemImage);
        backButton = findViewById(R.id.backButton);

        btnRequest = findViewById(R.id.btnRequest);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        itemId = getIntent().getStringExtra("itemId");


        Items itemss = (Items) getIntent().getSerializableExtra("item_data");
        if (itemss != null) {
            itemUploaderId = itemss.getUserId();
        }



//        if (currentUserId != null && itemUploaderId != null && currentUserId.equals(itemUploaderId)) {
//            btnRequest.setEnabled(false);
//            // or btnRequest.setVisibility(View.GONE); to hide the button
//        }




        // Set up click listener for the Request Claim button
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserId != null && itemUploaderId != null && currentUserId.equals(itemUploaderId)) {
                    Toast.makeText(DetailActivity.this, "You cannot request a claim for your own item.", Toast.LENGTH_SHORT).show();

                }

                // Check if itemId is not null
                if (itemId == null) {
                    Toast.makeText(DetailActivity.this, "Item ID is null.", Toast.LENGTH_SHORT).show();
                    return; // Exit onClick method
                }

                addClaimToDatabase();
            }


        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        Items item = (Items) getIntent().getSerializableExtra("item_data");

        if (item != null) {
            dataTitle.setText(item.getDataTitle());
            dataDesc.setText(item.getDataDesc());
            dataLang.setText(item.getDataLang());
            dataDate.setText(item.getDataDate());

            // Load the image into the ImageView using Glide
            Glide.with(this).load(item.getDataImage()).into(itemImage);
        }
    }

    private void addClaimToDatabase() {
        DatabaseReference claimsRef = FirebaseDatabase.getInstance().getReference().child("claims");

        // Generate a new unique ID for the claim
        String claimId = claimsRef.push().getKey();

        // Create a new claim object
        Claim claim = new Claim(currentUserId, System.currentTimeMillis(), "pending");

        // Store the claim under "claims/itemId/claimId"
        claimsRef.child(itemId).child(claimId).setValue(claim)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DetailActivity.this, "Claim requested successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailActivity.this, "Failed to request claim. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
