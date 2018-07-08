package com.example.maola.dtourmap.NewReportActivity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.maola.dtourmap.Model.Report;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.Utility.Constants;
import com.example.maola.dtourmap.databinding.ActivityNewReportBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewReportActivity extends AppCompatActivity {

    private Double mLat;
    private Double mLng;
    private Report report;
    private MyClickHandlers handlers;
    private ActivityNewReportBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_report);

         binding = DataBindingUtil.setContentView(this, R.layout.activity_new_report);
        report = new Report();

        Intent i = getIntent();
        mLat = i.getDoubleExtra(Constants.vLat, 0);
        mLng = i.getDoubleExtra(Constants.vLng,0);
        getAddress();

        binding.setReport(report);

        handlers = new MyClickHandlers(this);
        binding.setHandlers(handlers);

//        binding.newRepoBtnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Save button clicked!", Toast.LENGTH_SHORT).show();
//            }
//        });

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


    public class MyClickHandlers {

        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onFabClicked(View view) {
            Toast.makeText(getApplicationContext(), "FAB clicked!", Toast.LENGTH_SHORT).show();
        }

        public void onButtonClickWithParam(View view, Report report) {
            Toast.makeText(getApplicationContext(), "Button clicked! Name: " + report.description + " title var: ", Toast.LENGTH_SHORT).show();
        }
    }

}


