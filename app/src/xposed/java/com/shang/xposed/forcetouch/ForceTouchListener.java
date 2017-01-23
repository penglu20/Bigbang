package com.shang.xposed.forcetouch;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Michele on 21/10/2016.
 */

/**
 * Force Touch Listener is a class that provide listener to implement Force Touch with Android
 */
public class ForceTouchListener implements View.OnTouchListener {
    //Tag for logcat
    private static final String TAG = "ForceTouchListener";
    //Deafult pressure limit
    private static final float DEFAULT_PRESSURE_LIMIT = 0.27f;
    //Default millis to vibrate
    private static final long DEFAULT_MILLIS_TO_VIBRATE = 70;
    //Local variable
    private Context context;
    private long millisToVibrate;
    private float pressureLimit;
    private float pressure;
    //Callback method
    private Callback forceTouchExecution;
    //Default isProgressive false
    private boolean isProgressive = false;
    private boolean isVibrate = true;

    private TaskScheduler timer = new TaskScheduler();
    private Runnable runnable;
    private boolean alreadyExecuted = false;

    /**
     * Public constructor with Context, millisToVibrate and pressureLimit
     * @param context Context
     * @param millisToVibrate long
     * @param pressureLimit float
     * @param forceTouchExecution Callback
     */
    public ForceTouchListener(Context context, long millisToVibrate, float pressureLimit, boolean isVibrate, Callback forceTouchExecution)
    {
        this.context = context;
        this.millisToVibrate = millisToVibrate;
        this.pressureLimit = pressureLimit;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = false;
        this.isVibrate = isVibrate;
    }

    /**
     * Public constructor with Context and pressureLimit, millisToVibrate is setted to 70ms
     * @param context Context
     * @param pressureLimit float
     * @param forceTouchExecution Callback
     */
    public ForceTouchListener(Context context, float pressureLimit, boolean isVibrate, Callback forceTouchExecution)
    {
        this.context = context;
        this.millisToVibrate = DEFAULT_MILLIS_TO_VIBRATE;
        this.pressureLimit = pressureLimit;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = false;
        this.isVibrate = isVibrate;
    }

    /**
     * Public constructor with Context and millisToVibrate, pressureLimit is setted to 0.27
     * @param context Context
     * @param millisToVibrate long
     * @param forceTouchExecution Callback
     */
    public ForceTouchListener(Context context, long millisToVibrate, boolean isVibrate, Callback forceTouchExecution)
    {
        this.context = context;
        this.millisToVibrate = millisToVibrate;
        this.pressureLimit = DEFAULT_PRESSURE_LIMIT;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = false;
        this.isVibrate = isVibrate;
    }

    /**
     * Public constructor with only Context, millisToVibrate and pressureLimit is setted by default
     * @param context Context
     * @param forceTouchExecution Callback
     */
    public ForceTouchListener(Context context, boolean isVibrate, Callback forceTouchExecution){
        this.context = context;
        this.millisToVibrate = DEFAULT_MILLIS_TO_VIBRATE;
        this.pressureLimit = DEFAULT_PRESSURE_LIMIT;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = false;
        this.isVibrate = isVibrate;
    }

    /**
     * Public constructor with Context, millisToVibrate and pressureLimit
     * @param context Context
     * @param millisToVibrate long
     * @param pressureLimit float
     * @param forceTouchExecution Callback
     * @param isProgressive boolean
     */
    public ForceTouchListener(Context context, long millisToVibrate, float pressureLimit, boolean isProgressive, boolean isVibrate, Callback forceTouchExecution)
    {
        this.context = context;
        this.millisToVibrate = millisToVibrate;
        this.pressureLimit = pressureLimit;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = isProgressive;
        this.isVibrate = isVibrate;
    }

    /**
     * Public constructor with Context and pressureLimit, millisToVibrate is setted to 70ms
     * @param context Context
     * @param pressureLimit float
     * @param forceTouchExecution Callback
     * @param isProgressive boolean
     */
    public ForceTouchListener(Context context, float pressureLimit, boolean isProgressive, boolean isVibrate, Callback forceTouchExecution)
    {
        this.context = context;
        this.millisToVibrate = DEFAULT_MILLIS_TO_VIBRATE;
        this.pressureLimit = pressureLimit;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = isProgressive;
        this.isVibrate = isVibrate;
    }

    /**
     * Public constructor with Context and millisToVibrate, pressureLimit is setted to 0.27
     * @param context Context
     * @param millisToVibrate long
     * @param forceTouchExecution Callback
     * @param isProgressive boolean
     */
    public ForceTouchListener(Context context, long millisToVibrate, boolean isProgressive, boolean isVibrate, Callback forceTouchExecution)
    {
        this.context = context;
        this.millisToVibrate = millisToVibrate;
        this.pressureLimit = DEFAULT_PRESSURE_LIMIT;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = isProgressive;
        this.isVibrate = isVibrate;
    }

