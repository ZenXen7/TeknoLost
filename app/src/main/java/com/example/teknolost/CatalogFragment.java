package com.example.teknolost;

import static android.app.Activity.RESULT_OK;
import static android.app.ProgressDialog.show;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.Result;


public class CatalogFragment extends Fragment {
    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic, uploadDesc, uploadLang,uploadDate;
    String imageURL;
    Uri uri;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public String currentUserId = user.getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_catalog, container, false);

        uploadImage = (ImageView) v.findViewById(R.id.uploadImage);
        uploadDesc = (EditText) v.findViewById(R.id.uploadDesc);
        uploadTopic = (EditText) v.findViewById(R.id.uploadTopic);
        uploadLang = (EditText) v.findViewById(R.id.uploadLand);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        uploadDate =  (EditText) v.findViewById(R.id.uploadDate);

        uploadDate.setOnClickListener(view -> showDatePickerDialog());



        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();


            }
        });

        return v;


    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    // Format the date and set it in the EditText
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    uploadDate.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }


    private void saveData() {


        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Items")
                .child(uri.getLastPathSegment());
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();


                progressDialog.dismiss();
//                Toast.makeText(getContext(), "Upload Succesfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void uploadData() {
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String lang = uploadLang.getText().toString();
        String date = uploadDate.getText().toString();
        Data dataClass = new Data(title, desc, lang, imageURL, date);

        // Generate a unique key for each new entry
        String key = FirebaseDatabase.getInstance().getReference("Items").push().getKey();

        // Create a HashMap to store the data
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", currentUserId); // Include userId in the data
        dataMap.put("dataDesc", desc);
        dataMap.put("dataImage", imageURL);
        dataMap.put("dataLang", lang);
        dataMap.put("dataTitle", title);
        dataMap.put("dataDate", date);
        dataMap.put("status", "pending");
        dataMap.put("itemId", key);// Set status to default

        // Store the data in the database under the unique key
        FirebaseDatabase.getInstance().getReference("Items")
                .child(key)
                .setValue(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(),    "Success", Toast.LENGTH_SHORT).show();
                            // Clear input fields after successful upload
                            uploadTopic.setText("");
                            uploadDesc.setText("");
                            uploadLang.setText("");
                            uploadDate.setText("");

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }







}