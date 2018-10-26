package com.tc2.linkup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crowdfire.cfalertdialog.CFAlertDialog;
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
    String defaultImageURL = "https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";
    HttpURLConnection urlConnection = null;
    private ArrayList<Profile> profilesArray = new ArrayList<>();
    Integer profileId = -1;
    String cognitoId;
    String profileImageUrl;
    private Integer selected = -1;
    private ArrayList<App> accounts;
    private ArrayList<App> allAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        CardView cardView = rootView.findViewById(R.id.cardView);
        ImageView profileImage = rootView.findViewById(R.id.profileImageView);
        TextView profileNameTV = rootView.findViewById(R.id.profileNameTV);
        TextView profileDescriptionTV = rootView.findViewById(R.id.profileDescriptionTV);

        Bundle args = getArguments();
        profileId = args.getInt("profileId", -1);
        accounts = args.getParcelableArrayList("accounts");
        allAccounts = args.getParcelableArrayList("allAccounts");
        cognitoId = CredentialsManager.getInstance().getCognitoId();
        profileImageUrl = args.getString("profileImageUrl");

        profileDescriptionTV.setText(args.getString("profileDescription", "Something went wrong."));
        profileNameTV.setText(args.getString("profileName", "Something went wrong."));

        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(profileImage);
        downloadTask.execute(args.getString("profileImageUrl", defaultImageURL));

        cardView.setOnClickListener(v -> {
            Log.d(TAG, "Card View clicked");
            // Create Alert using Builder
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(rootView.getContext())
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle("What action would you like to perform?");
            builder.setItems(new String[]{"View Code", "Edit Profile", "Share Profile", "Delete Profile"}, (dialogInterface, index) -> {
                Toast.makeText(rootView.getContext(), "Selected:"+index, Toast.LENGTH_SHORT).show();
                if(index == 0){
                    Bundle args2 = new Bundle();
                    args2.putInt("profileId", profileId);
                    //Initializing a bottom sheet
                    BottomSheetDialogFragment bottomSheetDialogFragment = new PersonalCodeBottomSheetFragment();
                    bottomSheetDialogFragment.setArguments(args2);
                    //show it
                    bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());

                } else if (index == 1){
                    Bundle args3 = new Bundle();
                    args3.putInt("profileId", profileId);
                    args3.putString("profileName", profileNameTV.getText().toString());
                    args3.putString("profileDescription", profileDescriptionTV.getText().toString());
                    args3.putString("userFullName", "Chris Deck");
                    args3.putString("profileImageUrl", profileImageUrl);
                    args3.putParcelableArrayList("accounts", accounts);
                    args3.putParcelableArrayList("allAccounts", allAccounts);
                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    intent.putExtras(args3);
                    startActivity(intent);
                } else if (index == 2){
                    //share
                } else if (index == 3){
                    new DeleteProfile(rootView.getContext(), profileId).execute();
                }
                dialogInterface.dismiss();
            });
            builder.show();
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setSelected(Integer selectedIn){
        this.selected = selectedIn;
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

    private class DeleteProfile extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        Integer profileId;

        public DeleteProfile(Context c, Integer profileIdIn) {
            mcontext = c;
            profileId = profileIdIn;
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

            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/" + profileId;

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("DELETE");
                urlConnection.connect();

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
                    } else {
                        Log.e(TAG, "No input stream");
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

//            for (int n = 0; n < profilesArray.size(); n++) {
//                try {
//                    if (profilesArray.get(n).getProfileId() == profileId) {
//                        profilesArray.remove(n);
//                        Log.d(TAG, "Profile removed from array");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }
    }
}

