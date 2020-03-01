package com.rossita.furniturestore.furniture;

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
import android.view.WindowManager;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.rossita.furniturestore.Login;
import com.rossita.furniturestore.R;
import com.rossita.furniturestore.cart.ShoppingCart;
import com.rossita.furniturestore.cart.ShoppingCartItem;
import com.rossita.furniturestore.utilities.FurnitureStoreApplication;
import com.rossita.furniturestore.utilities.MyToast;

import java.util.List;

public class Furniture extends AppCompatActivity implements FurnitureAdapter.ItemClicked {

    List<Item> rvList;
    Activity activity;
    String whereClause;
    RecyclerView rvFurniture;
    DataQueryBuilder queryBuilder;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    private TextView tvLoad;
    private View mProgressView;
    private View mLoginFormView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture);

        activity = this;
        tvLoad = findViewById(R.id.tvLoad);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        String category = getIntent().getStringExtra("category");
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(category);


        rvFurniture = findViewById(R.id.rvFurnitureList);
        layoutManager = new LinearLayoutManager(this);
        rvFurniture.setLayoutManager(layoutManager);

        if (category.equals("All")){
            whereClause = "furniture = '" + category + "'";
        }else{
            whereClause = "category = '" + category + "'";
        }

        queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setSortBy("price");

        showProgress(true);
        tvLoad.setText("Loading the items...please wait...");

        Backendless.Persistence.of(Item.class).find(queryBuilder, new AsyncCallback<List<Item>>() {
            @Override
            public void handleResponse(List<Item> response) {
                rvList = response;
                myAdapter = new FurnitureAdapter(activity,response);
                rvFurniture.setAdapter(myAdapter);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showProgress(false);
                MyToast.showToast(fault.getMessage());
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
                            ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.CALL_PHONE}, FurnitureStoreApplication.CALL_PERMISSION);
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


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(Furniture.this, ShoppingCart.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store,menu);
        return super.onCreateOptionsMenu(menu);
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
    public void onItemCLicked(int index) {
        final ShoppingCartItem item = new ShoppingCartItem(rvList.get(index).getPrice(),rvList.get(index).getImg(),rvList.get(index).getName(),
                rvList.get(index).getCategory(),1,FurnitureStoreApplication.user.getEmail());

        final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("name = '" + item.getName() + "'");
        tvLoad.setText("Adding the item to your cart...please wait...");
        showProgress(true);
        Backendless.Data.find(ShoppingCartItem.class, queryBuilder, new AsyncCallback<List<ShoppingCartItem>>() {
            @Override
            public void handleResponse(List<ShoppingCartItem> response) {
                if (response.isEmpty()){
                    Backendless.Data.save(item, new AsyncCallback<ShoppingCartItem>() {
                        @Override
                        public void handleResponse(ShoppingCartItem response) {
                            showProgress(false);
                            MyToast.showToast(response.getName() + " was added to your cart");
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            showProgress(false);
                            MyToast.showToast(fault.getMessage());
                        }
                    });
                }else{
                    tvLoad.setText("Updating item quantity in your cart...please wait...");
                    String whereClause = "name = '" + item.getName() + "' and userEmail = '" + FurnitureStoreApplication.user.getEmail() + "'";
                    item.setQuantity(response.get(0).getQuantity()+1);
                    Backendless.Data.of(ShoppingCartItem.class).remove(whereClause, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse(Integer response) {
                            Backendless.Persistence.of(ShoppingCartItem.class).save(item, new AsyncCallback<ShoppingCartItem>() {
                                @Override
                                public void handleResponse(ShoppingCartItem response) {
                                    MyToast.showToast(item.getName() + " quantity was updated");
                                    showProgress(false);
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    showProgress(false);
                                    MyToast.showToast(fault.getMessage());
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            showProgress(false);
                            MyToast.showToast(fault.getMessage());
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showProgress(false);
                MyToast.showToast(fault.getMessage());
            }
        });

    }

}
