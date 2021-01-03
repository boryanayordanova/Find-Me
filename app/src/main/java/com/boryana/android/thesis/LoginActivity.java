package com.boryana.android.thesis;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText ET_logName,ET_logPass;
    String logName,logPass;
    private ProgressBar loadingLogin;
    private PersonalToastDesign personalToastDesign;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Вход");
        personalToastDesign = new PersonalToastDesign(this);

        ET_logName = (EditText)findViewById(R.id.user_name);
        ET_logPass = (EditText)findViewById(R.id.user_pass);
        loadingLogin = (ProgressBar) findViewById(R.id.progressBarLoadingLogin);

//        Log.e("SOME RANDOM TEXT","DADAMMMM");
        User userDetails = new User(getApplicationContext());
        Log.e("userID",userDetails.getUserId());
        if(!userDetails.getUserId().equals("empty")){
            Intent intent = new Intent(this,MainScreenActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public void onClickLogin(View view){

        logName = ET_logName.getText().toString();
        logPass = ET_logPass.getText().toString();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            if(logName.equals("") && logPass.equals("")) {
                //Toast.makeText(getApplicationContext(), "Моля, попълнете всички полета", Toast.LENGTH_LONG).show();
                personalToastDesign.showToast("Моля, попълнете всички полета");
            }else if(logName.equals("")){
                //Toast.makeText(getApplicationContext(), "Моля, въведете име", Toast.LENGTH_LONG).show();
                personalToastDesign.showToast("Моля, въведете име");
            }else if(logPass.equals("")){
                //Toast.makeText(getApplicationContext(), "Моля, въведете парола", Toast.LENGTH_LONG).show();
                personalToastDesign.showToast("Моля, въведете парола");
            }else{

                String method = "login";

                new ServerApiRequests(this,

                        new ServerApiRequests.AsyncStart(){

                            @Override
                            public void onProcessStart(){
                                loadingLogin.setVisibility(View.VISIBLE);
                            }
                        },

                        new ServerApiRequests.AsyncResponse() {

                            @Override
                            public void onProcessFinish(String output) {
                                Log.e("responce login", output);


                                try {
                                    JSONObject result = new JSONObject(output);

                                    String state = result.getString("success");
                                    if(state.equals("true")){

                                        JSONObject myData = result.getJSONObject("user");
                                        User user = new User(getApplicationContext());
                                        user.addUserID(myData.getString("ID"));
                                        user.addUserName(myData.getString("NAME"));

                                        onResume();

                                    } else {
                                        PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                                        String r = result.getString("error");
                                        if(r.equals("Wrong password.")){
                                            tst.showToast("Въведената парола е грешна.");
                                        }
                                        if(r.equals("User not found")){
                                            tst.showToast("Потребителя не е намерен.");
                                        }
                                        if(r.equals("Please provide user name and password.")){
                                            tst.showToast("Моля задайте потребителско име и парола.");
                                        }
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                loadingLogin.setVisibility(View.GONE);
                            }
                        }

                ).execute(method, logName, logPass);
                //Toast.makeText(getApplicationContext(), logName+" "+logPass, Toast.LENGTH_LONG).show();


                ET_logName.setText("");
                ET_logPass.setText("");




            }
        }else{
            //network desable
            //Toast.makeText(getApplicationContext(), "Няма връзка с интернет", Toast.LENGTH_LONG).show();
            personalToastDesign.showToast("Няма връзка с интернет");
        }


    }

    public void onClickReg(View view)    {
        startActivity(new Intent(this, RegisterActivity.class));
        //startActivity(new Intent(this,MapsActivity.class));
    }


    @Override
    protected void onResume(){
        super.onResume();

        User userDetails = new User(getApplicationContext());
        if(!userDetails.getUserId().equals("empty")){
            Intent intent = new Intent(this,MainScreenActivity.class);
            startActivity(intent);
            finish();
        }
    }

}