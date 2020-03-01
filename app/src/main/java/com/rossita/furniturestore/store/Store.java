package com.rossita.furniturestore.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rossita.furniturestore.Login;
import com.rossita.furniturestore.R;
import com.rossita.furniturestore.cart.ShoppingCart;
import com.rossita.furniturestore.furniture.Furniture;
import com.rossita.furniturestore.utilities.FurnitureStoreApplication;
import com.rossita.furniturestore.utilities.MyToast;

public class Store extends AppCompatActivity implements StoreAdapter.ItemClicked {

    Activity activity;
    RecyclerView rvCategoriesList;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Store");
        activity = this;

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        rvCategoriesList = findViewById(R.id.rvCategoriesList);
        layoutManager = new GridLayoutManager(this,3);
        rvCategoriesList.setLayoutManager(layoutManager);
        myAdapter = new StoreAdapter(activity, FurnitureStoreApplication.categories);
        rvCategoriesList.setAdapter(myAdapter);

        findViewById(R.id.tvContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyDialogTheme);
                View dialogView = getLayoutInflater().from(activity).inflate(R.layout.dialog_contact_view,null);
                builder.setView(dialogView);
                dialogView.findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.CALL_PHONE},FurnitureStoreApplication.CALL_PERMISSION);
                        }else{
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:123456789")));
                        }
                    }
                });
                dialogView.findViewById(R.id.btnSms).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.fromParts("sms","123456789",null)));
                    }
                });
                dialogView.findViewById(R.id.btnEmail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.fromParts("mailto","furniturestore@mail.com",null)));
                    }
                });
                dialogView.findViewById(R.id.btnGps).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FurnitureStoreApplication.CALL_PERMISSION);
                        }else{
                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("google.navigation:q=32.0845879,34.7987008")));
                            }else{
                                MyToast.showToast("Please enable GPS");
                            }
                        }
                    }
                });
                builder.show();
            }
        });

        findViewById(R.id.fabLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyDialogTheme);
                View dialogView = getLayoutInflater().from(activity).inflate(R.layout.dialog_logout_view,null);
                builder.setView(dialogView)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                             @Override
                            public void onClick(DialogInterface dialog, int which) {
                                 tvLoad.setText("Signing out...please wait...");
                                 showProgress(true);
                                 Backendless.UserService.logout(new AsyncCallback<Void>() {
                                     @Override
                                     public void handleResponse(Void response) {
                                         startActivity(new Intent(activity,Login.class));
                                         activity.finish();
                                     }

                                     @Override
                                     public void handleFault(BackendlessFault fault) {
                                        MyToast.showToast(fault.getMessage());
                                     }
                                 });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FurnitureStoreApplication.CALL_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:123456789")));
            else
                ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.CALL_PHONE},FurnitureStoreApplication.CALL_PERMISSION);
        }else if (requestCode == FurnitureStoreApplication.GPS_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("google.navigation:q=32.0845879,34.7987008")));
                }else{
                    MyToast.showToast("Please enable GPS");
                }
            }else
                ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},FurnitureStoreApplication.GPS_PERMISSION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(Store.this, ShoppingCart.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int index) {
        Intent intent = new Intent(activity,Furniture.class);
        intent.putExtra("category",FurnitureStoreApplication.categories.get(index).getCategoryName());
        startActivity(intent);
    }

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
}
