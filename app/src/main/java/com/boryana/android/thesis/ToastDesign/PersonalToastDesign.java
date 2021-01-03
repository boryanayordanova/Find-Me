package com.boryana.android.thesis.ToastDesign;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.boryana.android.thesis.R;

/**
 * Created by Boryana on 16.7.2016 г..
 */
public class PersonalToastDesign {

    private Context context;

    public PersonalToastDesign(Context context) {
        this.context = context;
    }



    public void showToast(String a) {
        Toast toast = Toast.makeText(context, a, Toast.LENGTH_SHORT);
        toast.getView().setPadding(15, 15, 15, 15);
        toast.getView().setMinimumWidth(30);
        toast.getView().setBackgroundResource(R.color.colorAccent);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.colorYellow));
        toast.show();
    }

}
