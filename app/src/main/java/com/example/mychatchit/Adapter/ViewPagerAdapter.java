package com.example.mychatchit.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mychatchit.Fragment.ChatFragment;
import com.example.mychatchit.Fragment.ExploreFragment;
import com.example.mychatchit.Fragment.PeopleFragment;
import com.example.mychatchit.R;

import butterknife.BindView;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return ChatFragment.getInstance();
            case 1:
                return PeopleFragment.getInstance();
            case 2:
                return ExploreFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
