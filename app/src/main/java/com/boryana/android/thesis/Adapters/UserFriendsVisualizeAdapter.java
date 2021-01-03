package com.boryana.android.thesis.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boryana.android.thesis.Models.UsersItem;
import com.boryana.android.thesis.R;

import java.util.ArrayList;

/**
 * Created by Boryana on 7/17/2016.
 */
public class UserFriendsVisualizeAdapter extends RecyclerView.Adapter<UserFriendsVisualizeAdapter.ViewHolder>  {

    private ArrayList<UsersItem> friendsArray;
    private final OnItemClickListener deleteListener;
    private final OnItemClickListener ItemListener;

    private static Double currentLotitude;
    private static Double currentLatitude;

    public interface OnItemClickListener {
        void onItemClick(UsersItem item);
    }


    public UserFriendsVisualizeAdapter(ArrayList<UsersItem> friendsArray, Double lot, Double lat, OnItemClickListener deleteListener, OnItemClickListener ItemListener) {
        this.friendsArray = friendsArray;
        this.deleteListener = deleteListener;
        this.ItemListener = ItemListener;
        this.currentLatitude = lat;
        this.currentLotitude = lot;
    }

    @Override
    public UserFriendsVisualizeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_user_friends_list_item, parent, false);



        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserFriendsVisualizeAdapter.ViewHolder holder, final int position) {
        holder.mView.setBackgroundColor(Color.parseColor(friendsArray.get(position).getColor()));
        holder.bind(friendsArray.get(position), deleteListener, ItemListener);
    }

    @Override
    public int getItemCount() {
        return friendsArray.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Context context;
        public ImageButton removeFriend;

        public TextView friendName;
        public TextView friendDistance;


        public ViewHolder(View view) {
            super(view);
            context = itemView.getContext();
            mView = view;

            removeFriend = (ImageButton) view.findViewById(R.id.removeFriend);

            friendName = (TextView) view.findViewById(R.id.friendName);
            friendDistance = (TextView) view.findViewById(R.id.friendDistance);
        }

        public void bind(final UsersItem item, final OnItemClickListener deleteListener,  final OnItemClickListener ItemListener) {

            friendName.setText(item.getUserName());
            if (currentLotitude == 0 || currentLatitude == 0 || item.getCoor_x() == null){
                friendDistance.setText("На ??? км. далеч");
            } else {
                friendDistance.setText("На " + String.valueOf(item.distanceToMe(currentLatitude, currentLotitude)) + " км. далеч");
            }

            removeFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onItemClick(item);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ItemListener.onItemClick(item);
                }
            });
        }

    }
}