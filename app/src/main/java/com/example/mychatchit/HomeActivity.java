package com.example.mychatchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatchit.Adapter.ViewPagerAdapter;
import com.example.mychatchit.Common.Common;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @BindView(R.id.navigation_view)
    public NavigationView navigationView;
    @BindView(R.id.viewpager)
    public ViewPager2 viewpager;
    @BindView(R.id.tab_layout)
    public TabLayout tabLayout;

    private LinearLayout headerMenu;
    private RoundedImageView img_avatar;
    private TextView txt_edit_profile;
    private Switch btn_dark_mode;


    int NightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init() {
        ButterKnife.bind(this);

        //region navigation menu
        navigationView.setNavigationItemSelectedListener(this);
        btn_dark_mode = (Switch) navigationView.getMenu().findItem(R.id.dark_mode).getActionView();
        headerMenu = navigationView.getHeaderView(0).findViewById(R.id.header_menu);
        img_avatar = headerMenu.findViewById(R.id.img_avatar);
        Picasso.get().load(Common.currentUser.getAvatar()).into(img_avatar);
        txt_edit_profile = headerMenu.findViewById(R.id.txt_edit_profile);
        img_avatar.setOnClickListener(this);
        txt_edit_profile.setOnClickListener(this);
        btn_dark_mode.setOnClickListener(this);
        //endregion

        //region main
        viewpager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {

            }

            @NonNull
            @Override
            public State getCurrentState() {
                return null;
            }
        }));
        new TabLayoutMediator(tabLayout, viewpager, (tab, position) -> {
            if(position == 0){
                tab.setText("Chat");
            }else if (position == 1){
                tab.setText("People");
            }else if (position == 2){
                tab.setText("Explore");
            }
        }).attach();
        //endregion

        //region night mode
        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        NightMode = sharedPreferences.getInt("NightModeInt", 1);
        AppCompatDelegate.setDefaultNightMode(NightMode);
        if (NightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            btn_dark_mode.setChecked(true);
        } else btn_dark_mode.setChecked(false);
        //endregion
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_me: {
                Toast.makeText(this, "Not Available", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == txt_edit_profile || v == img_avatar) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return;
        }
        if (v == btn_dark_mode) {
            switchToDarkMode();
            return;
        }
    }

    private void switchToDarkMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        NightMode = AppCompatDelegate.getDefaultNightMode();

        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putInt("NightModeInt", NightMode);
        editor.apply();
    }

}