package com.example.teknolost;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemDetailsActivity extends AppCompatActivity {
    private TextView itemName, claimant, briefDescription,retrieveLocation;
    private ImageButton btnBack;
    private ImageView itemImage;
    private Button confirmButton;

    private String requestId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemName = findViewById(R.id.previewItemName);
        claimant = findViewById(R.id.previewClaimantId);
        briefDescription = findViewById(R.id.previewbriefDescription);
        retrieveLocation = findViewById(R.id.location);
        itemImage = findViewById(R.id.previewItemImage);
        confirmButton = findViewById(R.id.btnConfirm);

        requestId = getIntent().getStringExtra("requestId");
        if (requestId != null) {
            Log.d("ItemDetailsActivity", "Request ID: " + requestId);
            fetchRequestDetails(requestId);
        } else {
            Log.e("ItemDetailsActivity", "Request ID is null");
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
    }

    private void fetchRequestDetails(String requestId) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("RequestClaims").child(requestId);
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String briefDescriptionText = snapshot.child("briefDescription").getValue(String.class);
                    String claimantId = snapshot.child("claimantId").getValue(String.class);
                    String itemId = snapshot.child("itemId").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    String retrievalLocation = snapshot.child("retrievalLocation").getValue(String.class);

                    briefDescription.setText("Message: " + briefDescriptionText);
                    claimant.setText("Claimant: " + claimantId);
                    fetchClaimantDetails(claimantId);
                    fetchItemDetails(itemId);

                    if (claimantId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        // Hide the confirm button
                        confirmButton.setVisibility(View.GONE);
                    }

                    // Check the status and update the visibility of retrievalLocation
                    if ("Confirmed".equals(status)) {
                        // Show the retrievalLocation
                        briefDescription.setVisibility(View.INVISIBLE);
                        retrieveLocation.setVisibility(View.VISIBLE);
                        retrieveLocation.setText("Retrieval Location: " + retrievalLocation);
                    } else {
                        // Hide the retrievalLocation
                        retrieveLocation.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ItemDetailsActivity.this, "Request not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ItemDetailsActivity.this, "Error fetching request details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchClaimantDetails(String claimantId) {
        DatabaseReference claimantRef = FirebaseDatabase.getInstance().getReference("Users");
        claimantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean claimantFound = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.child("id").getValue(String.class);
                    if (userId != null && userId.equals(claimantId)) {
                        String claimantName = userSnapshot.child("fullname").getValue(String.class);
                        Log.d("ItemDetailsActivity", "Claimant Name: " + claimantName);
                        claimant.setText("Claimant: " + claimantName);
                        claimantFound = true;
                        break;
                    }
                }

                if (!claimantFound) {
                    Log.e("ItemDetailsActivity", "Claimant not found for ID: " + claimantId);
                    Toast.makeText(ItemDetailsActivity.this, "Claimant not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ItemDetailsActivity.this, "Error fetching claimant details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchItemDetails(String itemId) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Items").child(itemId);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("dataTitle").getValue(String.class);
                    String description = snapshot.child("dataDesc").getValue(String.class);
                    String imageUrl = snapshot.child("dataImage").getValue(String.class);
                    itemName.setText(title);

                    // Load the image using Glide or any other image loading library
                    Glide.with(ItemDetailsActivity.this).load(imageUrl).into(itemImage);
                } else {
                    Toast.makeText(ItemDetailsActivity.this, "Item not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ItemDetailsActivity.this, "Error fetching item details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_confirm_request, null);

        final Spinner spinnerLocation = dialogView.findViewById(R.id.spinner_location);
        final CheckBox checkboxConfirm = dialogView.findViewById(R.id.checkbox_confirm);

        // Set up the Spinner with retrieval locations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.retrieval_locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Request");
        builder.setView(dialogView);
        builder.setPositiveButton("Confirm", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!checkboxConfirm.isChecked()) {
                            Toast.makeText(ItemDetailsActivity.this, "Please check the box to confirm.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String selectedLocation = spinnerLocation.getSelectedItem().toString();
                        if (selectedLocation.isEmpty()) {
                            Toast.makeText(ItemDetailsActivity.this, "Please select a retrieval location.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Proceed with the request and send the selected location
                        confirmRequest(selectedLocation,requestId);
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }


    private void sendConfirmationNotification(String claimantId, String message) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications").child(claimantId);

        String notificationId = notificationRef.push().getKey();
        if (notificationId == null) {
            Toast.makeText(ItemDetailsActivity.this, "Failed to generate notification ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        Notification notification = new Notification("Request Confirmed", message, String.valueOf(System.currentTimeMillis()), requestId);
        notificationRef.child(notificationId).setValue(notification)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Toast.makeText(ItemDetailsActivity.this, "Confirmation notification sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ItemDetailsActivity.this, "Failed to send confirmation notification.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void confirmRequest(String selectedLocation, String requestId) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("RequestClaims").child(requestId);

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(ItemDetailsActivity.this);
        progressDialog.setTitle("Confirming Request");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Fetch the claimantId using requestId
        requestRef.child("claimantId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String claimantId = snapshot.getValue(String.class);

                    // Update request status and retrieval location
                    requestRef.child("status").setValue("Confirmed");
                    requestRef.child("retrievalLocation").setValue(selectedLocation)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Send confirmation notification to the claimant
                                        sendConfirmationNotification(claimantId, "Your request has been confirmed.");

                                        // Delay dismissal of progress dialog for 3 seconds
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                Toast.makeText(ItemDetailsActivity.this, "Request confirmed with location: " + selectedLocation, Toast.LENGTH_SHORT).show();
                                            }
                                        }, 3000); // 3 seconds delay
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(ItemDetailsActivity.this, "Failed to confirm request. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ItemDetailsActivity.this, "Claimant ID not found for request: " + requestId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ItemDetailsActivity.this, "Error fetching claimant ID: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }






}
