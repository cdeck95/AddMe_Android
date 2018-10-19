package com.tc2.linkup;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Tom on 3/24/17.
 */

public class App implements Serializable, Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public App createFromParcel(Parcel in) {
            return new App(in);
        }

        public App[] newArray(int size) {
            return new App[size];
        }
    };
    private static final String TAG = "APP";
    private int accountId;
    private String platform;
    private String displayName;
    private String url;
    private String userId;
    private String username;

    public App() {

    }

    public App(int accountId, String displayName, String platform, String url, String usernameIn, Boolean appSwitchIsOn) {
        this.accountId = accountId;
        this.displayName = displayName;
        this.platform = platform;
        this.url = url;
        this.username = usernameIn;
       // this.appSwitchIsOn = appSwitchIsOn;
    }

    public App(int accountId, String displayName, String platform, String url) {
        this.accountId = accountId;
        this.displayName = displayName;
        this.platform = platform;
        this.url = url;
    }

    public App(Parcel in) {
        this.accountId = in.readInt();
        this.displayName = in.readString();
        this.platform = in.readString();
        this.url = in.readString();
        this.username = in.readString();
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.accountId);
        dest.writeString(this.displayName);
        dest.writeString(this.platform);
        dest.writeString(this.url);
        dest.writeString(this.username);
    }

//    @Override
//    private void readFromParcel(Parcel in) {
//        this.accountId = in.readInt();
//        this.displayName = in.readString();
//        this.platform = in.readString();
//        this.url = in.readString();
//        this.username = in.readString();
//    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + accountId + '\"' +
                ", \"displayName\":\"" + displayName + '\"' +
                ", \"username\":\"" + username + '\"' +
                ", \"url\":\"" + url + '\'' +
                ", \"platform\":\"" + platform + '\"' +
                //", switch is on='" + appSwitchIsOn + '\'' +
                '}';
    }
}
