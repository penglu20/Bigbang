//package com.shang.xposed.forcetouch;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.shang.xposed.R;
//import com.shang.xposed.setting.StatusBarCompat;
//
//import java.util.HashMap;
//
///**
// * Created by wangyan-pd on 2016/11/25.
// */
//
//public class ForceTouchActivity extends AppCompatActivity {
//    private static final String TAG = "MainActivity";
//    public static final String PRESSURE = "pressure";
//    private TextView pressureText;
//    private EditText editText;
//    private TextView currentPressureText;
//    private float currentPressure;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_forcetouch_setting);
//        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.primary_dark);
//
////        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.xposed_forcetouch);
//        pressureText = (TextView) findViewById(R.id.pressureText);
//        currentPressureText = (TextView) findViewById(R.id.current_pressureText);
//        pressureText.setText("Last Pressure: " + SPHelper.getFloat(PRESSURE, 0.0f) + "");
//        editText = (EditText) findViewById(R.id.edit_text);
//        editText.setSelection(editText.getText().toString().length());
//        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(editText.getText().toString())) {
//                    Toast.makeText(ForceTouchActivity.this, R.string.tap_screen, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                SPHelper.save(PRESSURE, Float.parseFloat(editText.getText().toString()));
//                Toast.makeText(ForceTouchActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
//                finish();
//
//            }
//        });
//        findViewById(R.id.save_current_pressure).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentPressure == 0) {
//                    Toast.makeText(ForceTouchActivity.this, R.string.tap_screen, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                SPHelper.save(PRESSURE, currentPressure);
//                Toast.makeText(ForceTouchActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
//                finish();
//
//            }
//        });
//
//        final ForceTouchListener forceTouchListener = new ForceTouchListener(this, 70, 0.27f, false, true, new Callback() {
//            @Override
//            public void onForceTouch() {
//                functionToInvokeOnForceTouch();
//            }
//
//            @Override
//            public void onNormalTouch() {
//                functionToInvokeOnNormalTouch();
//            }
//        });
//        getWindow().getDecorView().getRootView().setOnTouchListener(forceTouchListener);
//
//        schedule(forceTouchListener);
//
//    }
//
//    private void hideKeyboard() {
//
//        if (editText != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//        }
//    }
//
//    private void showKeyboard() {
//        if (editText != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(editText, 0);
//        }
//    }
//
//
//    private void schedule(final ForceTouchListener forceTouchListener) {
//
//        TaskScheduler timer = new TaskScheduler();
//        timer.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                currentPressureText.setText("Current Pressure: " + forceTouchListener.getPressure());
//                currentPressure = forceTouchListener.getPressure();
//            }
//        }, 10);
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return (super.onOptionsItemSelected(item));
//    }
//
//    /**
//     * Method invoked on ForceTouch detected
//     */
//    private void functionToInvokeOnForceTouch() {
//        Log.e(TAG, "Function invoked forced!");
//    }
//
//
//    /**
//     * Method invoked on NormalTouch detected
//     */
//    private void functionToInvokeOnNormalTouch() {
//        Log.e(TAG, "Function invoked normal!");
//    }
//
//
//    class TaskScheduler extends Handler {
//        private HashMap<Runnable, Runnable> tasks = new HashMap<>();
//
//        public void scheduleAtFixedRate(final Runnable task, long delay, final long period) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    task.run();
//                    postDelayed(this, period);
//                }
//            };
//            tasks.put(task, runnable);
//            postDelayed(runnable, delay);
//        }
//
//        public void scheduleAtFixedRate(final Runnable task, final long period) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    task.run();
//                    postDelayed(this, period);
//                }
//            };
//            tasks.put(task, runnable);
//            runnable.run();
//        }
//
//        public void stop(Runnable task) {
//            Runnable removed = tasks.remove(task);
//            if (removed != null) removeCallbacks(removed);
//        }
//
//        public void stopAll() {
//            if (tasks != null)
//                tasks.clear();
//        }
//    }
//}
