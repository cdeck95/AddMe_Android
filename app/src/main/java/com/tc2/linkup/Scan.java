package com.tc2.linkup;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Scan implements Serializable, Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Scan createFromParcel(Parcel in) {
            return new Scan(in);
        }

        public Scan[] newArray(int size) {
            return new Scan[size];
        }
    };

    private ArrayList<Profile> scanned_profiles;

    public Scan(){

    }

    public Scan(Parcel in){
        in.readTypedList(scanned_profiles, App.CREATOR);
    }

    public void setScanned_profiles(ArrayList<Profile> scanned_profiles) {
        this.scanned_profiles = scanned_profiles;
    }

    public ArrayList<Profile> getScanned_profiles() {
        return scanned_profiles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.scanned_profiles);
    }

}
