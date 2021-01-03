package com.boryana.android.thesis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.boryana.android.thesis.Adapters.SearchUsersVisualizeAdapter;
import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.Models.UsersItem;
import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class SearchUsersActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView searchedUsers;
    private EditText searchStringUsers;
    private User user;
    private ProgressBar loadingSearchUsers;

    private SearchUsersVisualizeAdapter searchUsersVisualizeAdapter;
    final ArrayList<UsersItem> users_found = new ArrayList<UsersItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        getSupportActionBar().setTitle("Търсене на потребители");


        searchedUsers = (RecyclerView) findViewById(R.id.recyclerViewUsers);
        searchStringUsers = (EditText) findViewById(R.id.searchName);


        user = new User(getApplicationContext());
        loadingSearchUsers = (ProgressBar) findViewById(R.id.progressBarLoadingSearchUsers);

        Button searchForUsers = (Button) findViewById(R.id.buttonSearchForFriends);
        searchForUsers.setOnClickListener(this);

        searchUsersVisualizeAdapter =
                new SearchUsersVisualizeAdapter(
                        users_found,
                        new SearchUsersVisualizeAdapter.OnItemClickListener() {
                            @Override public void onItemClick(UsersItem item) {
                                sendFriendRequest(user.getUserId(), String.valueOf(item.getUserId()));
                            }
                        });
        searchedUsers.setAdapter(searchUsersVisualizeAdapter);


    }

    protected void onResume(){
        super.onResume();
       // searchUsersVisualizeAdapter.notifyDataSetChanged();
       // coloredLines();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSearchForFriends){
            if(searchStringUsers.getText().length() == 0){
                new PersonalToastDesign(getApplicationContext()).showToast("Моля въведете име за търсене");
            } else {
                searchForUsersNotMyFriends(user.getUserId(), searchStringUsers.getText().toString());
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_button_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close_acivity) {
            setResult(RESULT_OK, null);
            finish();;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchForUsersNotMyFriends(String user_id, String search_name){
        new ServerApiRequests(this,

                new ServerApiRequests.AsyncStart(){

                    @Override
                    public void onProcessStart(){
                        loadingSearchUsers.setVisibility(View.VISIBLE);
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
                                searchedUsers.setVisibility(View.VISIBLE);
//                                tst.showToast("Users found.");


                                JSONArray jsonUsers = result.getJSONArray("users");
                                users_found.clear();
                                for (int i = 0; i < jsonUsers.length(); i++) {

                                    UsersItem tmpItem = new UsersItem(
                                            jsonUsers.getJSONObject(i).getInt("ID")
                                            , jsonUsers.getJSONObject(i).getString("NAME")
                                            , ""
                                            , ""
                                            , "");


                                   users_found.add(tmpItem);
/*
                                    if (users_found.size() % 2 == 1)
                                        users_found.get(users_found.size() - 1).setColor("#f4efde");
                                    else
                                        users_found.get(users_found.size() - 1).setColor("#a6ae64");
*/
                                    coloredLines();
                                }

                                searchUsersVisualizeAdapter.notifyDataSetChanged();

                            } else {
                                String r = result.getString("error");
                                if(r.equals("No users found")){
                                    tst.showToast("Не бяха намерени потребители с това име");
                                }
                                if(r.equals("Please provide user id and search string")){
                                    tst.showToast("Моля задайте потребител");
                                }
                                //tst.showToast(result.getString("error"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            tst.showToast("Възникна грешка със сървъра");
                        }
                        loadingSearchUsers.setVisibility(View.GONE);
                    }
                }

        ).execute("find_users_url", user_id, search_name);
    }


    private void sendFriendRequest(final String user_id_one, final String user_id_two){ // one sends to two request
        new ServerApiRequests(this,

                new ServerApiRequests.AsyncStart(){

                    @Override
                    public void onProcessStart(){
//                        loadingSearchUsers.setVisibility(View.VISIBLE);

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
                                //tst.showToast("Беше изпратена заявка за приятелство");
                                for(int i = 0; i<users_found.size(); i++){

                                    if(users_found.get(i).getUserId() == Integer.valueOf(user_id_two)){
                                        users_found.remove(i);
                                        //searchForUsersNotMyFriends(user.getUserId(), searchStringUsers.getText().toString());
                                        tst.showToast("Беше изпратена заявка за приятелство");
                                        for(int i1 = 0; i1 < users_found.size(); i1+=2){

                                            users_found.get(i1).setColor("#f4efde");

                                            for(int i2 = 1; i2 < users_found.size(); i2+=2){

                                                users_found.get(i2).setColor("#a6ae64");
                                            }
                                        }

                                        break;

                                    }


                                }

                                //searchForUsersNotMyFriends(user.getUserId(), searchStringUsers.getText().toString());

                                searchUsersVisualizeAdapter.notifyDataSetChanged();





                            } else {
                                String r = result.getString("error");
                                if(r.equals("Friend request exists and its pending.")){
                                    tst.showToast("Потребителската заявка съществува и е в очакване за потвърждение");
                                }
                                if(r.equals("Friend request failed, please try again later")){
                                    tst.showToast("Приятелската заявка не беше осъществена, моля опитайте отново по-късно.");
                                }
                                if(r.equals("Please provide user id one and user id two.")){
                                    tst.showToast("Моля задайте потребител");
                                }
                                //tst.showToast(result.getString("error"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            tst.showToast("Възникна проблем със сървъра");
                        }
//                        loadingSearchUsers.setVisibility(View.GONE);
                    }
                }

        ).execute("send_friend_request_url", user_id_one, user_id_two);
    }


    private void coloredLines(){

        if (users_found.size() % 2 == 1)
            users_found.get(users_found.size() - 1).setColor("#f4efde");
        else
            users_found.get(users_found.size()  -1).setColor("#a6ae64");
    }
}
