package com.example.maola.dtourmap.Model;

import android.databinding.Bindable;
import android.databinding.BaseObservable;


import com.android.databinding.library.baseAdapters.BR;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Maola on 07/08/2017.
 */

public class Report extends BaseObservable {
    private String userId;
    private String userName;
    private String title;
    private String description;
    private String category;
    private String picture;

    private String markerID;
    private Double lat, lng;
    private String address;
    private List<String> time;
    private long reportDate;
    private String sReportDate;
    //Data di segnalazione
    private long postingDate;
    private String sPostingDate;
    private String source;
    private int points;

    public Report(){}


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);

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

    public String getsReportDate() {
        return sReportDate;
    }

    public void setsReportDate(String sReportDate) {
        this.sReportDate = sReportDate;
    }

    public String getsPostingDate() {
        return sPostingDate;
    }

    public void setsPostingDate(String sPostingDate) {
        this.sPostingDate = sPostingDate;
    }

}
