package com.example.mychatchit.Fragment;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mychatchit.ChatActivity;
import com.example.mychatchit.Common.Common;
import com.example.mychatchit.Model.ChatInfoModel;
import com.example.mychatchit.Model.UserModel;
import com.example.mychatchit.R;
import com.example.mychatchit.ViewHolder.ChatInfoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatFragment extends Fragment {

    @BindView(R.id.recycler)
    public RecyclerView recycler;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    private FirebaseRecyclerAdapter adapter;
    private static ChatFragment instance;
    private Unbinder unbinder;


    public static ChatFragment getInstance() {
        if (instance == null) {
            synchronized (PeopleFragment.class) {
                if (instance == null) {
                    instance = new ChatFragment();
                }
            }
        }
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        initView(view);
        loadChatList();
        return view;
    }

    private void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
    }

    private void loadChatList(){
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.CHAT_LIST_REFERENCES)
                .child(Common.currentUser.getUid());
        FirebaseRecyclerOptions<ChatInfoModel> options = new FirebaseRecyclerOptions
                .Builder<ChatInfoModel>()
                .setQuery(query, ChatInfoModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ChatInfoModel, ChatInfoViewHolder>(options) {

            @NonNull
            @Override
            public ChatInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
                return new ChatInfoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatInfoViewHolder holder, int position, @NonNull ChatInfoModel model) {
                if(!adapter.getRef(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    holder.txt_name.setText((FirebaseAuth.getInstance().getCurrentUser().getUid()
                            .equals(model.getCreateId()) ? model.getFriendName() : model.getCreateName()));
                    String[] content = null;
                    if(!model.getLastMessage().equals("----"))
                        content = model.getLastMessage().split("----");
                    holder.txt_last_message.setText(content.length == 2 ? content[1] : "<Image>");
                    Picasso.get().load(model.getAvatar()).into(holder.img_avatar);
                    holder.txt_time.setText(simpleDateFormat.format(model.getLastUpdate()));
                    holder.itemView.setOnClickListener(v -> {
                        FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()
                                        .equals(model.getCreateId()) ? model.getFriendId() : model.getCreateId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            UserModel userModel = snapshot.getValue(UserModel.class);
                                            Common.chatUser = userModel;
                                            Common.chatUser.setUid(snapshot.getKey());

                                            String roomId = Common.generateChatRoomId(FirebaseAuth.getInstance()
                                                    .getCurrentUser().getUid(), Common.chatUser.getUid());
                                            Common.roomSelected = roomId;
                                            FirebaseMessaging.getInstance()
                                                    .subscribeToTopic(roomId)
                                                    .addOnSuccessListener(unused -> {
                                                        startActivity(new Intent(getContext(), ChatActivity.class));
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "[ERROR]: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                }else{
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
            }
        };

        adapter.startListening();
        recycler.setAdapter(adapter);
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