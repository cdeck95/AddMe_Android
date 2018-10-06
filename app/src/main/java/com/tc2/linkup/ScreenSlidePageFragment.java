package com.tc2.linkup;

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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScreenSlidePageFragment extends Fragment {

    private static final String TAG = "Slide_Page_Fragment";
    String imageURL = "https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";

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

