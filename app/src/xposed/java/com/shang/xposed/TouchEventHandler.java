package com.shang.xposed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dim on 16/10/23.
 */

public class TouchEventHandler {
    private static final String TAG = "TouchEventHandler";

    static {

        if (Build.VERSION.SDK_INT >= 21) {
            TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();
        } else {
            TOP_SORTED_CHILDREN_COMPARATOR = null;
        }
    }

    public static int BIG_BANG_RESPONSE_TIME = 1000;
    public static int INVALID_INTERVAL = 60;

    private static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;

    private final List<View> topmostChildList = new ArrayList<>();
    private View mCurrentView;


    private Handler handler;
    private GestureDetector gestureDetector;
    private boolean hasTriggerLongClick=false;
    private boolean hasTriggerClick=false;
    private boolean hasTriggerDoubleClick=false;


    private boolean useClick=false;
    private boolean useLongClick=false;
    private boolean useDoubleClick=false;

    public void setUseClick(boolean useClick) {
        this.useClick = useClick;
    }

    public void setUseLongClick(boolean useLongClick) {
        this.useLongClick = useLongClick;
    }

    public void setUseDoubleClick(boolean useDoubleClick) {
        this.useDoubleClick = useDoubleClick;
    }

    private class LongPressedRunnable implements Runnable{
        private String text;
        private float x,y;

        public void setText(String text) {
            this.text = text;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setPosition(float x, float y){
            this.x=x;
            this.y=y;
        }

        @Override
        public void run() {
            if (!TextUtils.isEmpty(text)){
                try {
                    text = text.replace("%","\1");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("forbigBang://?extra_text=" + text.trim()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (mCurrentView!=null) {
                        mCurrentView.getContext().startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private LongPressedRunnable longPressedRunnable=new LongPressedRunnable();
    private int mScaledTouchSlop;

    public void hookOnClickListener(final View v, final List<Filter> filters){
        if (!useClick){
            return;
        }
        if (!hasTriggerClick) {
            hasTriggerClick=true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String text=getTextFromView(v,filters);
                    Log.e(TAG,"hookOnClickListener text="+text);
                    longPressedRunnable.setText(text);
                    longPressedRunnable.run();
                }
            });

        }
    }

    public void hookOnLongClickListener(final View v, final List<Filter> filters){
        if (!useLongClick){
            return;
        }
        if (!hasTriggerLongClick) {
            hasTriggerLongClick=true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String text=getTextFromView(v,filters);
                    longPressedRunnable.setText(text);
                    Log.e(TAG,"hookOnLongClickListener text="+text);
                    longPressedRunnable.run();
                }
            });
        }
    }

    private String getTextFromView(View v,List<Filter> filters){
        if (v instanceof ViewGroup){
            String text="";
            int chileCount = ((ViewGroup) v).getChildCount();
            for (int i=0;i<chileCount;i++){
                text +=getTextFromView(((ViewGroup) v).getChildAt(i),filters);
            }
            return text;
        }else {
            String tex = getTextInFilters(v,filters);
            if (tex!=null){
                return tex+"\n";
            }
            return "";
        }
    }

    private String getTextInFilters(View v,List<Filter> filters){
        for (Filter filter:filters){
            if (filter.filter(v)){
                return filter.getContent(v);
            }
        }
        return null;
    }

    public boolean hookTouchEvent(View v, MotionEvent event, final List<Filter> filters, boolean needVerify, int anInt) {
        hasTriggerLongClick=false;
        hasTriggerClick=false;
        hasTriggerDoubleClick=false;
        if (handler==null){
            handler=new Handler(Looper.getMainLooper());
        }
        if (gestureDetector==null){
            gestureDetector=new GestureDetector(v.getContext(),new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onSingleTapUp");
                    return super.onSingleTapUp(e);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onLongPress");
                    if (!useLongClick){
                        return;
                    }
                    if (!hasTriggerLongClick){
                        hasTriggerLongClick=true;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String text=getTextFromView(mCurrentView,filters);
                                Log.e(TAG,"onLongPress text="+text);
                                longPressedRunnable.setText(text);
                                longPressedRunnable.run();
                            }
                        });
                    }
                    super.onLongPress(e);
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    Log.e(TAG,"gestureDetector onScroll");
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    Log.e(TAG,"gestureDetector onFling");
                    return super.onFling(e1, e2, velocityX, velocityY);
                }

