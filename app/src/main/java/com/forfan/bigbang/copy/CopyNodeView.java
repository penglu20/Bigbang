package com.forfan.bigbang.copy;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.forfan.bigbang.R;

public class CopyNodeView extends View {
    private Rect bound;
    private String content;
    private boolean selected = false;

    public CopyNodeView(Context var1, CopyNode var2, CopyActivity.OnCopyNodeViewClickCallback var3) {
        super(var1);
        this.bound = var2.getBound();
        this.content = var2.getContent();
        this.setContentDescription(this.content);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean var2;
                if(!selected) {
                    var2 = true;
                } else {
                    var2 = false;
                }
                setActiveState(var2);
                var3.onCopyNodeViewClick((CopyNodeView)v, var2);
            }
        });
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean var2;
                if(!selected) {
                    var2 = true;
                } else {
                    var2 = false;
                }
                setActiveState(var2);
                var3.onCopyNodeViewLongClick((CopyNodeView)v, var2);
                return true;
            }
        });
        setActiveState(false);
    }

    public void addToFrameLayout(FrameLayout var1, int var2) {
        LayoutParams var3 = new LayoutParams(this.bound.width(), this.bound.height());
        var3.leftMargin = this.bound.left;
        var3.topMargin = Math.max(0, this.bound.top - var2);
        var3.width = this.bound.width();
        var3.height = this.bound.height();
        var1.addView(this, 0, var3);
    }

    public boolean a() {
        return this.selected;
    }

    public String getText() {
        return this.content;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent var1) {
        super.onInitializeAccessibilityEvent(var1);
        var1.setChecked(this.selected);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo var1) {
        super.onInitializeAccessibilityNodeInfo(var1);
        var1.setCheckable(true);
        var1.setChecked(this.selected);
    }

    public void setActiveState(boolean var1) {
        this.selected = var1;
        if(this.selected) {
            this.setBackgroundColor(this.getContext().getResources().getColor(R.color.quarter_transparent));
        } else {
//            this.setBackgroundColor(0);
            this.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.universal_copy_node_bg_n));
        }

        this.sendAccessibilityEvent(0);
        this.invalidate();
    }
}
