package com.example.maola.dtourmap.Model;

import android.databinding.Bindable;
import android.databinding.BaseObservable;


import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

/**
 * Created by Maola on 07/08/2017.
 */

public class Report extends BaseObservable {



    public String userId;
    public String title;
    public String description;
    public String typology;
    public String picture;

    public String markerID;
    public Double lat, lng;
    public String address;
    public List<String> time;
    //Data di segnalazione
    public String postingDate;
    public List<String> comments;
    public String source;
    public int points;

    public Report(){}


    public Report(String userId, Double lat, Double lng, String title, String description, String typology, String picture, List<String> time,
                  String postingDate, int points, String markerID, String source) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.typology = typology;
        this.picture = picture;
        this.time = time;
        this.postingDate = postingDate;
//        this.comments = comments;
        this.points = points;
        this.markerID = markerID;
        this.source = source;
    }

//    public Report(String userId, Double lat, Double lng, String title, String description, String typology,
//                  String picture, List<String> time, String postingDate, List<String> comments, int points, String markerID, String source, String address) {
//        this.userId = userId;
//        this.lat = lat;
//        this.lng = lng;
//        this.title = title;
//        this.description = description;
//        this.typology = typology;
//        this.picture = picture;
//        this.time = time;
//        this.postingDate = postingDate;
//        this.comments = comments;
//        this.points = points;
//        this.markerID = markerID;
//        this.source = source;
//        this.address = address;
//    }


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

    public String getTypology() {
        return typology;
    }

    public void setTypology(String typology) {
        this.typology = typology;
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

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
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
}
