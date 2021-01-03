package com.boryana.android.thesis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.boryana.android.thesis.Adapters.UserFriendsVisualizeAdapter;
import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.Models.UsersItem;
import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserFriendsActivity extends AppCompatActivity {

    private static int RESULT_ACTIVITY_HOME_CODE = 1664;
    private static Context context;

    private RecyclerView recyclerViewEmailChain;
    UserFriendsVisualizeAdapter userFriendsVisualizeAdapter;
    final ArrayList<UsersItem> friendsArray = new ArrayList<UsersItem>();
    private ProgressBar loadingFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fiends);
        getSupportActionBar().setTitle("Приятели");
        friendsArray.clear();
        context = this;

        loadingFriends = (ProgressBar) findViewById(R.id.progressBarLoadFriendsList);
        recyclerViewEmailChain = (RecyclerView) findViewById(R.id.user_friends_list);

        userFriendsVisualizeAdapter =
                new UserFriendsVisualizeAdapter(
                        friendsArray,
                        MainScreenActivity.LotitudeCoords, // lotitude
                        MainScreenActivity.LatitudeCoords, // latitude

                        // delete button click listener
                        new UserFriendsVisualizeAdapter.OnItemClickListener() {
                            @Override public void onItemClick(UsersItem item) {

                                final UsersItem currentFriend = item;
                                final String user_id = String.valueOf(item.getUserId());
                                final String user_id_two = new User(context).getUserId(); // my id

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Сигурни ли сте че искате да премахнете "+ currentFriend.getUserName().toString()+" ?")
                                        .setTitle("Потвърждение");
                                builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.e("Sent emails click", "user_id");
                                        removeFriendRequest(currentFriend, user_id, user_id_two);


                                    }
                                });
                                builder.setNegativeButton("НЕ", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();


                            }
                        },
                        new UserFriendsVisualizeAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(UsersItem item) {

                                if (item.getCoor_y() ==null || item.getCoor_y() == null){
                                    new PersonalToastDesign(getApplicationContext()).showToast("Потребителя е с неизвестна локация");
                                }else{

                                    Intent showUserLocation = new Intent(UserFriendsActivity.this, ShowUserCurrentLocationActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("NAME", item.getUserName());
                                    bundle.putDouble("COOR_X", item.getCoor_x());
                                    bundle.putDouble("COOR_Y", item.getCoor_y());
                                    bundle.putString("REC_DATE", item.getRec_date());
                                    showUserLocation.putExtras(bundle);

                                    startActivity(showUserLocation);
                                }


                            }
                        });
        recyclerViewEmailChain.setAdapter(userFriendsVisualizeAdapter);


    }

    @Override
    protected void onResume(){
        super.onResume();
        User user = new User(getApplicationContext());
        getAllMyFriends(user.getUserId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.users_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_users) {
           Intent searchUsers = new Intent(this, SearchUsersActivity.class);
            startActivityForResult(searchUsers, RESULT_ACTIVITY_HOME_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == RESULT_ACTIVITY_HOME_CODE ) {
            if (resultCode == RESULT_OK){
                finish();
            }
        }
    }

    public void getAllMyFriends(String userId){
        new ServerApiRequests(this,

                new ServerApiRequests.AsyncStart(){

                    @Override
                    public void onProcessStart(){
                        loadingFriends.setVisibility(View.VISIBLE);
                    }
                },

                new ServerApiRequests.AsyncResponse() {

                    @Override
                    public void onProcessFinish(String output) {
                        Log.e("response", output);

                        try {
                            JSONObject result = new JSONObject(output);

                            String state = result.getString("success");
                            if(state.equals("true")){
//
                                JSONArray jsonUsers = result.getJSONArray("users");
                                friendsArray.clear();
                                for (int i = 0; i < jsonUsers.length(); i++) {

                                    UsersItem tmpItem = new UsersItem(
                                            jsonUsers.getJSONObject(i).getInt("ID")
                                            , jsonUsers.getJSONObject(i).getString("NAME")
                                            , jsonUsers.getJSONObject(i).getString("COOR_X")
                                            , jsonUsers.getJSONObject(i).getString("COOR_Y")
                                            , jsonUsers.getJSONObject(i).getString("REC_DATE"));


                                    friendsArray.add(tmpItem);

                                    if (friendsArray.size() % 2 == 1)
                                        friendsArray.get(friendsArray.size() - 1).setColor("#f4efde");
                                    else
                                        friendsArray.get(friendsArray.size() - 1).setColor("#a6ae64");

                                }

                                userFriendsVisualizeAdapter.notifyDataSetChanged();
                            } else {
                                PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                                String r = result.getString("error");
                                if(r.equals("No friends found")){
                                    tst.showToast("Не бяха намерени приятели");
                                }
                                if(r.equals("Please provide user id.")){
                                    tst.showToast("Моля въведете потребител");
                                }
                                //tst.showToast(result.getString("error"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            tst.showToast("Възникна грешка със сървъра.");
                        }
                        loadingFriends.setVisibility(View.GONE);
                    }
                }

        ).execute("get_all_friends_url", userId);
    }



    private void removeFriendRequest(final UsersItem item, String user_id_one, String user_id_two){
        Log.e("Removing fiend", "From database and updating the adapter");
        new ServerApiRequests(this,

                new ServerApiRequests.AsyncStart(){

                    @Override
                    public void onProcessStart(){
                        loadingFriends.setVisibility(View.VISIBLE);
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
                                //tst.showToast("Потребитялят вече не е ваш приятел");
                                for (int i = 0; i < friendsArray.size(); i++) {

                                    if (friendsArray.get(i).getUserId() == item.getUserId()){
                                        friendsArray.remove(i);
                                        tst.showToast("Премахването успешно");
                                        for(int i1 = 0; i1 < friendsArray.size(); i1+=2){

                                            friendsArray.get(i1).setColor("#f4efde");

                                            for(int i2 = 1; i2 < friendsArray.size(); i2+=2){

                                                friendsArray.get(i2).setColor("#a6ae64");
                                            }
                                        }

                                        break;
                                    }

                                }

                                userFriendsVisualizeAdapter.notifyDataSetChanged();


                            } else {
                                String r = result.getString("error");
                                if(r.equals("Remove not suckessfull")){
                                    tst.showToast("Премахването не беше успешно");
                                }
                                if(r.equals("Wrong parameters")){
                                    tst.showToast("Грешни параметри");
                                }
                                //tst.showToast(result.getString("error"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            tst.showToast("Възникна проблем със сървъра");
                        }
                        loadingFriends.setVisibility(View.GONE);
                    }
                }

        ).execute("unfriend_user_url", user_id_one, user_id_two);
    }

}
