package com.willows5.movies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
    String _id;
    String _key;
    String _name;
    String _type;

    public Video(String id, String key, String name, String type) {
        this._id = id;
        this._key = key;
        this._name = name;
        this._type = type;
    }

    protected Video(Parcel in) {
        this._id = in.readString();
        this._key = in.readString();
        this._name = in.readString();
        this._type = in.readString();
    }

    public String getId() {
        return _id;
    }

    public String getKey() {
        return _key;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this._key);
        dest.writeString(this._name);
        dest.writeString(this._type);
    }
}
