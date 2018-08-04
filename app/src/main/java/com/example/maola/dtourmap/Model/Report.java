package com.example.maola.dtourmap.Model;

import android.databinding.Bindable;
import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;


import com.android.databinding.library.baseAdapters.BR;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Maola on 07/08/2017.
 */

public class Report implements Parcelable {
    private String userId;
    private String userName;
    private String title;
    private String description;
    private String category; // Create category object with ids
    private String picture;

    private String markerID;
    private Double lat, lng;
    private String address;
    private List<String> time;
    private long reportDate;
    //Data di segnalazione
    private long postingDate;
    private String source;
    private int points;

    public Report(){}


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public long getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(long postingDate) {
        this.postingDate = postingDate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getMarkerID() {
        return markerID;
    }

    public void setMarkerID(String markerID) {
        this.markerID = markerID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getReportDate() {
        return reportDate;
    }

    public void setReportDate(long reportDate) {
        this.reportDate = reportDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.category);
        dest.writeString(this.picture);
        dest.writeString(this.markerID);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
        dest.writeString(this.address);
        dest.writeStringList(this.time);
        dest.writeLong(this.reportDate);
        dest.writeLong(this.postingDate);
        dest.writeString(this.source);
        dest.writeInt(this.points);
    }

    protected Report(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.category = in.readString();
        this.picture = in.readString();
        this.markerID = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
        this.address = in.readString();
        this.time = in.createStringArrayList();
        this.reportDate = in.readLong();
        this.postingDate = in.readLong();
        this.source = in.readString();
        this.points = in.readInt();
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel source) {
            return new Report(source);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };
}
