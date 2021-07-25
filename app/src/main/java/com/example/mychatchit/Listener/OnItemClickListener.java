package com.example.mychatchit.Listener;

import android.view.View;

import com.example.mychatchit.Model.UserModel;

public interface OnItemClickListener {
    void onPeopleClick(UserModel userModel);
    void onChatClick(UserModel userModel);
}
