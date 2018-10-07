package com.tc2.linkup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ScreenSlidePageFragment extends Fragment {

    private static final String TAG = "Slide_Page_Fragment";
    String imageURL = "https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";
    HttpURLConnection urlConnection = null;
    private ArrayList<Profile> profilesArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        ImageView profileImage = rootView.findViewById(R.id.profileImageView);
        TextView profileNameTV = rootView.findViewById(R.id.profileNameTV);
        TextView profileDescriptionTV = rootView.findViewById(R.id.profileDescriptionTV);
        //profileDescriptionTV.setText("Testing");
        //profileNameTV.setText("Test!!");

        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(profileImage);
        downloadTask.execute(imageURL);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ScreenSlidePageFragment.GetProfiles(getContext()).execute();

    }

    private class GetProfiles extends AsyncTask<Void, Void, Void> {
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
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
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

            profilesArray.clear();

            for (int n = 0; n < profiles.length(); n++) {
                try {
                    Profile profile = new Gson().fromJson(profiles.getString(n), Profile.class);
//                    JSONObject object = profiles.getJSONObject(n);
//                    Integer id = Integer.parseInt(object.getString("profileId"));
//                    String profileName = object.getString("name");
//                    String imageUrl = object.getString("imageUrl");
//                    String description = object.getString("description");
//                    JSONArray jsonAccounts = object.getJSONArray("accounts");
//                    ArrayList<App> accounts = null;
//                    for (int i = 0; i < jsonAccounts.length(); ++i) {
//                        JSONObject account = jsonAccounts.getJSONObject(i);
//                        ObjectMapper m = new ObjectMapper();
//                        MyClass myClass = m.readValue(o.toString(), MyClass.class);
//                        accounts.add(account);
//                        //....
//                    }
//                    //TODO: change app to profile
//                    Profile profile = new Profile(id, profileName, description, imageUrl, accounts);
                    Log.d(TAG, profile.toString());
                    //TODO: uncomment and add profile
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
            dialog.dismiss();

        }
    }

    private class DownloadImageWithURLTask extends AsyncTask<String,Void,Bitmap>{
        ImageView proflieImage;
        public DownloadImageWithURLTask(ImageView profileImageIn){
          this.proflieImage = profileImageIn;
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
            proflieImage.setImageBitmap(result);
        }
    }



}

