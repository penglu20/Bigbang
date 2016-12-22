package com.forfan.bigbang.onestep;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;

public abstract class SidebarItem {
    public boolean newAdded = false;
    public boolean newRemoved = false;

    private int mIndex = -1;

    public void setIndex(int index) {
        mIndex = index;
    }

    int getIndex() {
        return mIndex;
    }

    public abstract CharSequence getDisplayName();

    public abstract Drawable getAvatar();

    public abstract void delete();

    public abstract boolean acceptDragEvent(Context context, DragEvent event);

    public abstract boolean handleDragEvent(Context context, DragEvent event);

    public abstract boolean openUI(Context context);
}
