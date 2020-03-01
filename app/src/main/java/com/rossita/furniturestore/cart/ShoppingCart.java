package com.rossita.furniturestore.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.rossita.furniturestore.Login;
import com.rossita.furniturestore.R;
import com.rossita.furniturestore.utilities.FurnitureStoreApplication;
import com.rossita.furniturestore.utilities.MyToast;

import java.util.List;
import java.util.Map;

public class ShoppingCart extends AppCompatActivity implements ShoppingCartAdapter.ItemClicked {

    Activity activity;
    String whereClause;
    RecyclerView rvCart;
    TextView tvCartTotal;
    DataQueryBuilder queryBuilder;
    RecyclerView.Adapter myAdapter;
    List<ShoppingCartItem> cartList;
    RecyclerView.LayoutManager layoutManager;

    private TextView tvLoad;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        tvLoad = findViewById(R.id.tvLoad);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(FurnitureStoreApplication.user.getProperty("name") + "'s shopping cart");
        activity = this;

        tvCartTotal = findViewById(R.id.tvCartTotal);

        rvCart = findViewById(R.id.rvCart);
        rvCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvCart.setLayoutManager(layoutManager);

        whereClause = "userEmail = '" + FurnitureStoreApplication.user.getEmail() + "'";
        queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setSortBy("price");

        showProgress(true);
        tvLoad.setText("Loading shopping cart items...please wait...");

        Backendless.Persistence.of(ShoppingCartItem.class).find(queryBuilder, new AsyncCallback<List<ShoppingCartItem>>() {
            @Override
            public void handleResponse(List<ShoppingCartItem> response) {
                cartList = response;
                int sum = 0;
                for (ShoppingCartItem item : cartList) {
                    sum += Integer.parseInt(item.getPrice()) * item.getQuantity();
                }
                tvCartTotal.setText(Integer.toString(sum )+ " ₪");
                myAdapter = new ShoppingCartAdapter(activity,response);
                rvCart.setAdapter(myAdapter);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showProgress(false);
                MyToast.showToast(fault.getMessage());
            }
        });



        findViewById(R.id.tvContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                                        startActivity(new Intent(activity, Login.class));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        showProgress(true);
        tvLoad.setText("Reloading your cart...please wait...");
        finish();
        startActivity(new Intent(this,ShoppingCart.class));
        return super.onOptionsItemSelected(item);
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
    public void onItemClicked(final int index) {

        View v = findViewById((index+1)*10);
        TextView tv = findViewById((index+1)*13);
        ImageView img = findViewById((index+1)*11);
        ScrollableNumberPicker np = findViewById((index+1)*12);

        if (img.getVisibility() == View.VISIBLE){
            img.setVisibility(View.GONE);
            if (img.getTag().toString().equals("checked")){
                showProgress(true);
                tvLoad.setText("Updating your cart...please wait...");
                String where = "name = '" + cartList.get(index).getName() + "' and userEmail = '" + FurnitureStoreApplication.user.getEmail() + "'";
                Backendless.Persistence.of(ShoppingCartItem.class).remove(where, new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        MyToast.showToast(cartList.get(index).getName() + " was removed");
                        tvLoad.setText("Reloading your cart...please wait...");
                        finish();
                        startActivity(new Intent(activity,ShoppingCart.class));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        MyToast.showToast(fault.getMessage());
                        showProgress(false);
                    }
                });
            }
            np.setVisibility(View.GONE);
            if (np.getValue() != Integer.parseInt(tv.getText().toString())){
                tvLoad.setText("Updating your cart...please wait...");
                showProgress(true);
                tv.setText(Integer.toString(np.getValue()));
                final ShoppingCartItem item = new ShoppingCartItem(cartList.get(index).getPrice(),cartList.get(index).getImg(),
                        cartList.get(index).getName(),cartList.get(index).getCategory(),np.getValue(),FurnitureStoreApplication.user.getEmail());
                String where = "name = '" + item.getName() + "' and userEmail = '" + FurnitureStoreApplication.user.getEmail() + "'";
                Backendless.Persistence.of(ShoppingCartItem.class).remove(where, new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        Backendless.Persistence.of(ShoppingCartItem.class).save(item, new AsyncCallback<ShoppingCartItem>() {
                            @Override
                            public void handleResponse(ShoppingCartItem response) {
                                MyToast.showToast(response.getName() + " quantity was updated");
                                showProgress(false);
                                finish();
                                startActivity(new Intent(activity,ShoppingCart.class));
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                MyToast.showToast(fault.getMessage());
                                showProgress(false);
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        MyToast.showToast(fault.getMessage());
                        showProgress(false);
                    }
                });
            }
            v.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }else{
            img.setVisibility(View.VISIBLE);
            np.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            v.setVisibility(View.INVISIBLE);
            np.setValue(Integer.parseInt(tv.getText().toString()));
        }

    }

}
