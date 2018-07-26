package com.example.maola.dtourmap.reportActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.maola.dtourmap.Model.Report;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.Utility.Constants;
import com.example.maola.dtourmap.Utility.FirebaseUtils;
import com.example.maola.dtourmap.databinding.ActivityNewReportBinding;
import com.example.maola.dtourmap.databinding.ActivityReportDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportDetailActivity extends AppCompatActivity {
    private ActivityReportDetailBinding binding;
    private String reportId;
    private String TAG = "2ReportDetailActivity";
    private DatabaseReference myRef;
    private String reportTitle, reportAuthor, reportPoints, reportDescr, reportTypo, reportPciture;
    private Long reportDate;
    private List reportTime;
    private Report report;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_detail);


        Intent i = getIntent();
        reportId = i.getStringExtra(Constants.reportId);

        myRef = FirebaseUtils.getReportRef();
        getReportInfo();


    }

    public void getReportInfo() {
        query = myRef.child(reportId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                report = dataSnapshot.getValue(Report.class);
//                Iterable<DataSnapshot> contactChildren = dataSnapshot.getChildren();
//                for (DataSnapshot contact : contactChildren) {
//                    report = contact.getValue(Report.class);
//                    Log.i(TAG, report.getAddress());
//                    Log.i(TAG, report.getPicture());
//
//                }

                if (report != null) {
                    reportTitle = report.getTitle();
                    reportAuthor = report.getUserId();
                    reportDate = report.getReportDate();
                    reportDescr = report.getDescription();
                    reportTime = report.getTime();
                    reportTypo = report.getTypology();
                    if(report.getPicture() != null) {
                     reportPciture = report.getPicture();
                    }
                }
//                Log.i(TAG, reportPciture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.toString());

            }
        });
    }

    public Address getAddress(Double mLat, Double mLng){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        Address address = null;
        try {
            addresses = geocoder.getFromLocation(mLat, mLng, 1);
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0);
                // sending back first address line and locality
                report.setAddress(address.getAddressLine(0) + ", " + address.getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
