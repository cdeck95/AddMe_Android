package com.tc2.linkup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.regions.Regions;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    ImageButton imageButton;
    PublisherAdView mPublisherAdView;

    private static final int NUM_PAGES = 5;
    private SectionPageAdapter mSectionsPagerAdapter;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    private FloatingActionButton addAppButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(this, R.color.GColor1),
                        ContextCompat.getColor(this, R.color.GColor2),
                        ContextCompat.getColor(this, R.color.GColor3),
                        ContextCompat.getColor(this, R.color.GColor4),
                        ContextCompat.getColor(this, R.color.GColor5)});

        findViewById(R.id.background).setBackground(gradientDrawable);

        Permissions();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


        addAppButton = findViewById(R.id.fab);


        //AWSMobileClient.getInstance().initialize(this).execute();

        mSectionsPagerAdapter = new SectionPageAdapter(getSupportFragmentManager());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                openBottomSheet();
            }
        });


        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                getResources().getString(R.string.pool_id), // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        Log.d(TAG, credentialsProvider.getIdentityId() + "");
        String cognitoId = credentialsProvider.getIdentityId();
        CredentialsManager.getInstance().setCognitoId(cognitoId);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            //textView.setText(personName);
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            //Picasso.with(getApplicationContext()).load(personPhoto).into(imageView);
        }

//        mPublisherAdView = findViewById(R.id.publisherAdView);
//        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build();
//        mPublisherAdView.loadAd(adRequest);

        Button customButton = new Button(this);
        customButton.setText("");
        customButton.setEnabled(false);
        customButton.setVisibility(View.GONE);

//        ViewTarget target = new ViewTarget(R.id.fab, this);
//        new ShowcaseView.Builder(this)
//                .setTarget(target)
//                .setContentTitle("Let's get started!")
//                .setContentText("Click the plus button to add an account!")
//                .hideOnTouchOutside()
//                .setStyle(R.style.CustomShowcaseTheme2)
//                .replaceEndButton(customButton)
//
//                .build();


    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private void openBottomSheet() {

        //Initializing a bottom sheet
        BottomSheetDialogFragment bottomSheetDialogFragment = new CustomBottomSheetDialogFragment();

        //show it
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_help) {
            //Initializing a bottom sheet
            BottomSheetDialogFragment bottomSheetDialogFragment = new HelpBottomSheetDialogFragment();
            //show it
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } else if (id == R.id.action_logout) {
            //IdentityManager.getDefaultIdentityManager().signOut();
            IdentityManager.getDefaultIdentityManager().signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void Permissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        0);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        0);
            }
        }
    }
}
