package com.example.mychatchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatchit.Common.Common;
import com.example.mychatchit.Entity.HistorySearch;
import com.example.mychatchit.Model.HistorySearchViewModel;
import com.example.mychatchit.Model.UserModel;
import com.example.mychatchit.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.recycler)
    public RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    public EditText edt_search;
    @BindView(R.id.flow_layout)
    public FlowLayout flowLayout;

    private FirebaseRecyclerAdapter adapter;
    public HistorySearchViewModel historySearchViewModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
        loadPeopleList("");
    }

    private void init(){
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadPeopleList(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        historySearchViewModel = new ViewModelProvider(this).get(HistorySearchViewModel.class);
        historySearchViewModel.getAll().observe(this, historySearches -> {
            if(historySearches.size() > 5){
                historySearchViewModel.delete(historySearches.get(0));
            }
            flowLayout.removeAllViews();
            for(int i=0; i<historySearches.size(); i++){
                View view = LayoutInflater.from(this).inflate(R.layout.circle_people_item, null);
                RoundedImageView imageView = view.findViewById(R.id.img_avatar);
                TextView txt_name = view.findViewById(R.id.txt_name);
                FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES)
                        .child(historySearches.get(i).uid).get()
                        .addOnSuccessListener(dataSnapshot -> {
                            if(dataSnapshot.exists()){
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                userModel.setUid(dataSnapshot.getKey());
                                Picasso.get().load(userModel.getAvatar()).into(imageView);
                                txt_name.setText(userModel.getLastName());
                                view.setOnClickListener(v -> {
                                    Common.chatUser = userModel;
                                    startActivity(new Intent(SearchActivity.this, OthersProfileActivity.class));
                                });
                                flowLayout.addView(view);
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "[ERROR] load user failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void loadPeopleList(String text) {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_REFERENCES)
                .orderByChild("lastName").startAt(text.toUpperCase()).endAt(text.toLowerCase() + "\uf8ff");
        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, UserModel.class)
                        .build();
        if(adapter != null ) adapter.updateOptions(options);
        else{
            adapter = new FirebaseRecyclerAdapter<UserModel, UserViewHolder>(options) {
                @NonNull
                @NotNull
                @Override
                public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.people_item, parent, false);
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
                            historySearchViewModel.insert(new HistorySearch(Common.chatUser.getUid()));
                            startActivity(new Intent(SearchActivity.this, OthersProfileActivity.class));
                        });
                        holder.ic_chat.setOnClickListener(v -> {
                            Common.chatUser = model;
                            Common.chatUser.setUid(adapter.getRef(position).getKey());
                            String roomId = Common.generateChatRoomId(Common.currentUser.getUid(), Common.chatUser.getUid());
                            Common.roomSelected = roomId;
                            startActivity(new Intent(SearchActivity.this, ChatActivity.class));
                        });
                    }else{
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                    }
                }
            };
        }


        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}