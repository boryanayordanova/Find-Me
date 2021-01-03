package com.boryana.android.thesis;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.boryana.android.thesis.Adapters.FriendRequestsVisualizeAdapter;
import com.boryana.android.thesis.Models.User;
import com.boryana.android.thesis.Models.UsersItem;
import com.boryana.android.thesis.ToastDesign.PersonalToastDesign;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ListIterator;


public class FriendRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFriendRequests;
    private FriendRequestsVisualizeAdapter friendRequestsVisualizeAdapter;
    private ProgressBar updateRequest;
    private User user;

    final ArrayList<UsersItem> friendRequestsArray = new ArrayList<UsersItem>();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        getSupportActionBar().setTitle("Заявки за приятелство");

        user = new User(getApplicationContext());

        recyclerViewFriendRequests = (RecyclerView) findViewById(R.id.recyclerViewFriendRequests);
        updateRequest = (ProgressBar) findViewById(R.id.progressBarUpdateRequest);

        Log.e("in new acrtivity", MainScreenActivity.friendRequestsJSON);
        friendRequestsArray.clear();



        try {
            JSONObject response = new JSONObject(MainScreenActivity.friendRequestsJSON);
            JSONArray friendRequests = response.getJSONArray("users");

            for (int i = 0; i < friendRequests.length(); i++) {
                UsersItem tmpItem = new UsersItem(
                        friendRequests.getJSONObject(i).getInt("ID")
                        , friendRequests.getJSONObject(i).getString("NAME")
                        , friendRequests.getJSONObject(i).getString("COOR_X") // lotitude
                        , friendRequests.getJSONObject(i).getString("COOR_Y") // latitude
                        , friendRequests.getJSONObject(i).getString("REC_DATE"));


                friendRequestsArray.add(tmpItem);


                if (friendRequestsArray.size() % 2 == 1)
                    friendRequestsArray.get(friendRequestsArray.size() - 1).setColor("#f4efde");
                else
                    friendRequestsArray.get(friendRequestsArray.size() - 1).setColor("#a6ae64");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        friendRequestsVisualizeAdapter = new FriendRequestsVisualizeAdapter(
                friendRequestsArray,
                MainScreenActivity.LotitudeCoords, // lotitude
                MainScreenActivity.LatitudeCoords, // latitude
                new FriendRequestsVisualizeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(UsersItem item, ImageButton acceptFriendship, ImageButton declineFriendship) {
//                        acceptFriendship.setEnabled(false);
//                        declineFriendship.setEnabled(false);
                        setRequestResponse(item, String.valueOf(item.getUserId()), user.getUserId(), "1");
                        PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                        tst.showToast("Приятелството е прието");

                    }
                },
                new FriendRequestsVisualizeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(UsersItem item, ImageButton acceptFriendship, ImageButton declineFriendship) {
//                        acceptFriendship.setEnabled(false);
//                        declineFriendship.setEnabled(false);
                        setRequestResponse(item, String.valueOf(item.getUserId()), user.getUserId(), "0");
                        PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                        tst.showToast("Приятелството е отхвърлено");
                    }
                }
        );
        recyclerViewFriendRequests.setAdapter(friendRequestsVisualizeAdapter);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        friendRequestsVisualizeAdapter.notifyDataSetChanged();




    }

    public void setRequestResponse(final UsersItem item, String user_id_one, String user_id_two, String answer) {
        new ServerApiRequests(getApplicationContext(),

                new ServerApiRequests.AsyncStart() {

                    @Override
                    public void onProcessStart() {
                        updateRequest.setVisibility(View.VISIBLE);
                    }
                },

                new ServerApiRequests.AsyncResponse() {

                    @Override
                    public void onProcessFinish(String output) {
                        Log.e("response", output);

                        try {
                            JSONObject result = new JSONObject(output);
//
                            String state = result.getString("success");
                            if (state.equals("true")) {

                                for (int i = 0; i < friendRequestsArray.size(); i++) {

                                    if (friendRequestsArray.get(i).getUserId() == item.getUserId()) {
                                        friendRequestsArray.remove(i);

                                        for(int i1 = 0; i1 < friendRequestsArray.size(); i1+=2){

                                            friendRequestsArray.get(i1).setColor("#f4efde");

                                            for(int i2 = 1; i2 < friendRequestsArray.size(); i2+=2){

                                                friendRequestsArray.get(i2).setColor("#a6ae64");
                                            }
                                        }

                                        break;
                                    }

                                }


                                friendRequestsVisualizeAdapter.notifyDataSetChanged();



//                                if(friendRequestsArray.size() == 0) finish();

                            } else {
                                PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                                String r = result.getString("error");
                                if (r.equals("Updated not suckessfull")) {
                                    tst.showToast("Приятелството не можа да се осъществи");
                                }
                                if (r.equals("Remove not suckessfull")) {
                                    tst.showToast("Премахването не можа да се осъществи");
                                }
                                if (r.equals("Wrong parameters")) {
                                    tst.showToast("Грешни параметри");
                                }
                                if (r.equals("No connection between users")) {
                                    tst.showToast("Няма връзка между потребителите");
                                }
                                if (r.equals("Please provide user id one, user id two and answer to request")) {
                                    tst.showToast("Моля въведете потребител и отговор на заявката");
                                }
                                tst.showToast(result.getString("error"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            PersonalToastDesign tst = new PersonalToastDesign(getApplicationContext());
                            tst.showToast("Възникна проблем със сървъра");
                        }
                        updateRequest.setVisibility(View.GONE);
                    }
                }

        ).execute("set_friend_request_response_url", user_id_one, user_id_two, answer);



    }



    public void colored() {
/*
            if (friendRequestsArray.size() % 2 == 1)
                friendRequestsArray.get(friendRequestsArray.size() -1).setColor("#a6ae64");
            else
                friendRequestsArray.get(friendRequestsArray.size() -1).setColor("#f4efde");
                */
        if (friendRequestsArray.size() % 2 == 1)
            friendRequestsArray.get(friendRequestsArray.size() - 1).setColor("#f4efde");
        else
            friendRequestsArray.get(friendRequestsArray.size() - 1).setColor("#a6ae64");
        }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FriendRequests Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.boryana.android.thesis/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FriendRequests Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.boryana.android.thesis/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
