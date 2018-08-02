package com.example.maola.dtourmap.reportActivities;

import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maola.dtourmap.Model.Report;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.UserActivity.LoginActivity;
import com.example.maola.dtourmap.Utility.Constants;
import com.example.maola.dtourmap.Utility.FirebaseUtils;
import com.example.maola.dtourmap.Utility.StringUtils;
import com.example.maola.dtourmap.databinding.ActivityNewReportBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
//    private List<String> categoryArray;
    private Uri reportPic;
    private String urlReportPic;
    private DatePickerDialog datePickerDialog;
    private String TAG = "TAG";
    private boolean reportSaved = false;






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
        binding.newRepoTvTitleAddress.setText(getResources().getString(R.string.retrieve_address));
        binding.setReport(report);
        handlers = new MyClickHandlers(this);
        binding.setHandlers(handlers);
        getAddress(); // move to async tasks loader



        setupUI();

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
        myRef = FirebaseUtils.getReportRef();
        markerID = myRef.push().getKey();
        Log.i(TAG, markerID);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        riversRef = mStorageRef.child("images/" + markerID + "/reportpicture1");

        /*-------------------------------------------------*/

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
    public void onBackPressed() {
        super.onBackPressed();
        if(!reportSaved){
            removePicture();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nrm_action_save) {
            handlers.onButtonClickWithParam(null,report);
            return true;
        } else if (id == R.id.nrm_action_cancel) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void setupUI() {
        // Category spinner
//        categoryArray = Arrays.asList("Seleziona una categoria", "Furto", "Scippo", "Vandalismo", "Spaccio", "Altro");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Constants.categoryArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(arrayAdapter);

        //Date Button
        Calendar c = Calendar.getInstance();
        String currentDay = StringUtils.getDate(c.getTimeInMillis(), "dd/MM/yyyy");
        binding.newRepoSetDate.setText(currentDay);
        binding.newRepoSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                // Set default time(current day)
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day


                // date picker dialog
                datePickerDialog = new DatePickerDialog(NewReportActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String dateString = dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year;
                                binding.newRepoSetDate.setText(dateString);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(view.getYear(), view.getMonth(), view.getDayOfMonth(),
                0, 0, 0);
                                long startTime = calendar.getTimeInMillis();
                                report.setReportDate(startTime);
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
    }


    public void setThumbnail(Intent data) {
        reportPic = data.getData();
        binding.newRepoTvAddPicture.setImageURI(reportPic);
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
                            Log.i(TAG, "picture loaded " + markerID);



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

    private void removePicture() {
        if(reportPic != null) {
            riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d("Deleting file", "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.d("Deleting file", "onFailure: did not delete file");
                }
            });
        }
    }

    private void setMandatoryField() {
        // Avoid app crash because of DB
        report.setLat(0.0d);
        report.setLng(0.0d);
        report.setUserId("0");
        report.setUserName("");
        report.setTypology("Seleziona una categoria!");
        Calendar calendar = Calendar.getInstance();
        report.setReportDate(calendar.getTimeInMillis());
    }

    public void setDataToPush() {

        report.setLat(mLat);
        report.setLng(mLng);
        report.setMarkerID(markerID);
        Calendar calendar = Calendar.getInstance();
        long startTime = calendar.getTimeInMillis();
        report.setPostingDate(startTime);
        report.setUserId(user.getUid());
        report.setUserName(user.getDisplayName());
        if(urlReportPic != null) {
            report.setPicture(urlReportPic);
        }

        String selectedCategory = String.valueOf(binding.categorySpinner.getSelectedItem());
        if(selectedCategory.equals(Constants.categoryArray.get(0))) {
            ((TextView)binding.categorySpinner.getSelectedView()).setError("Seleziona una categoria!");
            return;
        } else {
            report.setTypology(selectedCategory);
        }
        List <String> time = new ArrayList<>();
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

        public void addPhoto() {
            openGallery();
        }

        public void onButtonClickWithParam(View view, Report report) {
            setDataToPush();
            myRef.child(markerID).setValue(report);
            Log.i(TAG, "report saved "+ markerID);
            reportSaved = true;
            onBackPressed();
        }


    }

}


