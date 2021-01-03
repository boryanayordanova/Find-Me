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
public class SearchUsersVisualizeAdapter extends RecyclerView.Adapter<SearchUsersVisualizeAdapter.ViewHolder> {

    private ArrayList<UsersItem> foundUsers;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UsersItem item);
    }

    public SearchUsersVisualizeAdapter(ArrayList<UsersItem> foundUsers, OnItemClickListener listener) {
        this.foundUsers = foundUsers;
        this.listener = listener;

    }

    @Override
    public SearchUsersVisualizeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_search_users_item, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchUsersVisualizeAdapter.ViewHolder holder, final int position) {
        holder.mView.setBackgroundColor(Color.parseColor(foundUsers.get(position).getColor()));
        holder.bind(foundUsers.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return foundUsers.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Context context;

        public ImageButton sendFriendRequest;
        public TextView userName;


        public ViewHolder(View view) {
            super(view);
            context = itemView.getContext();
            mView = view;

            sendFriendRequest = (ImageButton) view.findViewById(R.id.sendFriendRequest);
            userName = (TextView) view.findViewById(R.id.foundUserName);

        }

        public void bind(final UsersItem item, final OnItemClickListener listener) {

            userName.setText(item.getUserName());

            sendFriendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

        }

    }

}