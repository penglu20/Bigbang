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
        bound = copyNode.getBound();
        content = copyNode.getContent();
        setContentDescription(content);
        setOnClickListener(new OnClickListener() {
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
        setOnLongClickListener(new OnLongClickListener() {
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
        LayoutParams var3 = new LayoutParams(bound.width(), bound.height());
        var3.leftMargin = bound.left;
        var3.topMargin = Math.max(0, bound.top - height);
        var3.width = bound.width();
        var3.height = bound.height();
        frameLayout.addView(this, 0, var3);
    }

    public String getText() {
        return content;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setChecked(selected);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setCheckable(true);
        info.setChecked(selected);
    }

    public void setActiveState(boolean state) {
        selected = state;
        if(selected) {
            setBackgroundColor(getContext().getResources().getColor(R.color.quarter_transparent));
        } else {
//            setBackgroundColor(0);
            setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.universal_copy_node_bg_n));
        }

        sendAccessibilityEvent(0);
        invalidate();
    }
}
