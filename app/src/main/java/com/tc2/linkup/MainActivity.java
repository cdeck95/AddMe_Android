package com.tc2.linkup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.regions.Regions;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    ImageButton imageButton;
    PublisherAdView mPublisherAdView;

    private static final int NUM_PAGES = 5;
    private SectionPageAdapter mSectionsPagerAdapter;
    public ViewPager mPager;
    public PagerAdapter mPagerAdapter;

    private ArrayList<Profile> profilesArray = new ArrayList<>();
    HttpURLConnection urlConnection = null;
    private FloatingActionButton addAppButton;
    private Integer selected = -1;
    private String cognitoId;

    String defaultImageURL = "https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";
    private ArrayList<App> apps = new ArrayList<>();
    SwipeRefreshLayout pullToRefresh;
    // private JSONArray apps = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        new GetProfiles(this).execute();
        new GetAllAccounts(this).execute();

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(this, R.color.GColor2),
                        ContextCompat.getColor(this, R.color.GColor2)});

        findViewById(R.id.background).setBackground(gradientDrawable);

        Permissions();


        addAppButton = findViewById(R.id.fab);


        //AWSMobileClient.getInstance().initialize(this).execute();

        mSectionsPagerAdapter = new SectionPageAdapter(getSupportFragmentManager());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                // Create Alert using Builder
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setTitle("What would you like to add?");
                builder.setSingleChoiceItems(new String[]{"Profile", "Account"}, 3, (dialogInterface, index) ->
                {
                    setSelected(index);
                    Toast.makeText(MainActivity.this, "Selected:"+index, Toast.LENGTH_SHORT).show();
                });
                builder.addButton("DONE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialogInterface, i) ->
                {
                    Toast.makeText(MainActivity.this, "Selected:"+selected, Toast.LENGTH_SHORT).show();
                    if(selected == 1){
                        openBottomSheet();
                    } else {
                        addProfile();
                    }
                    dialogInterface.dismiss();
                });

                // Show the alert
                builder.show();
                //openBottomSheet();
            }
        });

        setCognitoId();


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
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setVisibility(View.GONE);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        //setting an setOnRefreshListener on the SwipeDownLayout
        pullToRefresh.setOnRefreshListener(() -> {
            Log.e(TAG, "Refeshing...");
            pullToRefresh.setRefreshing(true);
            new GetProfiles(MainActivity.this).execute();
            mPagerAdapter.notifyDataSetChanged();
            pullToRefresh.setRefreshing(false);
        });

        mPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float v, int i1 ) {
            }

            @Override
            public void onPageSelected( int position ) {
            }

            @Override
            public void onPageScrollStateChanged( int state ) {
                enableDisableSwipeRefresh( state == ViewPager.SCROLL_STATE_IDLE );
            }
        } );


        Button customButton = new Button(this);
        customButton.setText("");
        customButton.setEnabled(false);
        customButton.setVisibility(View.GONE);

        BottomNavigationViewEx bnve = findViewById(R.id.navigation);
        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(true);


    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (pullToRefresh != null) {
            pullToRefresh.setEnabled(enable);
        }
    }

    private void addProfile() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_profiles, null);
        dialogBuilder.setView(dialogView);

        final EditText nameIn = dialogView.findViewById(R.id.displayName);
        final EditText descriptionIn = dialogView.findViewById(R.id.username);
        //final EditText message = (EditText) dialogView.findViewById(R.id.customFeedback);

        dialogBuilder.setTitle("Add Profile");
        dialogBuilder.setMessage("Please enter details...");
        dialogBuilder.setPositiveButton("Add", (dialog, whichButton) -> {
            String name = nameIn.getText().toString().trim();
            String description = descriptionIn.getText().toString();
            // String messageStr = message.getText().toString().trim();
            Log.d(TAG, name);
            Log.d(TAG, description);
            new AddProfile(MainActivity.this, MainActivity.this, name, description).execute();

        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setSelected(Integer selectedIn){
        this.selected = selectedIn;
    }

    private void setCognitoId(){
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                getResources().getString(R.string.pool_id), // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        Log.d(TAG, credentialsProvider.getIdentityId() + "");
        cognitoId = credentialsProvider.getIdentityId();
        CredentialsManager.getInstance().setCognitoId(cognitoId);
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
            ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("profileId", profilesArray.get(position).getProfileId());
            args.putString("profileImageUrl", profilesArray.get(position).getImageUrl());
            args.putString("profileName", profilesArray.get(position).getName());
            args.putString("profileDescription", profilesArray.get(position).getDescription());
            screenSlidePageFragment.setArguments(args);
            return screenSlidePageFragment;
        }

        @Override
        public int getCount() {
            //return NUM_PAGES;
            return profilesArray.size();
        }
    }

    public class GetProfiles extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;


        public GetProfiles(Context c) {
            mcontext = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();

//              mProgressDialog = new ProgressDialog(mcontext);
//              mProgressDialog.setTitle("Contacting Server");
//              mProgressDialog.setMessage("Loading...");
//              mProgressDialog.setIndeterminate(false);
//              mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //connect to API
            JSONObject obj = null;
            JSONArray profiles = new JSONArray();
            cognitoId = CredentialsManager.getInstance().getCognitoId();
            if(cognitoId == null){
                setCognitoId();
                cognitoId = CredentialsManager.getInstance().getCognitoId();
            }
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                urlConnection = (HttpURLConnection) url.openConnection();

                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(TAG, "No Profiles Found.");
                } else {
                    if (urlConnection.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }

                        Log.e(TAG, "response buffer: " + builder.toString());

                        obj = new JSONObject(builder.toString());
                        profiles = obj.getJSONArray("profiles");
                        //prefs.edit().putString("accounts", builder.toString()).apply();

                    } else {
                        Log.d(TAG, "No input stream");
                        return null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            if(profilesArray != null){
                profilesArray.clear();
            }

            for (int n = 0; n < profiles.length(); n++) {
                try {
                    String profileString = profiles.getString(n);
                    Log.d(TAG, profileString);
                    Profile profile = new Gson().fromJson(profileString, Profile.class);
                    Log.d(TAG, profile.toString());
                    profilesArray.add(profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // populate list
            //TODO:Create populateProfiles
            // populateProfiles(1, getView());
            //mProgressDialog.dismiss();
            MainActivity.this.runOnUiThread(() -> {
                mPager.setAdapter(mPagerAdapter);
                mPager.setVisibility(View.VISIBLE);
            });
            dialog.dismiss();
        }
    }

    private class AddProfile extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        String profileName;
        String profileDescription;

        public AddProfile(Context c, Activity a, String nameIn, String descriptionIn) {
            mcontext = c;
            profileName = nameIn;
            profileDescription = descriptionIn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();

//              mProgressDialog = new ProgressDialog(mcontext);
//              mProgressDialog.setTitle("Contacting Server");
//              mProgressDialog.setMessage("Loading...");
//              mProgressDialog.setIndeterminate(false);
//              mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //connect to API
            JSONObject obj = new JSONObject();
            JSONObject tempObject = new JSONObject();
            JSONArray profiles = new JSONArray();
            cognitoId = CredentialsManager.getInstance().getCognitoId();
            if(cognitoId == null){
                setCognitoId();
                cognitoId = CredentialsManager.getInstance().getCognitoId();
            }
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();


                try {
                    tempObject.put("name", profileName);
                    tempObject.put("description", profileDescription);
                    tempObject.put("imageUrl", defaultImageURL);
                    Log.d(TAG, apps.toString());
                    JSONArray arrayOfIDs = new JSONArray();
                    for(int i=0; i<apps.size();i++){
                        arrayOfIDs.put(apps.get(i).getAccountId());
                    }
                    tempObject.putOpt("accounts", arrayOfIDs);
                    Log.d(TAG, tempObject.toString());
                } catch (JSONException j) {
                    j.printStackTrace();
                }

                try {
                    //Write
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(tempObject.toString());
                    writer.close();
                    os.close();

                    Log.e(TAG, "Response Number: " + urlConnection.getResponseCode());
                    Log.e(TAG, "Response Body: " + urlConnection.getResponseMessage());


                    if (urlConnection.getResponseCode() != 404) {
                        Log.e(TAG, "doInBackground: " + urlConnection.getInputStream());
                    } else {
                        if (urlConnection.getInputStream() != null) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String line;
                            StringBuilder builder = new StringBuilder();
                            while ((line = in.readLine()) != null) {
                                builder.append(line);
                            }

                            Log.e(TAG, "response buffer: " + builder.toString());

                            obj = new JSONObject(builder.toString());
                            profiles = obj.getJSONArray("profiles");
                            mPagerAdapter.notifyDataSetChanged();
                            //prefs.edit().putString("accounts", builder.toString()).apply();

                        } else {
                            Log.d(TAG, "No input stream");
                            return null;
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            if(profilesArray != null){
                profilesArray.clear();
            }

            for (int n = 0; n < profiles.length(); n++) {
                try {
                    String profileString = profiles.getString(n);
                    Log.d(TAG, profileString);
                    Profile profile = new Gson().fromJson(profileString, Profile.class);
                    Log.d(TAG, profile.toString());
                    profilesArray.add(profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // populate list
            //TODO:Create populateProfiles
            // populateProfiles(1, getView());
            //mProgressDialog.dismiss();
            MainActivity.this.runOnUiThread(() -> {
                mPager.setAdapter(mPagerAdapter);
                mPager.setVisibility(View.VISIBLE);
            });
            new GetProfiles(mcontext).execute();
            dialog.dismiss();
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        Log.d(TAG, result.toString());
        return result.toString();
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

    private class GetAllAccounts extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;


        public GetAllAccounts(Context c) {
            mcontext = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();

//              mProgressDialog = new ProgressDialog(mcontext);
//              mProgressDialog.setTitle("Contacting Server");
//              mProgressDialog.setMessage("Loading...");
//              mProgressDialog.setIndeterminate(false);
//              mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //connect to API
            JSONObject obj = null;
            JSONArray accounts = new JSONArray();
            cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(TAG, "No accounts Found.");
                } else {
                    if (urlConnection.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }

                        Log.e(TAG, "response buffer: " + builder.toString());

                        obj = new JSONObject(builder.toString());
                        accounts = obj.getJSONArray("accounts");


                    } else {
                        Log.d(TAG, "No input stream");
                        return null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            apps.clear();

            for (int n = 0; n < accounts.length(); n++) {
                try {
                    JSONObject object = accounts.getJSONObject(n);
                    Integer id = Integer.parseInt(object.getString("accountId"));
                    String displayName = object.getString("displayName");
                    String appUrl = object.getString("url");
                    String platform = object.getString("platform");
                    String username = object.getString("username");
                    App app = new App(id, displayName, platform, appUrl, username, Boolean.TRUE);
                    apps.add(app);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }
    }
}
