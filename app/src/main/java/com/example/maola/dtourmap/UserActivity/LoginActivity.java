package com.example.maola.dtourmap.UserActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maola.dtourmap.NewReportActivity.NewReportActivity;
import com.example.maola.dtourmap.R;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*-------------------VIEWS---------------*/
        loginETEmail = (EditText)findViewById(R.id.login_et_username);
        loginETPassword = (EditText)findViewById(R.id.login_et_pswd);
        loginBtnSignIp = (Button)findViewById(R.id.login__btn_signin);
        loginTxtNewUser = (TextView)findViewById(R.id.login_edt_new_user);
        loginCbRemember = (CheckBox)findViewById(R.id.login_cb_remember);

        /*---------------------------------------*/


        /*------PREFERENCES----------------------*/
        sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        setSharedPreferences();
        /*---------------------------------------*/




        loginBtnSignIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCredentials();
                signIn(loginETEmail.getText().toString(), loginETPassword.getText().toString());
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

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };



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
                            Intent i = new Intent(LoginActivity.this, NewReportActivity.class);
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


    private void saveCredentials(){
        if (loginCbRemember.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("emailLogin", loginETEmail.getText().toString());
            editor.putString("passwordLogin", loginETPassword.getText().toString());
            editor.putBoolean("rememberLogin", loginCbRemember.isChecked());

            editor.commit();
        }
    }

    private void setSharedPreferences() {

        String emailLogin = sharedPreferences.getString("emailLogin", "");
        String passwordLogin = sharedPreferences.getString("passwordLogin", "");
        boolean rememberLogin = sharedPreferences.getBoolean("rememberLogin", false);

        loginETEmail.setText(emailLogin);
        loginETPassword.setText(passwordLogin);
        if(rememberLogin){
            loginCbRemember.setChecked(true);
        }
    }

}