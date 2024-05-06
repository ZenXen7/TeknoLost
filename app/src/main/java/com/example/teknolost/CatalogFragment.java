package com.example.teknolost;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import javax.xml.transform.Result;


public class CatalogFragment extends Fragment {
    private ImageView profile;
    private Uri imageUri;
    private EditText name,description,date,testing;
    private TextView txt;
    private Button btnSub;
    private ImageButton btnDate;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_catalog, container, false);

        name = (EditText) v.findViewById(R.id.itemName);
        description = (EditText) v.findViewById(R.id.itemDesc);
        date = (EditText) v.findViewById(R.id.itemDate);
        btnSub = (Button) v.findViewById(R.id.btnSubmitData);
        btnDate = (ImageButton) v.findViewById(R.id.datebutton);
        txt = (TextView) v.findViewById(R.id.testText);


        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = name.getText().toString();
                String itemDescription = description.getText().toString();
                String dateString = date.toString();

                txt.setText(itemName + " " + itemDescription + " " + dateString);

            }
        });


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });




        return v;


    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DialogTheme , new DatePickerDialog.OnDateSetListener() {
             int pressCount = 0;

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            }
        }, 2023, 01, 20);

        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        datePickerDialog.show();

        datePickerDialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                pressCount++;
//                if (pressCount == 2) {
//                    datePickerDialog.dismiss();
//                }
            }
        });
    }





}