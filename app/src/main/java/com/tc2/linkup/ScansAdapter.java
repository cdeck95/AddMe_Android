package com.tc2.linkup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.droidbyme.dialoglib.DroidDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class ScansAdapter  extends RecyclerView.Adapter<ScansAdapter.ScansViewHolder> {

    private static final String TAG = "ScansAdapter";
    private Context mContext;
    private Scan scans;
    View customView;
    HttpURLConnection urlConnection;
    Activity activity;

    public ScansAdapter(Context mContext, Activity activityIn, Scan scans) {
        this.mContext = mContext;
        activity = activityIn;
        this.scans = scans;
    }

    @NonNull
    @Override
    public ScansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        customView = inflater.inflate(R.layout.custom_scans_row, parent, false);

        ScansViewHolder holder = new ScansViewHolder(customView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScansViewHolder holder, int position) {
        Profile scannedProfile = scans.getScanned_profiles().get(position);
        holder.scannedProfileDescription.setText(scannedProfile.getDescription());
        Log.d(TAG, "Desc: " + scannedProfile.getDescription());
        holder.scannedProfileName.setText(scannedProfile.getName());
        Log.d(TAG, "Name: " + scannedProfile.getName());
        ScansAdapter.DownloadImageWithURLTask downloadTask = new ScansAdapter.DownloadImageWithURLTask(holder.scannedProfileImage);
        downloadTask.execute(scannedProfile.getImageUrl());

        holder.cardViewScans.setOnClickListener(v -> {
            Log.d(TAG, "Card View Clicked");
            // Create Alert using Builder
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(mContext)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle("What action would you like to perform?");
            builder.setItems(new String[]{"View Link", "Delete Link"}, (dialogInterface, index) -> {
                if(index == 0){
                    Bundle args2 = new Bundle();
                    args2.putInt("profileId", scannedProfile.getProfileId());
                    Intent intent = new Intent(customView.getContext(), ScannedProfileActivity.class);
                    intent.putExtras(args2);
                    customView.getContext().startActivity(intent);
                } else if (index == 1) {
                    new DeleteScan(customView.getContext(), scannedProfile.getProfileId()).execute();
                }
                dialogInterface.dismiss();
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        if(scans == null) {
            return 0;
        }
        else return scans.getScanned_profiles().size();
    }

    class ScansViewHolder extends RecyclerView.ViewHolder{

        TextView scannedProfileName;
        TextView scannedProfileDescription;
        ImageView scannedProfileImage;
        CardView cardViewScans;

        public ScansViewHolder(View itemView) {
            super(itemView);

            scannedProfileName = itemView.findViewById(R.id.scansProfileName);
            scannedProfileDescription = itemView.findViewById(R.id.scansProfileDescription);
            scannedProfileImage = itemView.findViewById(R.id.scansProfileImageView);
            cardViewScans = itemView.findViewById(R.id.card_view_scans);
        }
    }

    private class DownloadImageWithURLTask extends AsyncTask<String,Void,Bitmap> {
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
            }
            catch (UnknownHostException e){
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                    activity.runOnUiThread(() -> {
                        new DroidDialog.Builder(mContext)
                                .icon(R.drawable.ic_action_close)
                                .title("Uh-oh!")
                                .content("Are you connected to the internet?")
                                .cancelable(true, true)
                                .neutralButton("DISMISS", droidDialog -> {
                                    droidDialog.dismiss();
                                }).show();
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            profileImage.setImageBitmap(result);
        }
    }

    private class DeleteScan extends AsyncTask<String, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        Integer profileId;


        public DeleteScan(Context c, Integer profileId) {
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
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/scans/"+ profileId;

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");


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
                    } else {
                        Log.d(TAG, "No input stream");
                        return null;
                    }
                }
            }  catch (UnknownHostException e){
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                    activity.runOnUiThread(() -> {
                        new DroidDialog.Builder(mcontext)
                                .icon(R.drawable.ic_action_close)
                                .title("Uh-oh!")
                                .content("Are you connected to the internet?")
                                .cancelable(true, true)
                                .neutralButton("DISMISS", droidDialog -> {
                                    droidDialog.dismiss();
                                }).show();
                    });

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

        }
    }

}
