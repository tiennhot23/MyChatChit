package com.example.mychatchit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mychatchit.Common.Common;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OthersProfileActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.edt_first_name)
    public EditText edt_first_name;
    @BindView(R.id.edt_last_name)
    public EditText edt_last_name;
    @BindView(R.id.edt_phone)
    public EditText edt_phone;
    @BindView(R.id.edt_birthday)
    public EditText edt_birthday;
    @BindView(R.id.edt_status)
    public EditText edt_status;
    @BindView(R.id.edt_gender)
    public EditText edt_gender;
    @BindView(R.id.edt_home)
    public EditText edt_home;
    @BindView(R.id.edt_job)
    public EditText edt_job;
    @BindView(R.id.edt_bio)
    public EditText edt_bio;
    @BindView(R.id.img_avatar)
    public RoundedImageView img_avatar;
    @BindView(R.id.btn_chat)
    public Button btn_chat;
    @BindView(R.id.ic_gender)
    public ImageView ic_gender;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Calendar calendar = Calendar.getInstance();
    
    
    @OnClick(R.id.btn_chat)
    public void onChatClick(){
        startActivity(new Intent(OthersProfileActivity.this, ChatActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        init();
    }

    private void init(){
        ButterKnife.bind(this);

        //region set default components
        calendar.setTimeInMillis(Common.chatUser.getBirthday());
        edt_first_name.setText(Common.chatUser.getFirstName());
        edt_last_name.setText(Common.chatUser.getLastName());
        edt_phone.setText(Common.chatUser.getPhone());
        edt_birthday.setText(simpleDateFormat.format(calendar.getTime()));
        edt_gender.setText(Common.chatUser.getGender());
        edt_status.setText(Common.chatUser.getStatus());
        edt_home.setText(Common.chatUser.getHome());
        edt_job.setText(Common.chatUser.getJob());
        edt_bio.setText(Common.chatUser.getBio());
        if(edt_gender.getText().toString().toLowerCase().equals("nam"))
            ic_gender.setImageResource(R.drawable.ic_male);
        else ic_gender.setImageResource(R.drawable.ic_female);
        Picasso.get().load(Common.chatUser.getAvatar()).into(img_avatar);
        //endregion

        //region toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        //endregion
    }
}