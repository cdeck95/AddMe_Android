package com.tc2.linkup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ScannedProfileActivity extends AppCompatActivity {

    private static final String TAG = ScannedProfileActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private Profile profile;
    private TextView userFullNameTV;
    private TextView profileNameTV;
    private TextView profileDescriptionTV;
    private HttpURLConnection urlConnection;
    private ScannedProfileAdapter adapter;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanned_profile_activity);

        Bundle bundle = getIntent().getExtras();
        Integer profileId = bundle.getInt("profileId");

        userFullNameTV = findViewById(R.id.scannedProfileUserFullName);
        profileNameTV = findViewById(R.id.scannedProfileName);
        profileDescriptionTV = findViewById(R.id.scannedProfileDescription);
        profileImageView = findViewById(R.id.scannedProfileImageView);
        recyclerView = findViewById(R.id.scannedProfileRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        profile = new Profile();
        ArrayList<App> loadingAccounts = new ArrayList<>();
        App account = new App();
        account.setDisplayName("Loading...");
        account.setPlatform("Custom");
        account.setUsername("Loading...");
        account.setUrl("https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
        loadingAccounts.add(account);
        profile.setAccounts(loadingAccounts);


        new GetProfile(this, profileId).execute();
    }


    private void populateApps() {
        if (profile.getAccounts().size() == 0) {
            Log.d(TAG, "Apps list is empty");
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ScannedProfileAdapter(this, profile.getAccounts());
            recyclerView.setAdapter(adapter);
        }

    }

    private class GetProfile extends AsyncTask<String, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        Integer profileId;


        public GetProfile(Context c, Integer profileId) {
            mcontext = c;
            this.profileId = profileId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = new MaterialDialog.Builder(mcontext)
//                    .title("Contacting Server")
//                    .content("Loading...")
//                    .progress(true, 0)
//                    .progressIndeterminateStyle(true)
//                    .show();
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONObject obj;
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/"+ profileId;

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");


                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(TAG, "No profile Found.");
                } else {
                    if (urlConnection.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }

                        Log.d(TAG, "response buffer: " + builder.toString());

                        obj = new JSONObject(builder.toString());
                        JSONObject profileObject = obj.getJSONObject("profile");
                        Gson gson = new Gson();
                        profile = gson.fromJson(profileObject.toString(), Profile.class);
                        Log.d(TAG, profile.getAccounts().toString());
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            adapter.notifyItemRangeChanged(0, profile.getAccounts().size());
//            Log.d(TAG, profile.getAccounts().toString());
            populateApps();
            profileNameTV.setText(profile.getName());
            profileDescriptionTV.setText(profile.getDescription());
            userFullNameTV.setText("Chris Deck");

            if(profile.getImageUrl() == null){
               profileImageView.setImageDrawable(getDrawable(R.drawable.androidicon));
            } else{
                ScannedProfileActivity.DownloadImageWithURLTask downloadTask = new ScannedProfileActivity.DownloadImageWithURLTask(profileImageView);
                downloadTask.execute(profile.getImageUrl());
            }

            //  dialog.dismiss();
        }
    }

    private class DownloadImageWithURLTask extends AsyncTask<String,Void,Bitmap>{
        ImageView profileImage;
        public DownloadImageWithURLTask(ImageView profileImageIn){
            this.profileImage = profileImageIn;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                if(e.getMessage() == null){
                    e.printStackTrace();
                }
                else {
                    Log.d(TAG, e.getMessage());
                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            profileImage.setImageBitmap(result);
        }
    }
}
