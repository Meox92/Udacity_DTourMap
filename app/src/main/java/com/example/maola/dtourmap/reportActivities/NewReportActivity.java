package com.example.maola.dtourmap.reportActivities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.maola.dtourmap.BuildConfig;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewReportActivity extends AppCompatActivity {

    private Double mLat;
    private Double mLng;
    private Report report;
    private MyClickHandlers handlers;
    private ActivityNewReportBinding binding;
    private StorageReference mStorageRef;
    private StorageReference riversRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String markerID;
    private Uri reportPic;
    private String urlReportPic;
    private DatePickerDialog datePickerDialog;
    private String TAG = "TAG";
    private boolean reportSaved = false;
    private long currentTime;
    private long reportTime;
    private List<String> categoryArray;

    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_report);

        Intent i = getIntent();
        mLat = i.getDoubleExtra(Constants.vLat, 0);
        mLng = i.getDoubleExtra(Constants.vLng,0);
        if(savedInstanceState == null) {
            report = new Report();
        } else {
            report = savedInstanceState.getParcelable("report");
        }
        setMandatoryField();
        binding.newRepoTvTitleAddress.setText(getResources().getString(R.string.retrieve_address));
        binding.setReport(report);
        handlers = new MyClickHandlers(this);
        binding.setHandlers(handlers);
        getAddress(); // move to async tasks loader

        sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);




        setupUI();

        /*-----------------USER INSTANCE----------------------*/
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
        mStorageRef = FirebaseStorage.getInstance().getReference();
        riversRef = mStorageRef.child("images/" + markerID + "/reportpicture1");

        /*-------------------------------------------------*/

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("report", report);
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
        // Category spinner: "Seleziona una categoria", "Furto", "Scippo", "Vandalismo", "Spaccio", "Altro"
        categoryArray = Arrays.asList(getString(R.string.select_a_category),
                getString(R.string.furto),
                getString(R.string.scippo),
                getString(R.string.vandalismo),
                getString(R.string.spaccio), getString(R.string.altro));


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoryArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(arrayAdapter);

        //Date Button
        Calendar c = Calendar.getInstance();
        String currentDay = StringUtils.getDate(c.getTimeInMillis(), false);
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
                                reportTime = calendar.getTimeInMillis();
                                report.setReportDate(reportTime);
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
            binding.progressBar.setVisibility(View.VISIBLE);

             riversRef.putFile(reportPic)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            urlReportPic = downloadUrl.toString();
                            binding.progressBar.setVisibility(View.GONE);

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), getString(R.string.file_uploaded), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            binding.progressBar.setVisibility(View.GONE);


                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
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
        report.setCategory(getString(R.string.select_a_category));
        Calendar calendar = Calendar.getInstance();
        report.setReportDate(calendar.getTimeInMillis());
    }


    private void saveSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Constants.wAddress, report.getAddress());
        editor.putString(Constants.wCategory, report.getCategory());
        editor.putString(Constants.wUser, user.getEmail());
        editor.putLong(Constants.wReportTime, report.getReportDate());
        editor.putLong(Constants.wTime, currentTime);

        editor.apply();
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
            report.setLat(mLat);
            report.setLng(mLng);
            report.setMarkerID(markerID);
            Calendar calendar = Calendar.getInstance();
            currentTime = calendar.getTimeInMillis();
            report.setPostingDate(currentTime);
            report.setUserId(user.getUid());
            report.setUserName(user.getDisplayName());
            if(urlReportPic != null) {
                report.setPicture(urlReportPic);
            }

            String selectedCategory = String.valueOf(binding.categorySpinner.getSelectedItem());
            if(selectedCategory.equals(categoryArray.get(0))) {
                ((TextView)binding.categorySpinner.getSelectedView()).setError(getString(R.string.select_a_category));
                return;
            } else {
                report.setCategory(selectedCategory);
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

            Toast.makeText(getApplicationContext(), getString(R.string.new_report_success), Toast.LENGTH_SHORT).show();
            myRef.child(markerID).setValue(report);
            Log.i(TAG, "report saved "+ markerID);
            reportSaved = true;
            saveSharedPreferences();
            onBackPressed();
        }


    }

}


