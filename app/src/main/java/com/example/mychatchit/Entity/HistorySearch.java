package com.example.mychatchit.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HistorySearch {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    public String uid;

    public HistorySearch(@NonNull String uid) {
        this.uid = uid;
    }

    public HistorySearch(){

    }
}
