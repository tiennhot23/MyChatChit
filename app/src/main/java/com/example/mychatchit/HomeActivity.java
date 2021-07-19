package com.example.mychatchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @BindView(R.id.navigation_view)
    NavigationView navigationView;


    private LinearLayout headerMenu;
    private RoundedImageView img_avatar;
    private TextView txt_edit_profile;
    private Switch btn_dark_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }
    private void init(){
        ButterKnife.bind(this);
        navigationView.setNavigationItemSelectedListener(this);
        btn_dark_mode = (Switch) navigationView.getMenu().findItem(R.id.dark_mode).getActionView();
        headerMenu = navigationView.getHeaderView(0).findViewById(R.id.header_menu);
        img_avatar = headerMenu.findViewById(R.id.img_avatar);
        txt_edit_profile = headerMenu.findViewById(R.id.txt_edit_profile);
        img_avatar.setOnClickListener(this);
        txt_edit_profile.setOnClickListener(this);
        btn_dark_mode.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.about_me:{
                Toast.makeText(this, "Not Available", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v == txt_edit_profile || v == img_avatar){
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return;
        }
        if(v == btn_dark_mode){
            switchToDarkMode();
            return;
        }
    }

    private void switchToDarkMode() {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            Toast.makeText(this, "Enable Dark Mode", Toast.LENGTH_SHORT).show();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btn_dark_mode.setChecked(true);
        }else{
            Toast.makeText(this, "Disable Dark Mode", Toast.LENGTH_SHORT).show();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btn_dark_mode.setChecked(false);
        }
    }
}