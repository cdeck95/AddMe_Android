package com.tc2.linkup;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
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
    private String description;
    private String name;
    private String imageUrl;
    private ArrayList<App> accounts;
    private String cognitoId;

    public Profile() {

    }

    public Profile(int profileId, String name, String profileDescription, String imageUrl, ArrayList<App> accounts) {
        this.profileId = profileId;
        this.name = name;
        this.description = profileDescription;
        this.imageUrl = imageUrl;
        this.accounts = accounts;
    }


    public Profile(Parcel in) {
        this.profileId = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.imageUrl = in.readString();
        in.readTypedList(accounts, App.CREATOR);
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.imageUrl);
        dest.writeTypedList(this.accounts);
    }

    @Override
    public String toString() {
        return "Profile: {" +
                "profileId='" + profileId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", accounts='" + accounts + '\'' +
                '}';
    }
}