package com.example.mychatchit.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatchit.R;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatInfoViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.txt_name)
    public TextView txt_name;
    @BindView(R.id.txt_last_message)
    public TextView txt_last_message;
    @BindView(R.id.txt_time)
    public TextView txt_time;
    @BindView(R.id.img_avatar)
    public RoundedImageView img_avatar;

    private Unbinder unbinder;

    public ChatInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        unbinder = ButterKnife.bind(this, itemView);
    }
}
