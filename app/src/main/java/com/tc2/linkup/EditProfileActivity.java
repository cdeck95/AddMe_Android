package com.tc2.linkup;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    ArrayList<App> accounts;
    private static final String TAG = "EditProfileActivity";
    private String cognitoId = "";
    private Integer profileId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        recyclerView = findViewById(R.id.editProfileRecyclerView);

        Intent intent = getIntent();
        profileId = intent.getIntExtra("profileId", -1);
        accounts = intent.getParcelableArrayListExtra("accounts");
//        if (intent!=null) {
//            if(intent.containsKey("profileId")) {
//                profileId = bundle.getInt("profileId");
//                if(bundle.containsKey("accounts")) {
//                    accounts = bundle.getParcelableArrayList("accounts");
//                } else {
//                    Log.e(TAG, "bundle does not contain accounts");
//                }
//
//            } else {
//                Log.e(TAG, "bundle does not contain profileId");
//            }
//        } else {
//            Log.e(TAG, "bundle is null");
//        }


        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "----Refreshed----");
            populateApps();
            //new GetAccountsForProfile(this).execute();
            //populateApps(1, rootView);
            mSwipeRefreshLayout.setRefreshing(false);
            final Snackbar snackBar = Snackbar.make(mSwipeRefreshLayout, "Refreshed", Snackbar.LENGTH_SHORT);
            snackBar.setAction("Dismiss", v -> snackBar.dismiss());
            snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            snackBar.show();
        });


        populateApps();
    }

    private void populateApps() {
        if (accounts.size() == 0) {
            Log.d(TAG, "Apps list is empty");
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            RecyclerView.Adapter adapter = new EditProfileAdapter(this, accounts);
            recyclerView.setAdapter(adapter);
        }

    }

//    public class GetAccountsForProfile extends AsyncTask<Void, Void, Void> {
//        String title;
//        Context mcontext;
//        MaterialDialog dialog;
//
//
//        public GetAccountsForProfile(Context c) {
//            mcontext = c;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            dialog = new MaterialDialog.Builder(mcontext)
//                    .title("Contacting Server")
//                    .content("Loading...")
//                    .progress(true, 0)
//                    .progressIndeterminateStyle(true)
//                    .show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            //connect to API
//            JSONObject obj = null;
//            JSONArray profiles = new JSONArray();
//            cognitoId = CredentialsManager.getInstance().getCognitoId();
//            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/";
//
//            Log.d(TAG, "Cognito ID: " + cognitoId);
//
//            URL url = null;
//            try {
//                url = new URL(urlIn);
//                Log.d(TAG, "URL: " + urlIn);
//                urlConnection = (HttpURLConnection) url.openConnection();
//
//                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
//                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
//                if (urlConnection.getResponseCode() == 404) {
//                    Log.e(TAG, "No Profiles Found.");
//                } else {
//                    if (urlConnection.getInputStream() != null) {
//                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                        String line;
//                        StringBuilder builder = new StringBuilder();
//                        while ((line = in.readLine()) != null) {
//                            builder.append(line);
//                        }
//
//                        Log.e(TAG, "response buffer: " + builder.toString());
//
//                        obj = new JSONObject(builder.toString());
//                        profiles = obj.getJSONArray("profiles");
//                        //prefs.edit().putString("accounts", builder.toString()).apply();
//
//                    } else {
//                        Log.d(TAG, "No input stream");
//                        return null;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//            }
//
//            if(profilesArray != null){
//                profilesArray.clear();
//            }
//
//            for (int n = 0; n < profiles.length(); n++) {
//                try {
//                    String profileString = profiles.getString(n);
//                    Log.d(TAG, profileString);
//                    Profile profile = new Gson().fromJson(profileString, Profile.class);
//                    Log.d(TAG, profile.toString());
//                    profilesArray.add(profile);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            // populate list
//            //TODO:Create populateProfiles
//            // populateProfiles(1, getView());
//            //mProgressDialog.dismiss();
//            MainActivity.this.runOnUiThread(() -> {
//                mPager.setAdapter(mPagerAdapter);
//                mPager.setVisibility(View.VISIBLE);
//            });
//            dialog.dismiss();
//        }
//    }
}
