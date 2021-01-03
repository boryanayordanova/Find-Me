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
 * Created by Boryana on 7/18/2016.
 */
public class FriendRequestsVisualizeAdapter extends RecyclerView.Adapter<FriendRequestsVisualizeAdapter.ViewHolder> {

    private static Double currentLotitude;
    private static Double currentLatitude;

    private ArrayList<UsersItem> foundUsers;
    private final OnItemClickListener listenerAccept;
    private final OnItemClickListener listenerDecline;

    public interface OnItemClickListener {
        void onItemClick(UsersItem item, ImageButton acceptFriendship, ImageButton declineFriendship);
    }

    public FriendRequestsVisualizeAdapter(ArrayList<UsersItem> foundUsers, Double lot, Double lat, OnItemClickListener listenerAccept, OnItemClickListener listenerDecline) {
        this.foundUsers = foundUsers;
        this.listenerAccept = listenerAccept;
        this.listenerDecline = listenerDecline;
        this.currentLatitude = lat;
        this.currentLotitude = lot;

    }

    @Override
    public FriendRequestsVisualizeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_friend_requests_item, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendRequestsVisualizeAdapter.ViewHolder holder, final int position) {
        holder.mView.setBackgroundColor(Color.parseColor(foundUsers.get(position).getColor()));
        holder.bind(foundUsers.get(position), listenerAccept, listenerDecline);
    }

    @Override
    public int getItemCount() {
        return foundUsers.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Context context;

        public ImageButton acceptFriendship;
        public ImageButton declineFriendship;
        public TextView friendRequestName;
        public TextView friendRequestDistance;


        public ViewHolder(View view) {
            super(view);
            context = itemView.getContext();
            mView = view;

            acceptFriendship = (ImageButton) view.findViewById(R.id.acceptFriendship);
            declineFriendship = (ImageButton) view.findViewById(R.id.declineFriendship);
            friendRequestName = (TextView) view.findViewById(R.id.friendRequestName);
            friendRequestDistance = (TextView) view.findViewById(R.id.friendRequestDistance);

        }

        public void bind(final UsersItem item, final OnItemClickListener listenerAccept, final OnItemClickListener listenerDecline) {

            friendRequestName.setText(item.getUserName());
            if (currentLotitude == 0 || currentLatitude == 0 || item.getCoor_x() == null){
                friendRequestDistance.setText("??? км. далеч");
            } else {
                friendRequestDistance.setText(String.valueOf(item.distanceToMe(currentLatitude, currentLotitude)) + " км. далеч");
            }

            acceptFriendship.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerAccept.onItemClick(item, acceptFriendship, declineFriendship);
                }
            });

            declineFriendship.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerDecline.onItemClick(item, acceptFriendship, declineFriendship);
                }
            });

        }

    }

}

