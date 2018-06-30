package com.example.maola.dtourmap.NewReportActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.Utility.Constants;

public class NewReportActivity extends AppCompatActivity {

    private Double mLat;
    private Double mLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        Intent i = getIntent();
//        i.getStringExtra(Constants.vLng);
//        i.getStringExtra(Constants.vLat);
    }
}
