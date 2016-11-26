package com.shang.xposed.forcetouch;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;
import com.shang.xposed.R;

/**
 * Created by wangyan-pd on 2016/11/25.
 */

public class ForceTouchActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String PRESSURE = "pressure";
    private TextView pressureText;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcetouch_setting);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.primary_dark);
        hideKeyboard();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.xposed_forcetouch);
        pressureText = (TextView) findViewById(R.id.pressureText);
        editText = (EditText) findViewById(R.id.edit_text);
        editText.setText(SPHelper.getFloat(PRESSURE,0.0f)+"");
        editText.setSelection(editText.getText().toString().length());
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    Toast.makeText(ForceTouchActivity.this, R.string.tap_screen, Toast.LENGTH_SHORT).show();
                    return;
                }
                SPHelper.save(PRESSURE, Float.parseFloat(editText.getText().toString()));
                Toast.makeText(ForceTouchActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        final ForceTouchListener forceTouchListener = new ForceTouchListener(getApplicationContext(), 70, 0.27f, false, true, new Callback() {
            @Override
            public void onForceTouch() {
                functionToInvokeOnForceTouch();
            }

            @Override
            public void onNormalTouch() {
                functionToInvokeOnNormalTouch();
            }
        });
        getWindow().getDecorView().getRootView().setOnTouchListener(forceTouchListener);

//        isProgressive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                ForceTouchListener forceTouchListener = new ForceTouchListener(getApplicationContext(), 70, Float.valueOf(pressureLimit.getText().toString()), isChecked, isVibrate.isChecked(), new Callback() {
//                    @Override
//                    public void onForceTouch() {
//                        functionToInvokeOnForceTouch();
//                    }
//
//                    @Override
//                    public void onNormalTouch() {
//                        functionToInvokeOnNormalTouch();
//                    }
//                });
//                getWindow().getDecorView().getRootView().setOnTouchListener(forceTouchListener);
//                schedule(forceTouchListener);
//            }
//        });
//
//        isVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                ForceTouchListener forceTouchListener = new ForceTouchListener(getApplicationContext(), 70, Float.valueOf(pressureLimit.getText().toString()), isProgressive.isChecked(), isChecked, new Callback() {
//                    @Override
//                    public void onForceTouch() {
//                        functionToInvokeOnForceTouch();
//                    }
//
//                    @Override
//                    public void onNormalTouch() {
//                        functionToInvokeOnNormalTouch();
//                    }
//                });
//                getWindow().getDecorView().getRootView().setOnTouchListener(forceTouchListener);
//                schedule(forceTouchListener);
//            }
//        });
//
//        pressureLimit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (keyEvent.getAction() == KeyEvent.ACTION_DOWN))) {
//                    ForceTouchListener forceTouchListener = new ForceTouchListener(getApplicationContext(), 70, Float.valueOf(pressureLimit.getText().toString()), isProgressive.isChecked(), isVibrate.isChecked(), new Callback() {
//                        @Override
//                        public void onForceTouch() {
//                            functionToInvokeOnForceTouch();
//                        }
//
//                        @Override
//                        public void onNormalTouch() {
//                            functionToInvokeOnNormalTouch();
//                        }
//                    });
//                    getWindow().getDecorView().getRootView().setOnTouchListener(forceTouchListener);
//                    schedule(forceTouchListener);
//                    hideKeyboard();
//                }
//                return true;
//            }
//        });
        schedule(forceTouchListener);

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void schedule(final ForceTouchListener forceTouchListener) {
        pressureText.setText("" + forceTouchListener.getPressureLimit());
        TaskScheduler timer = new TaskScheduler();
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                pressureText.setText("Pressure: " + forceTouchListener.getPressure());
                editText.setText(forceTouchListener.getPressure() + "");
                editText.setSelection(editText.getText().toString().length());

            }
        }, 1);

    }

    /**
     * Method invoked on ForceTouch detected
     */
    private void functionToInvokeOnForceTouch() {
        Log.e(TAG, "Function invoked forced!");
    }


    /**
     * Method invoked on NormalTouch detected
     */
    private void functionToInvokeOnNormalTouch() {
        Log.e(TAG, "Function invoked normal!");
    }


    class TaskScheduler extends Handler {
        private ArrayMap<Runnable, Runnable> tasks = new ArrayMap<>();

        public void scheduleAtFixedRate(final Runnable task, long delay, final long period) {
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

        public void scheduleAtFixedRate(final Runnable task, final long period) {
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
            if (removed != null) removeCallbacks(removed);
        }
    }
}
