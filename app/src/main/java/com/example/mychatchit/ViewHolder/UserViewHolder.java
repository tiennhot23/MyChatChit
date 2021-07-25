package com.example.mychatchit.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatchit.R;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.txt_name)
    public TextView txt_name;
    @BindView(R.id.img_avatar)
    public RoundedImageView img_avatar;
    @BindView(R.id.ic_chat)
    public ImageView ic_chat;

    private Unbinder unbinder;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        unbinder = ButterKnife.bind(this, itemView);
    }
}
