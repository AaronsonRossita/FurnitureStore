package com.rossita.furniturestore.utilities;

import android.view.View;
import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import com.rossita.furniturestore.R;

public class MyToast {

    private static Activity activity;

    public MyToast(Activity activity) {
        this.activity = activity;
    }

    public static void showToast(String s){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View toastView = inflater.inflate(R.layout.toast, (ViewGroup) activity.findViewById(R.id.linlay));
        TextView tvToast = toastView.findViewById(R.id.tvToast);
        tvToast.setText(s);
        android.widget.Toast toast = new android.widget.Toast(activity);
        toast.setDuration(android.widget.Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.setGravity(Gravity.BOTTOM ,0,0);
        toast.show();

    }
}
