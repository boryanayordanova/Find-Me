package com.boryana.android.thesis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.boryana.android.thesis.LocationService.TrackLocationService;
import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar loadChangePassword;
    private User user;
    private EditText passwordOne;
    private EditText passwordTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = new User(getApplicationContext());
        getSupportActionBar().setTitle(user.getUserName().toString());

        loadChangePassword = (ProgressBar) findViewById(R.id.progressBarChangePassword);

        passwordOne = (EditText) findViewById(R.id.changePassOne);
        passwordTwo = (EditText) findViewById(R.id.changePassTwo);

        Button btnLogout = (Button) findViewById(R.id.buttonLogout);
        btnLogout.setOnClickListener(this);

        Button btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonLogout) {
            User userDetails = new User(getApplicationContext());
            userDetails.clearAll();

            // stop straccting service
            Intent intent = new Intent(this, TrackLocationService.class);
            stopService(intent);

            finish(); // finish sents also result of activity and closes MainActivity

        } else if (v.getId() == R.id.btnChangePassword){
            if(passwordOne.getText().length() == 0 || passwordTwo.getText().length() == 0 || !passwordOne.getText().toString().equals(passwordTwo.getText().toString())){
                new PersonalToastDesign(this).showToast("Несъответстващи пароли");
            }else{
                changeUsersPassword(user.getUserId(), passwordOne.getText().toString());
                passwordOne.setText("");
                passwordTwo.setText("");
            }
        }
    }

    private void changeUsersPassword(String user_id, String new_passwrod){
//        Log.e("Removing fiend", "From database and updating the adapter");
        new ServerApiRequests(this,

                new ServerApiRequests.AsyncStart(){

                    @Override
                    public void onProcessStart(){
                        loadChangePassword.setVisibility(View.VISIBLE);
                    }
                },

                new ServerApiRequests.AsyncResponse() {

                    @Override
                    public void onProcessFinish(String output) {
                        Log.e("response", output);

                        try {
                            JSONObject result = new JSONObject(output);

                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            String state = result.getString("success");
                            if(state.equals("true")) {
                                tst.showToast("Парола успешно сменена.");
                            } else {
                                String r = result.getString("error");
                                if(r.equals("Couldn\'t update password. Try again later.")){
                                    tst.showToast("Паролата не се актуализира. Моля опитайте отново по-късно");
                                }
                                if(r.equals("User not found")){
                                    tst.showToast("Потребителят не беше намерен");
                                }
                                if(r.equals("Please provide user id and password.")){
                                    tst.showToast("Моля въведете потребител и парола");
                                }
                                //tst.showToast(result.getString("error"));
                            }
//
                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            tst.showToast("Възникна проблем със сървъра");
                        }
                        loadChangePassword.setVisibility(View.GONE);
                    }
                }

        ).execute("change_password_url", user_id, new_passwrod);
    }

}
