package com.example.maola.dtourmap.reportActivities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;

import com.example.maola.dtourmap.Model.Report;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.Utility.Constants;
import com.example.maola.dtourmap.Utility.FirebaseUtils;
import com.example.maola.dtourmap.Utility.StringUtils;
import com.example.maola.dtourmap.databinding.ActivityReportDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReportDetailActivity extends AppCompatActivity {
    private ActivityReportDetailBinding binding;
    private String reportId;
    private String TAG = "2ReportDetailActivity";
    private DatabaseReference myRef;
    private Report report;
    private Query query;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_detail);


        Intent i = getIntent();
        reportId = i.getStringExtra(Constants.reportId);
        report = new Report();
        binding.setReport(report);
        myRef = FirebaseUtils.getReportRef();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        CheckBox[] checkBoxes2 = {binding.checkBox, binding.checkBox2, binding.checkBox3, binding.checkBox4, binding.checkBox5, binding.checkBox6};
        for (CheckBox checkBox: checkBoxes2) {
            checkBox.setEnabled(false);
        }

        getReportInfo();
    }

    public void getReportInfo() {
        query = myRef.child(reportId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                report = dataSnapshot.getValue(Report.class);
                if (report != null) {
                    if(report.getAddress() == null) {
                        Address address = getAddress(report.getLat(), report.getLng());
                        if(address != null) {
                            report.setAddress(address.getAddressLine(0) + ", " + address.getLocality());
                        }
                    }
                    if(dataSnapshot.hasChild("postingDate")) {
                       String dateFormatted =  StringUtils.getDate(report.getPostingDate(), true);
                        binding.newRepoTvAuthor.setText("Postato da " + report.getUserName() + " il " + dateFormatted);
                    }
                    if(dataSnapshot.hasChild("reportDate")) {
                        String dateFormatted = StringUtils.getDate(report.getReportDate(), false);
                        binding.newRepoSetDate.setText(dateFormatted);
                    }
                    if(dataSnapshot.hasChild("picture")) {
                        binding.newRepoTvAddPicture.setImageResource(R.drawable.logo_testo_8);
                        setReportImage();

                    }
                    if(dataSnapshot.hasChild("time")) {
                        List<String> timeList = report.getTime();
                        for(String time: timeList) {
                            if(time.contains(getString(R.string.time_slot_0_4))) {
                                binding.checkBox.setChecked(true);
                            }
                            if(time.contains(getString(R.string.time_slot_4_8))) {
                                binding.checkBox2.setChecked(true);
                            }
                            if(time.contains(getString(R.string.time_slot_8_12))) {
                                binding.checkBox3.setChecked(true);
                            }
                            if(time.contains(getString(R.string.time_slot_12_16))) {
                                binding.checkBox4.setChecked(true);
                            }
                            if(time.contains(getString(R.string.time_slot_16_20))) {
                                binding.checkBox5.setChecked(true);
                            }
                            if(time.contains(getString(R.string.time_slot_20_24))) {
                                binding.checkBox6.setChecked(true);
                            }
                        }
                    }
                    binding.setReport(report);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.toString());

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setReportImage(){
        mStorageRef.child("images/"+report.getMarkerID()+"/"+"reportpicture1")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(binding.newRepoTvAddPicture);
                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, exception.toString());
            }
        });

    }

    public Address getAddress(Double mLat, Double mLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        Address address = null;
        try {
            addresses = geocoder.getFromLocation(mLat, mLng, 1);
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0);
                // sending back first address line and locality
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
