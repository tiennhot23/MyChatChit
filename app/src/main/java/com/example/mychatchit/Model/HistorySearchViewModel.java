package com.example.mychatchit.Model;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.mychatchit.Entity.HistorySearch;
import com.example.mychatchit.Repository.HistorySearchRepository;

import java.util.List;

public class HistorySearchViewModel extends AndroidViewModel {
    private HistorySearchRepository HistorySearchRepository;

    private final LiveData<List<HistorySearch>> listHistorySearch;

    public HistorySearchViewModel (Application application) {
        super(application);
        HistorySearchRepository = new HistorySearchRepository(application);
        listHistorySearch = HistorySearchRepository.getAll();
    }

    public LiveData<List<HistorySearch>> getAll(){
        return listHistorySearch;
    }

    public HistorySearch findHistorySearch(String uid){
        return HistorySearchRepository.findHistorySearch(uid);
    }

    public void insertAll(HistorySearch... historySearchs){
        HistorySearchRepository.insertAll(historySearchs);
    }

    public void insert(HistorySearch historySearch){
        HistorySearchRepository.insert(historySearch);
    }

    public void delete(HistorySearch historySearch){

        HistorySearchRepository.delete(historySearch);
    }

    public void update(HistorySearch historySearch){
        HistorySearchRepository.update(historySearch);
    }
}
