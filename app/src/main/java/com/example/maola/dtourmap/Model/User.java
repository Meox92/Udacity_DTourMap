package com.example.maola.dtourmap.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Maola on 07/08/2017.
 */

public class User implements Parcelable{
    public String username;
    public String email;
    public String city;
    public String address;
    public String nation;
    public String password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.city);
        dest.writeString(this.address);
        dest.writeString(this.nation);
        dest.writeString(this.password);
        dest.writeInt(this.points);
    }

    protected User(Parcel in) {
        this.username = in.readString();
        this.email = in.readString();
        this.city = in.readString();
        this.address = in.readString();
        this.nation = in.readString();
        this.password = in.readString();
        this.points = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
