package com.example.mychatchit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatchit.ChatActivity;
import com.example.mychatchit.Common.Common;
import com.example.mychatchit.Model.UserModel;
import com.example.mychatchit.OthersProfileActivity;
import com.example.mychatchit.R;
import com.example.mychatchit.SearchActivity;
import com.example.mychatchit.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends FirebaseRecyclerAdapter<UserModel, UserViewHolder> implements Filterable {

    Context context;
    List<UserModel> peoples;
    public PeopleAdapter(@NonNull @NotNull FirebaseRecyclerOptions<UserModel> options, Context context, List<UserModel> peoples) {
        super(options);
        this.context = context;
        this.peoples = peoples;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull UserViewHolder holder, int position, @NonNull @NotNull UserModel model) {
        if(!this.getRef(position).getKey().equals(Common.currentUser.getUid())){
            holder.txt_name.setText(new StringBuilder().append(model.getFirstName()).append(" ")
                    .append(model.getLastName()).toString());
            Picasso.get().load(model.getAvatar()).into(holder.img_avatar);

            holder.itemView.setOnClickListener(v -> {
                Common.chatUser = model;
                Common.chatUser.setUid(this.getRef(position).getKey());
                context.startActivity(new Intent(context, OthersProfileActivity.class));
            });
            holder.ic_chat.setOnClickListener(v -> {
                Common.chatUser = model;
                Common.chatUser.setUid(this.getRef(position).getKey());
                String roomId = Common.generateChatRoomId(Common.currentUser.getUid(), Common.chatUser.getUid());
                Common.roomSelected = roomId;
                context.startActivity(new Intent(context, ChatActivity.class));
            });
        }else{
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }

        System.out.println(peoples.size());
    }

    @NonNull
    @NotNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.people_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return peoples.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence text) {
            FirebaseDatabase.getInstance().getReference().child(Common.USER_REFERENCES)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if(text != null || text.length() != 0){
                                if(snapshot.getValue(UserModel.class).getLastName().startsWith(text.toString())){

                                }
                            }else{

                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

            FilterResults results = new FilterResults();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            notifyDataSetChanged();
        }
    };
}