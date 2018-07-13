package com.example.maola.dtourmap.NewReportActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maola.dtourmap.Model.Report;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.UserActivity.LoginActivity;
import com.example.maola.dtourmap.Utility.Constants;
import com.example.maola.dtourmap.Utility.FirebaseUtils;
import com.example.maola.dtourmap.databinding.ActivityNewReportBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewReportActivity extends AppCompatActivity {

    private Double mLat;
    private Double mLng;
    private Report report;
    private MyClickHandlers handlers;
    private ActivityNewReportBinding binding;
    private Spinner spinner;
    private StorageReference mStorageRef;
    private StorageReference riversRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String markerID;
    private List<String> categoryArray;
    private Uri reportPic;
    private String urlReportPic;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_report);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_report);

        Intent i = getIntent();
        mLat = i.getDoubleExtra(Constants.vLat, 0);
        mLng = i.getDoubleExtra(Constants.vLng,0);
        report = new Report();
        setMandatoryField();
        report.setAddress(getResources().getString(R.string.retrieve_address));
        binding.setReport(report);
        handlers = new MyClickHandlers(this);
        binding.setHandlers(handlers);
        getAddress(); // move to async tasks loader


//        binding.newRepoBtnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Save button clicked!", Toast.LENGTH_SHORT).show();
//            }
//        });
        categoryArray = Arrays.asList("Seleziona una categoria", "Furto", "Scippo", "Vandalismo", "Spaccio");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoryArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(arrayAdapter);

        /*-----------------USER INSTANCE----------------------*/
        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };
        /*-------------------------------------------------*/

        mStorageRef = FirebaseStorage.getInstance().getReference();
        riversRef = mStorageRef.child("images/" + user.getUid() + "/" + markerID + "/reportpicture1");

        /*-------------------------------------------------*/

        myRef = FirebaseUtils.getReportRef();
        markerID = myRef.push().getKey();

    }

    public void getAddress(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(mLat, mLng, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // sending back first address line and locality
                report.setAddress(address.getAddressLine(0) + ", " + address.getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    setThumbnail(data);
                    uploadFile();
                }
        }
    }



    public void setThumbnail(Intent data) {

        /*Questo metodo mostra l'anteprima e il nome del file selezionata, premendo sul nome si può eliminare il file */
//        reportPic = data.getData();
//        newMarkerTxtPic1.setText(reportPic.getLastPathSegment());
//        newMarkerTxtPic1.setVisibility(View.VISIBLE);
//        newMarkerTxtPic1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                reportPic = null;
//                riversRef.delete();
//                newMarkerTxtPic1.setVisibility(View.GONE);
//                newMarkerImgPic1.setVisibility(View.GONE);
//            }
//        });
//        newMarkerImgPic1.setImageURI(reportPic);
//        newMarkerImgPic1.setVisibility(View.VISIBLE);
    }


    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (reportPic != null) {

            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            riversRef.putFile(reportPic)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            urlReportPic = downloadUrl.toString();


                            progressDialog.dismiss();
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred());

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    private void setMandatoryField() {
        // Avoid app crash because of DB
        report.setLat(0.0d);
        report.setLng(0.0d);
        report.setTypology("N.D.");
    }

    public void setDataToPush() {
        List <String> time = new ArrayList<>();

        report.setLat(mLat);
        report.setLng(mLng);
        report.setMarkerID(markerID);
        Date currentTime = Calendar.getInstance().getTime();
        report.setPostingDate(currentTime.toString());

        String selectedCategory = String.valueOf(binding.categorySpinner.getSelectedItem());
        if(selectedCategory.equals(categoryArray.get(0))) {
            ((TextView)binding.categorySpinner.getSelectedView()).setError("Seleziona una categoria!");
            return;
        } else {
            report.setTypology(selectedCategory);
        }

        CheckBox[] checkBoxes2 = {binding.checkBox, binding.checkBox2, binding.checkBox3, binding.checkBox4, binding.checkBox5, binding.checkBox6};
        for (CheckBox checkBox: checkBoxes2){
            if(checkBox.isChecked()){
                String time1 = checkBox.getText().toString();
                time.add(time1);
            }
        }
        report.setTime(time);



     Toast.makeText(getApplicationContext(), "Segnalazione inserita con successo!", Toast.LENGTH_SHORT).show();
    }


    public class MyClickHandlers {

        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onFabClicked(View view) {
            Toast.makeText(getApplicationContext(), "FAB clicked!", Toast.LENGTH_SHORT).show();
        }

        public void addPhoto() {
            openGallery();
        }

        public void onButtonClickWithParam(View view, Report report) {
            setDataToPush();

            myRef.child(markerID).setValue(report);

            Toast.makeText(getApplicationContext(), "Button clicked! Name: " + report.description + " title var: ", Toast.LENGTH_SHORT).show();
        }
    }

}


