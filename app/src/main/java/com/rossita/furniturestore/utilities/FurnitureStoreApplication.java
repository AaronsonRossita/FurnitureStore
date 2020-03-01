package com.rossita.furniturestore.utilities;

import java.util.List;
import android.Manifest;
import java.util.ArrayList;
import android.app.Application;
import com.backendless.Backendless;
import com.rossita.furniturestore.R;
import com.backendless.BackendlessUser;
import com.rossita.furniturestore.store.Category;
import com.rossita.furniturestore.cart.ShoppingCartItem;

public class FurnitureStoreApplication extends Application {

    public static final String APPLICATION_ID = "825B9F70-55F7-980B-FFFB-B6AB507D2800";
    public static final String API_KEY = "9DACDC70-4EC8-4C4C-AE1A-06FBF6460BE3";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Category> categories;
    public static ShoppingCartItem cartItem;

    public static final int GPS_PERMISSION = 1;
    public static final int CALL_PERMISSION = 2;
    public static final int MULTIPLE_PERMISSION = 3;
    public static final String[] permissions = {Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );

        Backendless.initApp(getApplicationContext(),FurnitureStoreApplication.APPLICATION_ID, FurnitureStoreApplication.API_KEY );

        categories = new ArrayList<Category>();
        categories.add(new Category("All",R.drawable.all));
        categories.add(new Category("Bed",R.drawable.category_bed));
        categories.add(new Category("Sofa",R.drawable.category_sofa));
        categories.add(new Category("Kids",R.drawable.category_kids));
        categories.add(new Category("Decor",R.drawable.category_decor));
        categories.add(new Category("Chair",R.drawable.category_chair));
        categories.add(new Category("Table",R.drawable.category_table));
        categories.add(new Category("Closet",R.drawable.category_closet));
        categories.add(new Category("Balcony",R.drawable.category_balcony));
        categories.add(new Category("Armchair",R.drawable.category_armchair));
        categories.add(new Category("Shelving",R.drawable.category_shelving));
        categories.add(new Category("Lighting",R.drawable.category_lighting));
        categories.add(new Category("Bathroom", R.drawable.category_bathroom));
    }

}

