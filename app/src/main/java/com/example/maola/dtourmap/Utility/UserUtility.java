package com.example.maola.dtourmap.Utility;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.maola.dtourmap.UserActivity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class UserUtility {
//    private String TAG = "UserUtility";
//    private void logout() {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        currentUser = firebaseAuth.getCurrentUser();
//        if(currentUser != null && currentUser.isAnonymous()){
//            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();
//                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                        i.putExtra(Constants.show_anonymous, false);
//                        startActivity(i);
//                    } else {
//                        Log.w(TAG,"Something is wrong!");
//                    }
//                }
//            });
//        } else {
//            FirebaseAuth.getInstance().signOut();
//            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(i);
//            finish();
//        }
//    }
}