                @Override
                public void onShowPress(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onShowPress");
                    super.onShowPress(e);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onDown");
                    return super.onDown(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onDoubleTap");
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onDoubleTapEvent");
                    return super.onDoubleTapEvent(e);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onSingleTapConfirmed");
                    if (!useClick){
                        return false;
                    }
                    if (mCurrentView==null){
                        return false;
                    }
                    //这里如果触发单击则会影响到qq里的单击响应，估计也会影响其他应用的单击，应该要屏蔽掉设置了单击事件的view去除才行
//                    Boolean isSetOnClick = (Boolean) mCurrentView.getTag(R.id.bigBang_$_click);
//                    if (isSetOnClick!=null && isSetOnClick){
//                        return false;
//                    }
                    if (!hasTriggerClick){
                        hasTriggerClick=true;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String text = getTextFromView(mCurrentView, filters);
                                Log.e(TAG, "onSingleTapConfirmed text=" + text);
                                longPressedRunnable.setText(text);
                                longPressedRunnable.run();
                            }
                        });
                    }
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onContextClick(MotionEvent e) {
                    Log.e(TAG,"gestureDetector onContextClick");
                    return super.onContextClick(e);
                }
            });
        }
        gestureDetector.onTouchEvent(event);
        BIG_BANG_RESPONSE_TIME = anInt;
        boolean handle = false;
//        Log.e(TAG,"hookTouchEvent event:"+event);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (useLongClick){
                View targetTextView = getTargetTextView(v, event,filters);
                Log.e(TAG,"hookTouchEvent getTargetView:"+targetTextView);
                if (targetTextView!=null && targetTextView!=mCurrentView){
                    handler.removeCallbacks(longPressedRunnable);
                }
                mCurrentView=targetTextView;
                String msg = null;
                msg = getTextFromView(targetTextView,filters);
                Log.e(TAG,"hookTouchEvent getTextFromView:"+msg);
                if (msg != null && (needVerify || verifyText(msg))) {
                    longPressedRunnable.setText(msg);
                    longPressedRunnable.setPosition(event.getRawX(),event.getRawY());
                }
                handler.postDelayed(longPressedRunnable,1000);
                return true;
            }else if (useClick){
                View targetTextView = getTargetTextView(v, event,filters);
                mCurrentView=targetTextView;
            }
        }
        float currentX = event.getRawX();
        float currentY = event.getRawY();

        float x =longPressedRunnable.getX();
        float y=longPressedRunnable.getY();
        if (mScaledTouchSlop==0) {
            mScaledTouchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        }

        if (Math.abs(y - currentY) > mScaledTouchSlop || Math.abs(x - currentX) > mScaledTouchSlop) {
            handler.removeCallbacks(longPressedRunnable);
        }

        if (event.getAction() == MotionEvent.ACTION_CANCEL||event.getAction() == MotionEvent.ACTION_OUTSIDE){
            handler.removeCallbacks(longPressedRunnable);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handler.removeCallbacks(longPressedRunnable);
//            View targetTextView = getTargetTextView(v, event, filters);
            View targetTextView = getTargetTextView(v, event,filters);

            long currentTimeMillis = System.currentTimeMillis();
            if(targetTextView != mCurrentView) {
                mCurrentView=targetTextView;
                if (targetTextView!=null) {
                    targetTextView.setTag(R.id.bigBang_$$, currentTimeMillis);
                }
                return false;
            }
            if (!useDoubleClick){
                return handle;
            }
            if (targetTextView != null) {
                Logger.logClass(TAG, targetTextView.getClass());
                long preClickTimeMillis = getClickTimeMillis(targetTextView);
                if (preClickTimeMillis != 0) {
                    long interval = currentTimeMillis - preClickTimeMillis;
                    if (interval < INVALID_INTERVAL) {
                        return false;
                    }
                    if (interval < BIG_BANG_RESPONSE_TIME) {
                        String msg = null;
//                        for (Filter filter : filters) {
//                            msg = filter.getContent(targetTextView);
//                            if (msg != null) {
//                                break;
//                            }
//                        }
                        msg=getTextFromView(targetTextView,filters);
                        if (msg != null && (needVerify || verifyText(msg))) {

                            handle = true;
                            Context context = targetTextView.getContext();
                            try {
                                msg = msg.replace("%","\1");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("forbigBang://?extra_text=" + msg));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mCurrentView=null;
                            targetTextView.setTag(R.id.bigBang_$$, currentTimeMillis);
                            return handle;
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
          Log.e("shang","event:"+event);
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
                                msg = msg.replace("%","\1");
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

    private View getTargetView(View view, MotionEvent event) {
        if (isOnTouchRect(view, event)) {
            if (view instanceof ViewGroup) {
                getTopSortedChildren((ViewGroup) view, topmostChildList);
                final int childCount = topmostChildList.size();
                for (int i = 0; i < childCount; i++) {
                    View child = topmostChildList.get(i);
                    if (isOnTouchRect(child, event)) {
                        if (child instanceof ViewGroup) {
                            View target = getTargetView(child, event);
                            return target==null?view:target;
                        } else
                            return child;
                    }
                }
            } else {
                return view;
            }
        }
        return null;
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
            final float lz;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                lz = lhs.getZ();
            }else {
                lz = 0;
            }
            final float rz;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                rz = rhs.getZ();
            }else {
                rz = 0;
            }
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
