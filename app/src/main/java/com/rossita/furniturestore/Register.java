package com.rossita.furniturestore;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rossita.furniturestore.store.Store;
import com.rossita.furniturestore.utilities.FurnitureStoreApplication;
import com.rossita.furniturestore.utilities.MyToast;

public class Register extends AppCompatActivity {

    EditText etNameReg, etEmailReg, etPassReg, etPassReenter;
    CheckBox showReg;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New User");
        new MyToast(Register.this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etNameReg = findViewById(R.id.etNameReg);
        etEmailReg = findViewById(R.id.etEmailReg);
        etPassReg = findViewById(R.id.etPassReg);
        etPassReenter = findViewById(R.id.etPassReenter);
        tvLoad = findViewById(R.id.tvLoad);
        showReg = findViewById(R.id.showReg);
        showReg.setOnCheckedChangeListener(listener);

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLoad.setText("Taking you back to login page...please wait...");
                showProgress(true);
                startActivity(new Intent(Register.this,Login.class));
                Register.this.finish();
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNameReg.getText().toString().isEmpty() || etEmailReg.getText().toString().isEmpty() ||
                        etPassReg.getText().toString().isEmpty() || etPassReenter.getText().toString().isEmpty()){
                    MyToast.showToast("Please enter all fields");
                }else if (!etPassReg.getText().toString().equals(etPassReenter.getText().toString())){
                    MyToast.showToast("Please make sure the passwords match");
                }else{
                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(etEmailReg.getText().toString().trim());
                    user.setPassword(etPassReg.getText().toString().trim());
                    user.setProperty("name",etNameReg.getText().toString().trim());

                    tvLoad.setText("Registering you online...please wait...");
                    showProgress(true);
                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            tvLoad.setText("Loading login page...please wait...");
                            MyToast.showToast("Registration successfull");
                            startActivity(new Intent(Register.this,Login.class));
                            finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            MyToast.showToast(fault.getMessage());
                            showProgress(false);
                        }
                    });
                }
            }
        });
    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked){
                etPassReg.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassReenter.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else{
                etPassReg.setInputType(InputType.TYPE_CLASS_TEXT);
                etPassReenter.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
        dialog.setMessage("Are you sure you want to leave ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyToast.showToast("Left");
                Register.super.onBackPressed();
                dialog.dismiss();
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
}
