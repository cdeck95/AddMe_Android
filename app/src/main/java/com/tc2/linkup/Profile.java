package com.tc2.linkup;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Profile implements Serializable, Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
    private static final String TAG = "APP";
    private int profileId;
    private String profileDescription;
    private String profileName;
    private String profileImageUrl;
    private ArrayList<App> accounts;

    public Profile() {

    }

    public Profile(int profileId, String profileName, String profileDescription, String profileImageUrl, ArrayList<App> accounts) {
        this.profileId = profileId;
        this.profileName = profileName;
        this.profileDescription = profileDescription;
        this.profileImageUrl = profileImageUrl;
        this.accounts = accounts;
    }


    public Profile(Parcel in) {
        this.profileId = in.readInt();
        this.profileName = in.readString();
        this.profileDescription = in.readString();
        this.profileImageUrl = in.readString();
        in.readTypedList(accounts, App.CREATOR);
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public ArrayList<App> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<App> accounts) {
        this.accounts = accounts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.profileId);
        dest.writeString(this.profileName);
        dest.writeString(this.profileDescription);
        dest.writeString(this.profileImageUrl);
        dest.writeTypedList(this.accounts);
    }

    @Override
    public String toString() {
        return "Profile: {" +
                "id='" + profileId + '\'' +
                ", name='" + profileName + '\'' +
                ", description='" + profileDescription + '\'' +
                ", url='" + profileImageUrl + '\'' +
                ", accounts='" + accounts + '\'' +
                '}';
    }
}