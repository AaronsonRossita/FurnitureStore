package com.rossita.furniturestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.rossita.furniturestore.furniture.Furniture;
import com.rossita.furniturestore.store.Store;
import com.rossita.furniturestore.utilities.FurnitureStoreApplication;
import com.rossita.furniturestore.utilities.MyToast;

import javax.security.auth.callback.PasswordCallback;

public class Login extends AppCompatActivity {

    Activity activity;
    CheckBox showLogin;
    EditText etEmailLogin, etPassLogin, etResetEmail;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sign in");
        new MyToast(Login.this);
        activity = this;

        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart",true);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        showLogin = findViewById(R.id.showLogin);
        showLogin.setOnCheckedChangeListener(listener);
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPassLogin = findViewById(R.id.etPassLogin);

        if (firstStart){
            requestPermission();
        }

        tvLoad.setText("Authenticating user...please wait...");
        showProgress(true);

        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if (response){
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            FurnitureStoreApplication.user = response;
                            startActivity(new Intent(activity,Store.class));
                            activity.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            MyToast.showToast(fault.getMessage());
                            showProgress(false);
                        }
                    });
                }else{
                    tvLoad.setText("Loading the sign in screen...please wait...");
                    showProgress(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                tvLoad.setText("Loading the sign in screen...please wait...");
                MyToast.showToast(fault.getMessage());
                showProgress(false);
            }
        });


        findViewById(R.id.btnNewUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLoad.setText("Loading registration form...please wait...");
                showProgress(true);
                startActivity(new Intent(activity,Register.class));
                activity.finish();
            }
        });

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmailLogin.getText().toString().isEmpty() || etPassLogin.getText().toString().isEmpty()){
                    MyToast.showToast("Please enter all fields");
                }else{
                    tvLoad.setText("Signing you in...please wait...");
                    showProgress(true);
                    Backendless.UserService.login(etEmailLogin.getText().toString().trim(), etPassLogin.getText().toString().trim(), new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            FurnitureStoreApplication.user = response;
                            tvLoad.setText("Loading store page...please wait...");
                            MyToast.showToast("Sign in successful");
                            startActivity(new Intent(activity, Store.class));
                            activity.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            showProgress(false);
                            MyToast.showToast(fault.getMessage());
                        }
                    },true);
                }
            }
        });

        findViewById(R.id.tvReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this,R.style.MyDialogTheme);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_view,null);
                etResetEmail = dialogView.findViewById(R.id.etResetEmail);
                dialog.setView(dialogView)
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                             @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    if (etResetEmail.getText().toString().isEmpty()){
                                        MyToast.showToast("Please enter your email");
                                    }else{
                                        tvLoad.setText("Sending you the link...please wait..");
                                        showProgress(true);
                                        Backendless.UserService.restorePassword(etResetEmail.getText().toString().trim(), new AsyncCallback<Void>() {
                                            @Override
                                            public void handleResponse(Void response) {
                                                showProgress(false);
                                                MyToast.showToast("Email sent");
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                showProgress(false);
                                                 MyToast.showToast(fault.getMessage());
                                            }
                                        });
                                    }
                                }
                            })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(Login.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Login.this,FurnitureStoreApplication.permissions,FurnitureStoreApplication.MULTIPLE_PERMISSION);
        }

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }



    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked)
                etPassLogin.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                etPassLogin.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity,R.style.MyDialogTheme);
        dialog.setMessage("Are you sure you want to leave ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FurnitureStoreApplication.MULTIPLE_PERMISSION){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                MyToast.showToast("Call permission denied");
            }if (grantResults[1] != PackageManager.PERMISSION_GRANTED){
                MyToast.showToast("Sms permission denied");
            }if (grantResults[2] != PackageManager.PERMISSION_GRANTED){
                MyToast.showToast("GPS permission denied");
            }else{
                MyToast.showToast("All permissions granted");
            }
        }
    }
}