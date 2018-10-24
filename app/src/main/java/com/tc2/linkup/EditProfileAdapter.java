package com.tc2.linkup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EditProfileAdapter  extends RecyclerView.Adapter<EditProfileAdapter.EditProfileViewHolder> {

    private static final String TAG = "EditProfileAdapter";
    private final ArrayList<App> allAccounts;
    private Context mContext;
    private ArrayList<App> accountsList;
    Boolean isOn = false;

    public EditProfileAdapter(Context mContext, ArrayList<App> accountsList, ArrayList<App> allAccounts) {
        this.mContext = mContext;
        this.accountsList = accountsList;
        this.allAccounts = allAccounts;
    }

    @NonNull
    @Override
    public EditProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View customView = inflater.inflate(R.layout.edit_profile_custom_row, parent, false);
        EditProfileViewHolder holder = new EditProfileViewHolder(customView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditProfileViewHolder holder, int position) {
        App account = accountsList.get(position);
        holder.txtAppDisplayName.setText(account.getDisplayName());
        holder.txtAppUsername.setText("@" + account.getUsername());
        holder.appID = account.getAccountId() + "";
        holder.txtAppID.setText(holder.appID);
        holder.platform = account.getPlatform();

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

        holder.accountSwitch.setChecked(true);

        holder.accountSwitch.setOnClickListener(v -> {
            Log.d(TAG, "Account switch is " + holder.accountSwitch.isChecked());
            if(holder.accountSwitch.isChecked()){
                if(accountsList.contains(account)){
                    //do nothing
                } else {
                    accountsList.add(account);
                }
            } else {
                if(accountsList.contains(account)){
                    accountsList.remove(account);
                } else {
                    //do nothing
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }

    class EditProfileViewHolder extends RecyclerView.ViewHolder{

        TextView txtAppDisplayName;
        TextView txtAppUsername;
        Switch accountSwitch;
        TextView txtAppID;
        String appID;
        ImageView appImage;
        String platform;

        public EditProfileViewHolder(View itemView) {
            super(itemView);

            txtAppDisplayName = itemView.findViewById(R.id.accountDisplayName);
            txtAppUsername = itemView.findViewById(R.id.accountUsername);
            accountSwitch = itemView.findViewById(R.id.accountSwitch);
            txtAppID = itemView.findViewById(R.id.editProfileAccountId);
            appImage = itemView.findViewById(R.id.accountImage);
        }
    }

    public ArrayList<App> getAccountsList(){
        return accountsList;
    }

}
