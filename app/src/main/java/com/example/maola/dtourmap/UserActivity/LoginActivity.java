package com.example.maola.dtourmap.UserActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maola.dtourmap.MainActivity;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.Utility.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText loginETEmail;
    private EditText loginETPassword;
    private Button loginBtnSignIp;
    private TextView loginTxtNewUser;
    private CheckBox loginCbRemember;
    private SharedPreferences sharedPreferences;
    private String SHARED_PREF = "PREF_LOGIN";
    private ProgressDialog progressDialog;
    private Button loginBtnAnon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*-------------------VIEWS---------------*/
        loginETEmail = (EditText)findViewById(R.id.login_et_username);
        loginETPassword = (EditText)findViewById(R.id.login_et_pswd);
        loginBtnSignIp = (Button)findViewById(R.id.login__btn_signin);
        loginTxtNewUser = (TextView)findViewById(R.id.login_edt_new_user);
        loginBtnAnon = (Button)findViewById(R.id.login_btn_anonimo);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                // User is signed in
                    if(user.isAnonymous()) {
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else if(!user.isAnonymous()) {
                        Log.d(TAG, "onAuthStateChanged:signed_in: anon" + user.getUid());
                    }
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                } else {
                    Log.i(TAG, "onAuthStateChanged: signed_out");
                    return;
                }
            }
        };

        setupOnButtonClick();
        Intent i = getIntent();
        boolean isAnon = i.getBooleanExtra(Constants.show_anonymous, true);
        if(!isAnon) {
            loginBtnAnon.setText("No grazie, continua come ospite");
            loginBtnAnon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInAnonymously();
                }
            });
        }



    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        //to clear all old opened activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void signIn(String email, String password){
        progressDialog = ProgressDialog.show(LoginActivity.this, "Login in progress",
                "", true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//                        Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();

                        if(task.isSuccessful()){
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();

                    }
                });
    }

    private void signInAnonymously() {
        progressDialog = ProgressDialog.show(LoginActivity.this, "Login in progress",
                "Entro come ospite", true);
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                        Log.d(TAG, "OnComplete : " +task.isSuccessful());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                        } else if (!task.isSuccessful()) {
                            Log.w(TAG, "Failed : ", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void setupOnButtonClick() {
        loginBtnSignIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveCredentials();
                if(loginETEmail.getText().toString().isEmpty()) {
                    loginETEmail.setError(getResources().getString(R.string.insert_email_error));
                } if(loginETPassword.getText().toString().isEmpty()) {
                    loginETPassword.setError(getResources().getString(R.string.insert_password_error));
                } else {
                    signIn(loginETEmail.getText().toString(), loginETPassword.getText().toString());
                }
            }
        });

        loginBtnAnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAnonymously();
            }
        });

        loginTxtNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO possibilità di registrarsi
//                Intent i = new Intent(LoginActivity.this, NewUserActivity.class);
//                startActivity(i);
//                finish();
            }
        });
    }

    private void setupAnonymousMode(boolean isAnon) {

    }



}