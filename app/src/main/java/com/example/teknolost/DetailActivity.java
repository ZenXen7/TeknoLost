package com.example.teknolost;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private TextView dataTitle, dataDesc, dataLang, dataDate;
    private ImageView itemImage;
    private ImageButton backButton;
    private Button btnRequest;
    private String currentUserId;
    private String itemUploaderId;
    private String itemId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);

        dataTitle = findViewById(R.id.ItemName);
        dataDesc = findViewById(R.id.itemDescription);
        dataLang = findViewById(R.id.Location);
        dataDate = findViewById(R.id.itemDate);
        itemImage = findViewById(R.id.itemImage);
        backButton = findViewById(R.id.backButton);
        btnRequest = findViewById(R.id.btnRequest);

        Items item = (Items) getIntent().getSerializableExtra("item_data");
        itemId = item.getItemId();
        itemUploaderId = item.getUserId();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        if (currentUserId != null && itemUploaderId != null && currentUserId.equals(itemUploaderId)) {
            btnRequest.setEnabled(false);
            btnRequest.setVisibility(View.INVISIBLE);
        }

        if (itemId == null) {
            Toast.makeText(DetailActivity.this, "Item ID is null.", Toast.LENGTH_SHORT).show();
        }

        if (item != null) {
            dataTitle.setText(item.getDataTitle());
            dataDesc.setText(item.getDataDesc());
            dataLang.setText("Location: " + item.getDataLang());
            dataDate.setText("Date: " + item.getDataDate());

            // Load the image into the ImageView using Glide
            Glide.with(this).load(item.getDataImage()).into(itemImage);
        }

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemId == null) {
                    Toast.makeText(DetailActivity.this, "Item ID is null.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Show confirmation dialog
                showConfirmationDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showConfirmationDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_confirm_sentrequest, null);

        final EditText editTextDescription = dialogView.findViewById(R.id.edittext_description);
        final CheckBox checkboxConfirm = dialogView.findViewById(R.id.checkbox_confirm);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Confirm Request");
        builder.setView(dialogView);
        builder.setPositiveButton("Confirm", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();

        // Override the positive button click to check the checkbox state and input field
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!checkboxConfirm.isChecked()) {
                            Toast.makeText(DetailActivity.this, "Please check the box to confirm.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String description = editTextDescription.getText().toString().trim();
                        if (description.isEmpty()) {
                            Toast.makeText(DetailActivity.this, "Please provide a brief description of the item.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Proceed with the request and send the description
                        sendRequest(description);
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void sendRequest(String description) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Create a new requestClaimId
        String requestClaimId = databaseReference.child("RequestClaims").push().getKey();

        if (requestClaimId == null) {
            Toast.makeText(DetailActivity.this, "Failed to generate request ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the claimant's full name from the Firebase Realtime Database
        databaseReference.child("users").child(currentUserId).child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String fullname = dataSnapshot.getValue(String.class);

                        // Create a request object with the claimant's name
                        Map<String, Object> requestClaim = new HashMap<>();
                        requestClaim.put("requestId", requestClaimId);
                        requestClaim.put("itemId", itemId);
                        requestClaim.put("claimantId", currentUserId);
                        requestClaim.put("claimantName", fullname);
                        requestClaim.put("itemUploaderId", itemUploaderId);
                        requestClaim.put("briefDescription", description);
                        requestClaim.put("status", "Request");

                        // Create a new node for the claim request
                        databaseReference.child("RequestClaims").child(requestClaimId).setValue(requestClaim)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Update the status of the item
                                            databaseReference.child("Items").child(itemId).child("status").setValue("Request")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Send notification to the item uploader and claimant
                                                                String currentDate = getCurrentDate();
                                                                sendNotification(itemUploaderId, "Claim Request", "Someone has requested to claim the item you uploaded.", requestClaimId, currentDate, fullname);
                                                                sendNotification(currentUserId, "Successfully sent a request", "Your claim request has been sent successfully.", requestClaimId, currentDate, fullname);
                                                                finish();
                                                                Toast.makeText(DetailActivity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(DetailActivity.this, "Failed to update item status.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(DetailActivity.this, "Failed to send request.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(DetailActivity.this, "Fullname data does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, "Error getting fullname data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void sendNotification(String userId, String title, String message, String requestClaimId, String date,String claimant) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        String notificationId = notificationRef.push().getKey();
        if (notificationId == null) {
            Toast.makeText(DetailActivity.this, "Failed to generate notification ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        Notification notification = new Notification(title, message, date, requestClaimId,claimant);
        notificationRef.child(notificationId).setValue(notification)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Notification sent successfully
                        } else {
                            Toast.makeText(DetailActivity.this, "Failed to send notification.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
