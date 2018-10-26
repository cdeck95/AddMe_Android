package com.tc2.linkup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.atomic.DoubleAccumulator;

public class EditProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static final String TAG = "EditProfileActivity";
    private String cognitoId = "";
    private Integer profileId = -1;
    private Profile profile;
    private TextView userFullNameTV;
    private EditText profileNameTV;
    private EditText profileDescriptionTV;
    private HttpURLConnection urlConnection;
    private ArrayList<App> allAccounts = new ArrayList<>();
    private EditProfileAdapter adapter;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);


        userFullNameTV = findViewById(R.id.editProfileUserFullName);
        profileNameTV = findViewById(R.id.editProfileName);
        profileDescriptionTV = findViewById(R.id.editProfileDescription);
        profileImageView = findViewById(R.id.editProfileImageView);
        recyclerView = findViewById(R.id.editProfileRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        profileId = bundle.getInt("profileId");
        new GetAllAccounts(getApplicationContext()).execute();
        new GetProfile(getApplicationContext(), profileId).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        //new GetAllAccounts(getApplicationContext()).execute();
    }

    private void populateApps() {
        if (allAccounts.size() == 0) {
            Log.d(TAG, "Apps list is empty");
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new EditProfileAdapter(this, profile.getAccounts(), allAccounts);
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            //save information
            Log.d(TAG, "saving information....");
            profile.setAccounts(adapter.getAccountsList());
            profile.setName(profileNameTV.getText().toString());
            profile.setDescription(profileDescriptionTV.getText().toString());
            Log.d(TAG, profile.toString());
            new SaveProfile(getApplicationContext(), profile).execute();
        } else if (id == R.id.action_settings) {
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


    private class GetAllAccounts extends AsyncTask<String, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;


        public GetAllAccounts(Context c) {
            mcontext = c;
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
            JSONArray accounts = new JSONArray();
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";

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
                    Log.e(TAG, "No accounts Found.");
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

            if(allAccounts != null){
                allAccounts.clear();
            }

            for (int n = 0; n < accounts.length(); n++) {
                try {
                    JSONObject object = accounts.getJSONObject(n);
                    Integer id = Integer.parseInt(object.getString("accountId"));
                    String displayName = object.getString("displayName");
                    String appUrl = object.getString("url");
                    String platform = object.getString("platform");
                    String username = object.getString("username");
                    App app = new App(id, displayName, platform, appUrl, username);
                    allAccounts.add(app);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
           //  populateApps();
           //  dialog.dismiss();
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
            populateApps();
            profileNameTV.setText(profile.getName());
            profileDescriptionTV.setText(profile.getDescription());
            userFullNameTV.setText("Chris Deck");
            DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(profileImageView);
            downloadTask.execute(profile.getImageUrl());
            //  dialog.dismiss();
        }
    }

    class SaveProfile extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "SaveProfile";
        String title;
        Context mcontext;
        Profile profile;
        MaterialDialog dialog;
        HttpURLConnection urlConnection = null;


        public SaveProfile(Context c, Profile profile) {
            mcontext = c;
            this.profile = profile;
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
        protected Void doInBackground(Void... params) {
            HttpURLConnection httpcon;
            JSONObject tempObject = new JSONObject();
            JSONObject accounts = new JSONObject();

            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String url = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/" + profile.getProfileId();

            Log.e(TAG, "Retrieve Data URL: " + url);
            try {
                //Connect
                httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
                httpcon.setRequestProperty("Content-Type", "application/json");
                httpcon.setRequestProperty("Accept", "application/json");
                httpcon.setRequestMethod("PUT");
                httpcon.connect();


                try {
                   // tempObject.put("profileId", profile.getProfileId());
                    tempObject.put("name", profile.getName());
                    tempObject.put("description", profile.getDescription());

                    tempObject.put("imageUrl", profile.getImageUrl());
                    JSONArray profileIds = new JSONArray();
                    for(App app: profile.getAccounts()){
                        profileIds.put(app.getAccountId());
                    }
                    tempObject.put("accounts", profileIds);
                    Log.d(TAG, tempObject.toString());
                } catch (JSONException j) {
                    j.printStackTrace();
                }


                //Write
                OutputStream os = httpcon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(tempObject.toString());
                writer.close();
                os.close();

                Log.e(TAG, "Response Number: " + httpcon.getResponseCode());
                Log.e(TAG, "Response Body: " + httpcon.getResponseMessage());


                if (httpcon.getResponseCode() != 404) {
                    Log.e(TAG, "doInBackground: " + httpcon.getInputStream());
                } else {
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // populate list
//            dialog.dismiss();
//            new DroidDialog.Builder(mcontext)
//                    .icon(R.drawable.ic_action_tick)
//                    .title("Success!")
//                    .content("Your profile has been updated.")
//                    .cancelable(true, true)
//                    .neutralButton("DISMISS", droidDialog -> {
//                        droidDialog.dismiss();
//
//                    }).show();

            //            final Snackbar snackBar = Snackbar.make(mView, "Refreshed", Snackbar.LENGTH_SHORT);
//            snackBar.setAction("Dismiss", v -> snackBar.dismiss());
//            snackBar.setActionTextColor(ContextCompat.getColor(mcontext, R.color.colorPrimary));
//            snackBar.show();
            finish();
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
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            profileImage.setImageBitmap(result);
        }
    }
}
