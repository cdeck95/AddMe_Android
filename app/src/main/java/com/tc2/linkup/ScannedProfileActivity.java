package com.tc2.linkup;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ScannedProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanned_profile_activity);

        Bundle bundle = getIntent().getExtras();
        String profileId = bundle.getString("profileId");

        TextView profileIdTV = findViewById(R.id.profileIDTextViewImport);
        profileIdTV.setText(profileId);
    }
}
