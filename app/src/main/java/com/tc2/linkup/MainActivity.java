package com.tc2.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.regions.Regions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActionBar toolbar;


    // collections
    private SparseIntArray items;// used for change ViewPager selected item
    private List<Fragment> fragments;// used for ViewPager adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment homeFragment = new HomeActivity();
        loadFragment(homeFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.profile:
                    toolbar.setTitle("Home");
                    fragment = new HomeActivity();
                    loadFragment(fragment);
                    return true;
                case R.id.import_code:
                    toolbar.setTitle("Import");
                    fragment = new ImportCodeActivity();
                    loadFragment(fragment);
                    return true;
                case R.id.scan_code:
                    toolbar.setTitle("Scan");
                    fragment = new ScanCodeActivity();
                    loadFragment(fragment);
                    return true;
                case R.id.MyLinks:
                    toolbar.setTitle("MyLinks");
                    fragment = new MyLinksActivity();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_help) {
            //Initializing a bottom sheet
            BottomSheetDialogFragment bottomSheetDialogFragment = new HelpBottomSheetDialogFragment();
            //show it
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } else if (id == R.id.action_logout) {
            //IdentityManager.getDefaultIdentityManager().signOut();
            IdentityManager.getDefaultIdentityManager().signOut();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    this,
                    getResources().getString(R.string.pool_id), // Identity pool ID
                    Regions.US_EAST_1 // Region
            );

            credentialsProvider.clearCredentials();
            credentialsProvider.clear();
        }

        return super.onOptionsItemSelected(item);
    }
}