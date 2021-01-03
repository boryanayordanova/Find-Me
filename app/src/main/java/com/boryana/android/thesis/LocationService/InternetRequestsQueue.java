package com.boryana.android.thesis.LocationService;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Boryana on 7/23/2016.
 */
public class InternetRequestsQueue {

    private static InternetRequestsQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private InternetRequestsQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReviecer if someone passes one in
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }

        return mRequestQueue;
    }

    // ------ for manipulation ----------
    // for accsess to the singleton
    public static synchronized InternetRequestsQueue getInstance(Context context) {
        if (mInstance == null){
            mInstance = new InternetRequestsQueue(context);
        }

        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req){
        // against Timeout
        req.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }


}
