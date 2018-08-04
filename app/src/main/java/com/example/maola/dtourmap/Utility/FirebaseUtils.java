package com.example.maola.dtourmap.Utility;

/**
 * Created by Maola on 16/09/2017.
 */


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }


    public static DatabaseReference getCurrentUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("utenti").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getReportRef() {
        return getBaseRef().child("Report");
    }

    public static String getReportPath() {
        return "Report/";
    }

    public static DatabaseReference getUsersRef() {
        return getBaseRef().child("utenti");
    }

    public static DatabaseReference getCommentsRef() {
        return getBaseRef().child("comments");
    }



}

