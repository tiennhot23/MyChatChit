package com.example.mychatchit.Repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.mychatchit.DAO.HistorySearchDao;
import com.example.mychatchit.Database.AppDatabase;
import com.example.mychatchit.Entity.HistorySearch;

import java.util.List;

public class HistorySearchRepository {
    private HistorySearchDao historySearchDao;
    private LiveData<List<HistorySearch>> listHistorySearch;

    public HistorySearchRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        historySearchDao = db.historySearchDao();
        listHistorySearch = historySearchDao.getAll();
    }

    public LiveData<List<HistorySearch>> getAll(){
        return listHistorySearch;
    }

    public HistorySearch findHistorySearch(String uid){
        return historySearchDao.findHistorySearch(uid);
    }

    public void insertAll(HistorySearch... historySearchs){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            historySearchDao.insertAll(historySearchs);
        });
    }

    public void insert(HistorySearch historySearch){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            historySearchDao.insert(historySearch);
        });
    }

    public void delete(HistorySearch historySearch){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            historySearchDao.delete(historySearch);
        });
    }

    public void update(HistorySearch historySearch){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            historySearchDao.update(historySearch);
        });
    }
}
