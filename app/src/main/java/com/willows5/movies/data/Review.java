package com.willows5.movies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    String _author;
    String _content;
    String _id;
    String _url;

    public Review(String author, String content, String id, String url) {
        this._author = author;
        this._content = content;
        this._id = id;
        this._url = url;
    }

    protected Review(Parcel in) {
        this._author = in.readString();
        this._content = in.readString();
        this._id = in.readString();
        this._url = in.readString();
    }

    public String getAuthor() {
        return _author;
    }

    public String getContent() {
        return _content;
    }

    public String getId() {
        return _id;
    }

    public String getUrl() {
        return _url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._author);
        dest.writeString(this._content);
        dest.writeString(this._id);
        dest.writeString(this._url);
    }
}