    /**
     * Public constructor with only Context, millisToVibrate and pressureLimit is setted by default
     * @param context Context
     * @param forceTouchExecution Callback
     * @param isProgressive boolean
     */
    public ForceTouchListener(Context context, boolean isProgressive, boolean isVibrate, Callback forceTouchExecution){
        this.context = context;
        this.millisToVibrate = DEFAULT_MILLIS_TO_VIBRATE;
        this.pressureLimit = DEFAULT_PRESSURE_LIMIT;
        this.forceTouchExecution = forceTouchExecution;
        this.isProgressive = isProgressive;
        this.isVibrate = isVibrate;
    }

    /**
     * Implemented ForceTouchListener
     * @param view VIew
     * @param motionEvent MotionEvent
     * @return boolean
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float pressure = motionEvent.getPressure();
        checkParam(pressureLimit, millisToVibrate);
        setPressure(pressure);
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(pressure >= pressureLimit && !alreadyExecuted && !isProgressive) {
                    if(isVibrate) {
                        Vibrator vibr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibr.vibrate(millisToVibrate);
                    }
                    forceTouchExecution.onForceTouch();
                    alreadyExecuted = true;
                }else if(isProgressive){
                    alreadyExecuted = false;
                    progressiveForceTouch();
                }else{
                    alreadyExecuted = false;
                    forceTouchExecution.onNormalTouch();
                    timer.stop(runnable);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if(isProgressive) {
                    timer.stop(runnable);
                }
                alreadyExecuted = false;
                break;
            case MotionEvent.ACTION_UP:
                if(isProgressive) {
                    timer.stop(runnable);
                }
                alreadyExecuted = false;
                break;
        }
        return false;
    }

    /**
     * Handle progressive pressure on the screen
     */
    private void progressiveForceTouch(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if(getPressure() >= pressureLimit && !alreadyExecuted && isProgressive) {
                    if(isVibrate) {
                        Vibrator vibr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibr.vibrate(millisToVibrate);
                    }
                    forceTouchExecution.onForceTouch();
                    alreadyExecuted = true;
                }
            }
        };
        timer.scheduleAtFixedRate(runnable, 1);
    }

    /**
     * Check param before continue
     * @param pressureLimit float
     * @param millisToVibrate long
     */
    private void checkParam(float pressureLimit, long millisToVibrate){
        if(pressureLimit < 0 && pressureLimit > 1){
            setPressureLimit(DEFAULT_PRESSURE_LIMIT);
            Log.e(TAG, "Invalid pressureLimit (float between 0 and 1), restored default: " + DEFAULT_PRESSURE_LIMIT);
        }if(millisToVibrate < 0){
            setMillisToVibrate(DEFAULT_MILLIS_TO_VIBRATE);
            Log.e(TAG, "Invalid millisToVibrate, restored default: " + DEFAULT_MILLIS_TO_VIBRATE + " millis");
        }
    }

    /**
     * Set millisToVibrate
     * @param millisToVibrate long
     */
    private void setMillisToVibrate(long millisToVibrate){ this.millisToVibrate = millisToVibrate; }

    /**
     * Set pressureLimit
     * @param pressureLimit float
     */
    private void setPressureLimit(float pressureLimit) { this.pressureLimit = pressureLimit; }

    /**
     * Set runtime pressure
     * @param pressure float
     */
    private void setPressure(float pressure){
        this.pressure = pressure;
    }

    /**
     * Get runtime pressure
     * @return float
     */
    public float getPressure(){
        return pressure;
    }

    /**
     * Get pressure limit
     * @return float
     */
    public float getPressureLimit(){ return pressureLimit; }

    /**
     * Get millis to vibrate
     * @return long
     */
    public long getMillisToVibrate(){ return millisToVibrate; }

    /**
     * Get isProgressive
     * @return boolean
     */
    public boolean isProgressive() {
        return isProgressive;
    }
}

/**
 * TaskScheduler class
 */
class TaskScheduler extends Handler {
    private ArrayMap<Runnable,Runnable> tasks = new ArrayMap<>();

    public void scheduleAtFixedRate(final Runnable task,long delay,final long period) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.run();
                postDelayed(this, period);
            }
        };
        tasks.put(task, runnable);
        postDelayed(runnable, delay);
    }

    public void scheduleAtFixedRate(final Runnable task,final long period) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.run();
                postDelayed(this, period);
            }
        };
        tasks.put(task, runnable);
        runnable.run();
    }

    public void stop(Runnable task) {
        Runnable removed = tasks.remove(task);
        if (removed!=null) removeCallbacks(removed);
    }

}