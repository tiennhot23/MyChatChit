package com.example.mychatchit.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.mychatchit.DAO.HistorySearchDao;
import com.example.mychatchit.Entity.HistorySearch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {HistorySearch.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HistorySearchDao historySearchDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "chat_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}