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
    private int appID;
    private String platform;
    private String displayName;
    private String url;
    private boolean appSwitchIsOn;
    private String username;

    public App() {

    }

    public App(int appID, String displayName, String platform, String url, String usernameIn, Boolean appSwitchIsOn) {
        this.appID = appID;
        this.displayName = displayName;
        this.platform = platform;
        this.url = url;
        this.username = usernameIn;
        this.appSwitchIsOn = appSwitchIsOn;
    }

    public App(int appID, String displayName, String platform, String url) {
        this.appID = appID;
        this.displayName = displayName;
        this.platform = platform;
        this.url = url;
    }

    public App(Parcel in) {
        this.appID = in.readInt();
        this.displayName = in.readString();
        this.platform = in.readString();
        this.url = in.readString();
        this.username = in.readString();
        Integer bool = in.readInt();
        if (bool == 1) {
            setAppSwitchIsOn(true);
        } else if (bool == 0) {
            setAppSwitchIsOn(false);
        } else {
            setAppSwitchIsOn(false);
            Log.d(TAG, "boolean was not 1 or 0");
        }
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
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

    public boolean isAppSwitchIsOn() {
        return appSwitchIsOn;
    }

    public void setAppSwitchIsOn(boolean appSwitchIsOn) {
        this.appSwitchIsOn = appSwitchIsOn;
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
        dest.writeInt(this.appID);
        dest.writeString(this.displayName);
        dest.writeString(this.platform);
        dest.writeString(this.url);
        dest.writeString(this.username);
        if (isAppSwitchIsOn()) {
            dest.writeInt(1);
        } else dest.writeInt(0);
    }

    @Override
    public String toString() {
        return "App{" +
                "id='" + appID + '\'' +
                ", display name='" + displayName + '\'' +
                ", username='" + username + '\'' +
                ", url='" + url + '\'' +
                ", platform='" + platform + '\'' +
                ", switch is on='" + appSwitchIsOn + '\'' +
                '}';
    }
}
