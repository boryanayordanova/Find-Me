package com.boryana.android.thesis.Models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Boryana on 16.7.2016 г..
 */
public class User {
//    private String ID;
//    private String name;

    private String USER_ID = "USER_ID";
    private String USER_NAME = "USER_NAME";

    private SharedPreferences sharedPreferences = null;
    private static String SHARED_PREF_NAME = "FIND_ME";

    public User(Context context){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        }
    }



    public void addUserID(String userId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public String getUserId(){
        String userId = null;
        userId = sharedPreferences.getString(USER_ID, "empty");
        return userId;
    }


    public void addUserName(String userName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public String getUserName(){
        String userName = null;
        userName = sharedPreferences.getString(USER_NAME, "empty");
        return userName;
    }

    public void clearAll(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

}
