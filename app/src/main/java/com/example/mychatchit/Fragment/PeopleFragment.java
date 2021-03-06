package com.example.mychatchit.Fragment;

import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mychatchit.ChatActivity;
import com.example.mychatchit.Common.Common;
import com.example.mychatchit.Listener.OnItemClickListener;
import com.example.mychatchit.Model.UserModel;
import com.example.mychatchit.OthersProfileActivity;
import com.example.mychatchit.R;
import com.example.mychatchit.SearchActivity;
import com.example.mychatchit.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PeopleFragment extends Fragment{

    @BindView(R.id.search_view)
    public SearchView search_view;
    @BindView(R.id.recycler)
    public RecyclerView recyclerView;

    @OnClick(R.id.search_view)
    void onSeachViewClick(){
        startActivity(new Intent(getContext(), SearchActivity.class));
    }

    OnItemClickListener itemClickListener;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    private FirebaseRecyclerAdapter adapter;
    private static PeopleFragment instance;
    private Unbinder unbinder;


    public static PeopleFragment getInstance() {
        if (instance == null) {
            synchronized (PeopleFragment.class) {
                if (instance == null) {
                    instance = new PeopleFragment();
                }
            }
        }
        return instance;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.people_fragment, container, false);
        init(view);
        loadPeopleList();
        return view;
    }

    private void init(View view) {
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadPeopleList() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_REFERENCES);
        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<UserModel, UserViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull UserViewHolder holder, int position, @NonNull @NotNull UserModel model) {
                if(!adapter.getRef(position).getKey().equals(Common.currentUser.getUid())){
                    holder.txt_name.setText(new StringBuilder().append(model.getFirstName()).append(" ")
                            .append(model.getLastName()).toString());
                    Picasso.get().load(model.getAvatar()).into(holder.img_avatar);

                    holder.itemView.setOnClickListener(v -> {
                        Common.chatUser = model;
                        Common.chatUser.setUid(adapter.getRef(position).getKey());
                        startActivity(new Intent(getContext(), OthersProfileActivity.class));
                    });
                    holder.ic_chat.setOnClickListener(v -> {
                        Common.chatUser = model;
                        Common.chatUser.setUid(adapter.getRef(position).getKey());
                        String roomId = Common.generateChatRoomId(Common.currentUser.getUid(), Common.chatUser.getUid());
                        Common.roomSelected = roomId;
                        startActivity(new Intent(getContext(), ChatActivity.class));
                    });
                }else{
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        if(adapter != null) adapter.stopListening();
        super.onStop();
    }

}