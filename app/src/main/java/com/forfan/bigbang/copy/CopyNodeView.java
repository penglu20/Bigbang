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

    public CopyNodeView(Context context, CopyNode copyNode, CopyActivity.OnCopyNodeViewClickCallback clickCallback) {
        super(context);
        this.bound = copyNode.getBound();
        this.content = copyNode.getContent();
        this.setContentDescription(this.content);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state;
                if(!selected) {
                    state = true;
                } else {
                    state = false;
                }
                setActiveState(state);
                clickCallback.onCopyNodeViewClick((CopyNodeView)v, state);
            }
        });
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean state;
                if(!selected) {
                    state = true;
                } else {
                    state = false;
                }
                setActiveState(state);
                clickCallback.onCopyNodeViewLongClick((CopyNodeView)v, state);
                return true;
            }
        });
        setActiveState(false);
    }

    public void addToFrameLayout(FrameLayout frameLayout, int height) {
        LayoutParams var3 = new LayoutParams(this.bound.width(), this.bound.height());
        var3.leftMargin = this.bound.left;
        var3.topMargin = Math.max(0, this.bound.top - height);
        var3.width = this.bound.width();
        var3.height = this.bound.height();
        frameLayout.addView(this, 0, var3);
    }

    public String getText() {
        return this.content;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setChecked(this.selected);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setCheckable(true);
        info.setChecked(this.selected);
    }

    public void setActiveState(boolean state) {
        this.selected = state;
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
