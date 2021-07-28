package com.example.mychatchit.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.mychatchit.Entity.HistorySearch;

import java.util.List;

@Dao
public interface HistorySearchDao {
    @Query("SELECT * FROM historysearch")
    LiveData<List<HistorySearch>> getAll();

    @Query("SELECT * FROM historysearch WHERE uid LIKE :uid LIMIT 1")
    HistorySearch findHistorySearch(String uid);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(HistorySearch... historysearchs);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistorySearch historysearch);

    @Delete
    void delete(HistorySearch historysearch);

    @Update
    void update(HistorySearch historysearch);
}
