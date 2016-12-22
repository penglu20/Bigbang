package com.forfan.bigbang.onestep.widget;

import android.view.View;

/**
 * Created by Gavin on 2016/10/19.
 */

public class DropView {

    private View targetView;
    private int x, y, w, h;

    public DropView(View targetView) {
        this.targetView = targetView;
    }

    public boolean isContains(float touchX, float touchY) {
        int[] position = new int[2];
        targetView.getLocationOnScreen(position);
        this.x = position[0];
        this.y = position[1];
        this.w = targetView.getWidth();
        this.h = targetView.getHeight();

        return touchX > x && touchX < x + w && touchY > y && touchY < y + h;
    }

}
