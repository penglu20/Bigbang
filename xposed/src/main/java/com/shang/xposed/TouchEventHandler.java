package com.shang.xposed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dim on 16/10/23.
 */

public class TouchEventHandler {

    static {

        if (Build.VERSION.SDK_INT >= 21) {
            TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();
        } else {
            TOP_SORTED_CHILDREN_COMPARATOR = null;
        }
    }

    public static int BIG_BANG_RESPONSE_TIME = 1000;
    public static int INVALID_INTERVAL = 60;

    private static final String TAG = "TouchEventHandler";
    private static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;

    private final List<View> topmostChildList = new ArrayList<>();
    private View mCurrentView;


    public boolean hookTouchEvent(View v, MotionEvent event, List<Filter> filters, boolean needVerify, int anInt) {
        BIG_BANG_RESPONSE_TIME = anInt;
        boolean handle = false;
        // Log.e("shang","event:"+event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View targetTextView = getTargetTextView(v, event, filters);
            if (targetTextView != null) {
                Logger.logClass(TAG, targetTextView.getClass());
                long preClickTimeMillis = getClickTimeMillis(targetTextView);
                long currentTimeMillis = System.currentTimeMillis();
                if (preClickTimeMillis != 0) {
                    long interval = currentTimeMillis - preClickTimeMillis;
                    if (interval < INVALID_INTERVAL) {
                        return false;
                    }
                    if (interval < BIG_BANG_RESPONSE_TIME) {
                        String msg = null;
                        for (Filter filter : filters) {
                            msg = filter.getContent(targetTextView);
                            if (msg != null) {
                                break;
                            }
                        }
                        if (msg != null && (needVerify || verifyText(msg))) {
                            if(mCurrentView != null && targetTextView != mCurrentView)
                                return false;

                            handle = true;
                            Context context = targetTextView.getContext();
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("forbigBang://?extra_text=" + msg));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                targetTextView.setTag(R.id.bigBang_$$, currentTimeMillis);
                // setClickTimeMillis(targetTextView, currentTimeMillis);
                mCurrentView = targetTextView;
            }
        }
        return handle;
    }

    public boolean hookAllTouchEvent(View v, MotionEvent event, List<Filter> filters, boolean needVerify, int anInt) {
        BIG_BANG_RESPONSE_TIME = anInt;
        boolean handle = false;
        //  Log.e("shang","event:"+event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View targetTextView = getTargetTextView(v, event, filters);
            if (targetTextView != null) {
                Logger.logClass(TAG, targetTextView.getClass());
                long preClickTimeMillis = getClickTimeMillis(targetTextView);
                long currentTimeMillis = System.currentTimeMillis();
                if (preClickTimeMillis != 0) {
                    long interval = currentTimeMillis - preClickTimeMillis;
                    if (interval < INVALID_INTERVAL) {
                        return false;
                    }
                    if (interval < BIG_BANG_RESPONSE_TIME) {
                        String msg = null;
                        for (Filter filter : filters) {
                            msg = filter.getContent(targetTextView);
                            if (msg != null) {
                                break;
                            }
                        }
                        if (msg != null && (needVerify || verifyText(msg))) {
                            handle = true;
                            Context context = targetTextView.getContext();
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("forbigBang://?extra_text=" + msg));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                targetTextView.setTag(R.id.bigBang_$$, 0);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                targetTextView.setTag(R.id.bigBang_$$, currentTimeMillis);
                // setClickTimeMillis(targetTextView, currentTimeMillis);

            }
        }
        return handle;
    }

//    public boolean hookForceTouchEvent(View v, MotionEvent event, final List<Filter> filters, final boolean needVerify) {
//        final boolean[] handle = {false};
//        final View targetTextView = getTargetTextView(v, event, filters);
//        if (targetTextView != null) {
//            Global.init(v.getContext());
//            ForceTouchListener forceTouchListener = new ForceTouchListener(v.getContext(), 70, SPHelper.getFloat(ForceTouchActivity.PRESSURE, 1000.0f), true, true, new Callback() {
//                @Override
//                public void onForceTouch() {
//                    Logger.logClass(TAG, targetTextView.getClass());
//                    String msg = null;
//                    for (Filter filter : filters) {
//                        msg = filter.getContent(targetTextView);
//                        if (msg != null) {
//                            break;
//                        }
//                    }
//                    if (msg != null && (needVerify || verifyText(msg))) {
//                        Context context = targetTextView.getContext();
//                        try {
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("forbigBang://?extra_text=" + msg));
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    handle[0] = true;
//                }
//
//                @Override
//                public void onNormalTouch() {
//
//                }
//            });
//            forceTouchListener.onTouch(v, event);
//        }
//        return handle[0];
//    }

    private boolean verifyText(String msg) {
        if (msg.length() > 20) {
            return true;
        }
        //中文大于5个
        if (hasChinaLength(msg) > 5) {
            return true;
            //英文大于4个单词
        } else if (hasEnglishLength(msg) > 4) {
            return true;
        }
        return false;
    }

    private int hasChinaLength(String text) {
        int length = 0;
        for (char c : text.toCharArray()) {
            //[\u4e00-\u9fbb]
            if (19968 <= c && c <= 40891) {
                length++;
            }
        }
        return length;
    }

    private int hasEnglishLength(String text) {
        int length = text.split("\\w+").length;
        return length;
    }

    public long getClickTimeMillis(View view) {
        Object preClickTimeMillis = view.getTag(R.id.bigBang_$$);
        if (preClickTimeMillis != null) {
            return (Long) preClickTimeMillis;
        }
        return 0;
    }

    public long getViewClickTimeMillis(View view) {
        Object preClickTimeMillis = view.getTag(R.id.bigBang_$);
        if (preClickTimeMillis != null) {
            return (Long) preClickTimeMillis;
        }
        return 0;
    }

    public void setClickTimeMillis(View view, long timeMillis) {
        view.setTag(R.id.bigBang_$$, timeMillis);
    }

    public void setViewClickTimeMillis(View view, long timeMillis) {
        view.setTag(R.id.bigBang_$, timeMillis);
    }

    private View getTargetTextView(View view, MotionEvent event, List<Filter> filters) {
        if (isOnTouchRect(view, event)) {
            if (view instanceof ViewGroup) {
                getTopSortedChildren((ViewGroup) view, topmostChildList);
                final int childCount = topmostChildList.size();
                for (int i = 0; i < childCount; i++) {
                    View child = topmostChildList.get(i);
                    if (isOnTouchRect(child, event)) {
                        if (child instanceof ViewGroup) {
                            return getTargetTextView(child, event, filters);
                        } else if (isValid(filters, child))
                            return child;
                    }
                }
            } else {
                if (isOnTouchRect(view, event) && isValid(filters, view)) {
                    return view;
                }
            }

        }
        return null;
    }

    private boolean isValid(List<Filter> filters, View view) {

        for (Filter filter : filters) {
            if (filter.filter(view)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnTouchRect(View view, MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        Rect rect = new Rect();
        rect.set(xy[0], xy[1], xy[0] + view.getWidth(), xy[1] + view.getHeight());
        return rect.contains(rawX, rawY);
    }


    /**
     * Sorts child views with higher Z values to the beginning of a collection.
     */
    static class ViewElevationComparator implements Comparator<View> {
        @Override
        public int compare(View lhs, View rhs) {
            final float lz = ViewCompat.getZ(lhs);
            final float rz = ViewCompat.getZ(rhs);
            if (lz > rz) {
                return -1;
            } else if (lz < rz) {
                return 1;
            }
            return 0;
        }
    }

    private void getTopSortedChildren(ViewGroup viewGroup, List<View> out) {
        out.clear();
        //todo 因为系统的限制不能再非ViewGroup 中调用 isChildrenDrawingOrderEnabled 和 isChildrenDrawingOrderEnabled 方法。所以这里暂时注释掉了
//        final boolean useCustomOrder = viewGroup.isChildrenDrawingOrderEnabled();
        final int childCount = viewGroup.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
//             int childIndex = useCustomOrder ? viewGroup.isChildrenDrawingOrderEnabled(childCount, i) : i;
            int childIndex = i;
            final View child = viewGroup.getChildAt(childIndex);
            if (child.getVisibility() == View.VISIBLE) {
                out.add(child);
            }
        }

        if (TOP_SORTED_CHILDREN_COMPARATOR != null) {
            Collections.sort(out, TOP_SORTED_CHILDREN_COMPARATOR);
        }
    }

}
