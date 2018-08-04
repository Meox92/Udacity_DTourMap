package com.example.maola.dtourmap.UserActivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.maola.dtourmap.MainActivity;
import com.example.maola.dtourmap.Model.User;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.InputStream;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivitySignupBinding binding;
    private User user;
    // Temporary image, gonna replace it in future
    // Cannot upload in firebase storage because it requires authentication
    public static String logo_url = "https://image.ibb.co/hi7vDz/degrado_Tour_Logo.png";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        if(savedInstanceState == null) {
            user = new User();
        } else {
            user = savedInstanceState.getParcelable("user");
        }
        binding.setUser(user);
        binding.signupBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser();
            }
        });

        binding.imageView3.setTag(logo_url);
        new DownloadImagesTask().execute(binding.imageView3);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("user", user);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = savedInstanceState.getParcelable("user");
    }

    private void newUser(){

        final String email = user.getEmail();
        final String password = user.getPassword();

        //creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            signIn(email, password);

                        } else {
                            //display some message here
                            Toast.makeText(SignupActivity.this,getString(R.string.sign_up_error),Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void signIn(String email, String password){
//        progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.login_in_progress),
//                "", true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();

                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getUsername()).build();
                            if(firebaseUser != null) {
                                firebaseUser.updateProfile(profileUpdates);
                            }

                            Intent i = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(i);
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        ImageView imageView = null;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }


        private Bitmap download_Image(String url) {
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
    }

}
