package com.example.mychatchit.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatchit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatTextHolder extends RecyclerView.ViewHolder {
    private Unbinder unbinder;
    @BindView(R.id.txt_time)
    public TextView txt_time;
    @BindView(R.id.txt_translate)
    public TextView txt_translate;
    @BindView(R.id.txt_chat_message)
    public TextView txt_chat_message;
    public ChatTextHolder(@NonNull View itemView) {
        super(itemView);
        unbinder = ButterKnife.bind(this, itemView);
    }
}
