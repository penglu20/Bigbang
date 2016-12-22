package com.forfan.bigbang.onestep;

import java.util.ArrayList;
import java.util.List;

public abstract class DataManager {
    private List<RecentUpdateListener> mListeners = new ArrayList<RecentUpdateListener>();
    public void addListener(RecentUpdateListener listener){
        mListeners.add(listener);
    }

    public void removeListener(RecentUpdateListener listener){
        mListeners.remove(listener);
    }

    protected void notifyListener(){
        for(RecentUpdateListener lis : mListeners){
            lis.onUpdate();
        }
    }

    public interface RecentUpdateListener {
        void onUpdate();
    }

}
