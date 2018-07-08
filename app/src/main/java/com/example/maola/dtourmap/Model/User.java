package com.example.maola.dtourmap.Model;

/**
 * Created by Maola on 07/08/2017.
 */

public class User {
    public String username, email, city, address, nation, report;


    public int points;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String username, String email, String city, String address, String nation, String report, int points) {
        this.username = username;
        this.email = email;
        this.city = city;
        this.address = address;
        this.nation = nation;
        this.report = report;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }




}
