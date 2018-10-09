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
    String defaultImageURL = "https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";
    HttpURLConnection urlConnection = null;
    private ArrayList<Profile> profilesArray = new ArrayList<>();
    Integer profileId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        ImageView profileImage = rootView.findViewById(R.id.profileImageView);
        TextView profileNameTV = rootView.findViewById(R.id.profileNameTV);
        TextView profileDescriptionTV = rootView.findViewById(R.id.profileDescriptionTV);

        Bundle args = getArguments();
        profileId = args.getInt("profileId", -1);

        profileDescriptionTV.setText(args.getString("profileDescription", "Something went wrong."));
        profileNameTV.setText(args.getString("profileName", "Something went wrong."));

        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(profileImage);
        downloadTask.execute(args.getString("profileImageUrl", defaultImageURL));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

