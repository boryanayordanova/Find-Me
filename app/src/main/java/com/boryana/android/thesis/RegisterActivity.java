package com.boryana.android.thesis;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class RegisterActivity extends AppCompatActivity {



    EditText ET_regName, ET_regPass1, ET_regPass2;
    String regName, regPass1, regPass2;
    Button doRegistration;
    ProgressBar registerLoading;
    private PersonalToastDesign personalToastDesign;


/*
    public void showToast(String a) {
        Toast toast = Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT);
        toast.getView().setPadding(15, 15, 15, 15);
        toast.getView().setMinimumWidth(30);
        toast.getView().setBackgroundResource(R.color.colorAccent);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(getResources().getColor(R.color.colorYellow));
        toast.show();
    }

*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Регистрация");
        personalToastDesign = new PersonalToastDesign(this);

        ET_regName = (EditText)findViewById(R.id.new_user_name);
        ET_regPass1 = (EditText)findViewById(R.id.new_user_pass1);
        ET_regPass2 = (EditText)findViewById(R.id.new_user_pass2);

        registerLoading = (ProgressBar) findViewById(R.id.progressBarRegister);

        doRegistration = (Button) findViewById(R.id.buttonDoRegistration);
        doRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                doRegistration.setEnabled(false);
                userReg(v);
            }
        });

    }

    public void userReg(View view){


        regName = ET_regName.getText().toString();
        regPass1 = ET_regPass1.getText().toString();
        regPass2 = ET_regPass2.getText().toString();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //network enable
            if(regName.isEmpty() && regPass1.equals("") && regPass2.equals("")){
                //Toast.makeText(getApplicationContext(), "Моля, попълнете полетата", Toast.LENGTH_LONG).show();
                personalToastDesign.showToast("Моля, попълнете всички полета");
                ET_regName.requestFocus();

            }else if(regName.equals("")){
                //Toast.makeText(getApplicationContext(), "Моля, въведете име", Toast.LENGTH_LONG).show();
                personalToastDesign.showToast("Моля, въведете име");
            }else if(regPass1.equals("") || regPass2.equals("")){
                //Toast.makeText(getApplicationContext(), "Моля, въведете парола", Toast.LENGTH_LONG).show();
                personalToastDesign.showToast("Моля, въведете парола");
            }else if(!regPass1.equals(regPass2)){
                //Toast.makeText(getApplicationContext(), "Въведената парола трябва да е една и съща", Toast.LENGTH_LONG).show();
                personalToastDesign.showToast("Въведената парола трябва да е една и съща");
            }else {

                String method = "register";
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                String currentDateTimeString = df.format(c.getTime());
                Log.e("current time", currentDateTimeString);

                ServerApiRequests backgroundTask = new ServerApiRequests(this,

                    new ServerApiRequests.AsyncStart(){

                        @Override
                        public void onProcessStart(){
                            registerLoading.setVisibility(View.VISIBLE);
                        }
                    }, new ServerApiRequests.AsyncResponse() {

                        @Override
                        public void onProcessFinish(String output) {
                            //Here you will receive the result fired from async class
                            //of onPostExecute(result) method.
                            Log.e("responce", output);
                            try {
                                JSONObject result = new JSONObject(output);

                                String state = result.getString("success");
                                if(state.equals("true")){

                                    personalToastDesign.showToast("Здравейте " + regName + " ,успешно се регистрирахте");
                                    finish();

                                } else {
                                    PersonalToastDesign tst = new  PersonalToastDesign(getApplicationContext());
                                    String r = result.getString("error");
                                    if(r.equals("User exists")){
                                       tst.showToast("Потребителят съществува");
                                    }
                                    if(r.equals("Registration failed, please try again later")){
                                        tst.showToast("Регистрацията се провали, моля опитайте отново по-късно");
                                    }
                                    if(r.equals("Please provide user name, password and registration date")){
                                        tst.showToast("Моля въведете име и парола");
                                    }
                                   // personalToastDesign.showToast(result.getString("error"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            registerLoading.setVisibility(View.GONE);
//                            doRegistration.setEnabled(true);
                        }
                    }
                );

                //backgroundTask.
                backgroundTask.execute(method, regName, regPass1, currentDateTimeString);


                ET_regName.setText("");
                ET_regPass1.setText("");
                ET_regPass2.setText("");


            }

        }else{
            //network desable
            //Toast.makeText(getApplicationContext(), "Няма връзка с интернет", Toast.LENGTH_LONG).show();
            personalToastDesign.showToast("Няма връзка с интернет");
        }
    }


}
