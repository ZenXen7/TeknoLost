package com.example.teknolost;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CatalogFragment extends Fragment {
    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic, uploadDesc, uploadLang, uploadDate;
    String imageURL;
    Uri uri;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public String currentUserId = user.getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_catalog, container, false);
        uploadImage = v.findViewById(R.id.uploadImage);
        uploadDesc = v.findViewById(R.id.descriptionEditText);
        uploadTopic = v.findViewById(R.id.itemNameEditText);
        uploadLang = v.findViewById(R.id.landmarkEditText);
        saveButton = v.findViewById(R.id.submitButton);
        uploadDate = v.findViewById(R.id.dateEditText);
        uploadDate.setOnClickListener(view -> showDatePickerDialog());

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 1);
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
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
        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                imageURL = uri.toString();
                uploadData();
                progressDialog.dismiss();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    private void uploadData() {
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String lang = uploadLang.getText().toString();
        String date = uploadDate.getText().toString();
        Data dataClass = new Data(title, desc, lang, imageURL, date);
        String key = FirebaseDatabase.getInstance().getReference("Items").push().getKey();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", currentUserId);
        dataMap.put("dataDesc", desc);
        dataMap.put("dataImage", imageURL);
        dataMap.put("dataLang", lang);
        dataMap.put("dataTitle", title);
        dataMap.put("dataDate", date);
        dataMap.put("status", "pending");
        dataMap.put("itemId", key);
        FirebaseDatabase.getInstance().getReference("Items")
                .child(key)
                .setValue(dataMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        uploadTopic.setText("");
                        uploadDesc.setText("");
                        uploadLang.setText("");
                        uploadDate.setText("");
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1){
            if (data != null){
                uri = data.getData();
                uploadImage.setImageURI(uri);
            } else {
                Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
