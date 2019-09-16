package com.tc2.linkup;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScannedProfileAdapter extends RecyclerView.Adapter<ScannedProfileAdapter.ScannedProfileViewHolder> {

    private static final String TAG = "scannedProfileAdapter";
    private Context mContext;
    private ArrayList<App> accountsListInProfile;
    Boolean isOn = false;

    public ScannedProfileAdapter(Context mContext, ArrayList<App> accountsListInProfile) {
        this.mContext = mContext;
        this.accountsListInProfile = accountsListInProfile;
    }

    @NonNull
    @Override
    public ScannedProfileAdapter.ScannedProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View customView = inflater.inflate(R.layout.scanned_profile_row, parent, false);
        ScannedProfileAdapter.ScannedProfileViewHolder holder = new ScannedProfileAdapter.ScannedProfileViewHolder(customView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScannedProfileAdapter.ScannedProfileViewHolder holder, int position) {
        App account = accountsListInProfile.get(position);
        holder.txtAppDisplayName.setText(account.getDisplayName());
        holder.txtAppUsername.setText("@" + account.getUsername());
        holder.platform = account.getPlatform();
        holder.openAccountButton.setOnClickListener(v -> {
            openWebPage(accountsListInProfile.get(position).getUrl());
        });

        switch (holder.platform) {
            case "Facebook":
                holder.appImage.setImageResource(R.mipmap.ic_facebook);
                break;
            case "Twitter":
                holder.appImage.setImageResource(R.mipmap.ic_twitter);
                break;
            case "Instagram":
                holder.appImage.setImageResource(R.mipmap.ic_instagram);
                break;
            case "Snapchat":
                holder.appImage.setImageResource(R.mipmap.ic_snapchat);
                break;
            case "GooglePlus":
                holder.appImage.setImageResource(R.mipmap.ic_google_plus);
                break;
            case "LinkedIn":
                holder.appImage.setImageResource(R.mipmap.ic_linked_in);
                break;
            case "Xbox":
                holder.appImage.setImageResource(R.mipmap.xbox);
                break;
            case "PSN":
                holder.appImage.setImageResource(R.mipmap.ic_psn);
                break;
            case "Twitch":
                holder.appImage.setImageResource(R.mipmap.ic_twitch);
                break;
            default:
                holder.appImage.setImageResource(R.mipmap.ic_custom); //need custom
                break;
        }



    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Log.d(TAG, webpage+"");
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            mContext.startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return accountsListInProfile.size();
    }

    class ScannedProfileViewHolder extends RecyclerView.ViewHolder{

        TextView txtAppDisplayName;
        TextView txtAppUsername;
        Button openAccountButton;
        ImageView appImage;
        String platform;

        public ScannedProfileViewHolder(View itemView) {
            super(itemView);

            txtAppDisplayName = itemView.findViewById(R.id.accountDisplayName);
            txtAppUsername = itemView.findViewById(R.id.accountUsername);
            openAccountButton = itemView.findViewById(R.id.openAccountButton);
            appImage = itemView.findViewById(R.id.accountImage);
        }
    }

    public ArrayList<App> getAccountsList(){
        return accountsListInProfile;
    }

}