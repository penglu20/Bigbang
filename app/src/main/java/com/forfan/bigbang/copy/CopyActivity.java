
package com.forfan.bigbang.copy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.shang.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CopyActivity extends BaseActivity {
    private FrameLayout copyNodeViewContainer;
    private FloatingActionButton copyFab;
    private FloatingActionButton exitFab;
    private FloatingActionButton exitFullScreenFab;
    private Menu menu;
    private List<CopyNodeView> selectedNodes;
    private OnCopyNodeViewClickCallback mOnCopyNodeViewClickCallback;
    private int contentHeight = 0;
    private int s = 0;
    private int actionBarHeight = 0;
    private BottomSheetDialog bottomSheetDialog;
    private boolean v = false;
    private boolean w = false;
    private boolean isFullScreen = false;

    public CopyActivity() {
    }

    private int a(TextView var1) {
        int var3 = (int) (var1.getHeight() + this.s + this.actionBarHeight - ViewUtil.dp2px( 44));
        int var2 = var3;
        if(var3 > this.contentHeight) {
            var2 = this.contentHeight;
        }

        return var2;
    }

    private void addCopyNodeView(CopyNode var1, int var2) {
        (new CopyNodeView(this, var1, this.mOnCopyNodeViewClickCallback)).addToFrameLayout(this.copyNodeViewContainer, var2);
    }

    private void adjustActionBar(boolean notingSelected, boolean var2) {
        this.menu.setGroupVisible(R.id.copy_actions, var2);
        if(this.isFullScreen) {
            if(var2) {
                this.exitFab.hide();
                this.copyFab.show();
                this.exitFullScreenFab.show();
            } else {
                this.copyFab.hide();
                this.exitFullScreenFab.hide();
                this.exitFab.show();
            }
        }

        ActionBar var3 = this.getSupportActionBar();
        if(var3 != null) {
            if(notingSelected) {
                var3.setTitle(R.string.copy_title);
                var3.setSubtitle(R.string.copy_subtitle);
                var3.setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
                return;
            }

            var3.setTitle((CharSequence)null);
            var3.setSubtitle((CharSequence)null);
            var3.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
        }

    }

    private void setSelectTextToClipboard(TextView var1) {
//        ClipboardUtils.setText(this,this.getSelectedTextViewText(var1));

        Intent intent=new Intent(this, BigBangActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BigBangActivity.TO_SPLIT_STR, getSelectedTextViewText(var1));
        startActivity(intent);
    }

    private void b(boolean var1) {
//        ActionBar var2 = this.f();
//        if(var2 != null) {
//            var2.a(var1);
//        }

    }

    private String getSelectedTextViewText(TextView var1) {
        if(var1 == null) {
            return getSelectedText();
        } else {
            CharSequence var2 = var1.getText();
            if(var1.getSelectionStart() == var1.getSelectionEnd()) {
                return var2.toString();
            } else {
                CharSequence var3 = var2.subSequence(var1.getSelectionStart(), var1.getSelectionEnd());
                return var3 != null?var3.toString():var2.toString();
            }
        }
    }

    private void fullScreenMode(boolean var1) {
        ActionBar var2 = getSupportActionBar();
        this.isFullScreen = var1;
        if(var1) {
            if(var2 != null) {
                var2.hide();
            }

            this.adjustActionBarWrap();
        } else {
            if(var2 != null) {
                var2.show();
            }

            this.copyFab.hide();
            this.exitFab.hide();
            this.exitFullScreenFab.hide();
        }
    }

    private void showSelectedText() {
        this.v = false;
        this.adjustActionBar(false, false);
        this.b(false);
        this.bottomSheetDialog = new BottomSheetDialog(this){
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                StatusBarCompat.setTranslucentStatus(getWindow(),true);
            }
        };
        View var1 = this.getLayoutInflater().inflate(R.layout.dialog_copy_text_editor, (ViewGroup)null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.topMargin = (int) (this.actionBarHeight - ViewUtil.dp2px(44));
        var1.setLayoutParams(layoutParams);
        TextView var4 = (TextView)var1.findViewById(R.id.text);
        var4.setText(new SpannableString(getSelectedText()), TextView.BufferType.SPANNABLE);
        var4.setCustomSelectionActionModeCallback(new MySelectionActionModeCallback(var4));
        ((FloatingActionButton)var1.findViewById(R.id.fab_copy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectTextToClipboard(null);
                bottomSheetDialog.dismiss();
                finish();
            }
        });
        this.bottomSheetDialog.setContentView(var1);
        ((View)var1.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        BottomSheetBehavior var3 = BottomSheetBehavior.from((View)var1.getParent());
        this.bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                var3.setState(BottomSheetBehavior.STATE_EXPANDED);
                var1.requestLayout();
            }
        });
        this.bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                adjustActionBarWrap();
            }
        });
        this.bottomSheetDialog.show();
    }

    private void adjustActionBarWrap() {
        boolean var2 = true;
        boolean var1;
        if(this.selectedNodes.size() > 0) {
            var1 = true;
        } else {
            var1 = false;
        }

        if(var1) {
            var2 = false;
        }

        this.adjustActionBar(var2, var1);
    }

    private String getSelectedText() {
        StringBuilder var2 = new StringBuilder();

        for(int var1 = 0; var1 < this.selectedNodes.size(); ++var1) {
            var2.append(((CopyNodeView)this.selectedNodes.get(var1)).getText());
            if(var1 + 1 < this.selectedNodes.size()) {
                var2.append("\n");
            }
        }

        return var2.toString();
    }

    private int getStatusBarHeight() {
        int var1 = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return var1 > 0?this.getResources().getDimensionPixelSize(var1):(int)Math.ceil((double)(25.0F * this.getResources().getDisplayMetrics().density));
    }

    protected void onCreate(Bundle var1) {
        int var3 = 0;
        super.onCreate(var1);
        this.setContentView(R.layout.activity_copy_overlay);
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        if(toolbar != null) {

            try {
                setSupportActionBar(toolbar);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            getSupportActionBar().setTitle(R.string.copy_title);
            getSupportActionBar().setSubtitle(R.string.copy_subtitle);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        }

        this.selectedNodes = new ArrayList();
        this.mOnCopyNodeViewClickCallback = new OnCopyNodeViewClickCallback() {
            @Override
            public void onCopyNodeViewLongClick(CopyNodeView var1, boolean var2) {
                if (var2){
                    selectedNodes.add(var1);
                    adjustActionBarWrap();
                    showSelectedText();
                }else {
                    selectedNodes.remove(var1);
                    adjustActionBarWrap();
                }
            }

            @Override
            public void onCopyNodeViewClick(CopyNodeView var1, boolean var2) {
                if (var2){
                    selectedNodes.add(var1);
                    adjustActionBarWrap();
                }else {
                    selectedNodes.remove(var1);
                    adjustActionBarWrap();
                }
            }
        };
        this.copyFab = (FloatingActionButton)this.findViewById(R.id.fab_copy_main);
        this.copyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_COPY_FAB);
                setSelectTextToClipboard(null);
                finish();
            }
        });
        this.exitFab = (FloatingActionButton)this.findViewById(R.id.exit_button);
        this.exitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_FAB);
                finish();
            }
        });
        this.exitFullScreenFab = (FloatingActionButton)this.findViewById(R.id.exit_full_screen_button);
        exitFullScreenFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_FULLSCREEN_FAB);
                fullScreenMode(false);
            }
        });

//        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int statusBarHeight = getStatusBarHeight();
        int screenHeight = displayMetrics.heightPixels;
        this.copyNodeViewContainer = (FrameLayout)this.findViewById(R.id.overlay_root);
        TypedValue var8 = new TypedValue();
        if(this.getTheme().resolveAttribute(android.R.attr.actionBarSize, var8, true)) {
            this.actionBarHeight = TypedValue.complexToDimensionPixelSize(var8.data, this.getResources().getDisplayMetrics());
        }

        this.contentHeight = screenHeight - statusBarHeight;
        this.s = (int) ViewUtil.dp2px(132);
        String var9 = this.getIntent().getStringExtra("source_package");
        screenHeight = statusBarHeight;
        if(var9 != null) {
            screenHeight = statusBarHeight;
            if("com.android.chrome".equals(var9)) {
                screenHeight = (int) (this.actionBarHeight - statusBarHeight - ViewUtil.dp2px(7));
            }
        }

        ArrayList var10 = this.getIntent().getParcelableArrayListExtra("copy_nodes");
        if(var10 != null && var10.size() > 0) {
            CopyNode[] var11 = (CopyNode[])var10.toArray(new CopyNode[0]);
            Arrays.sort(var11, new CopyNodeComparator());

            for(statusBarHeight = var11.length; var3 < statusBarHeight; ++var3) {
                this.addCopyNodeView(var11[var3], screenHeight);
            }
        } else {
            ToastUtil.show(R.string.error_in_copy);
            this.finish();
        }

    }

    public boolean onCreateOptionsMenu(Menu var1) {
        this.getMenuInflater().inflate(R.menu.universal_copy, var1);
        this.menu = var1;
        return super.onCreateOptionsMenu(var1);
    }

    public boolean onOptionsItemSelected(MenuItem var1) {
        switch(var1.getItemId()) {
            case android.R.id.home:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT);
                // TODO: 2016/11/19  
                if(this.selectedNodes.size() <= 0) {
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT);
                    this.finish();
                    return true;
                }
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_RETUN);
                Iterator var2 = this.selectedNodes.iterator();

                while(var2.hasNext()) {
                    ((CopyNodeView)var2.next()).setActiveState(false);
                }

                this.selectedNodes.clear();
                this.adjustActionBarWrap();
                return true;
            case R.id.action_full_screen:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_FULLSCREEN_ACTION);
                this.fullScreenMode(true);
                return true;
            case R.id.action_select_mode:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EDIT);
                showSelectedText();
                return true;
            case R.id.action_copy:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_COPY_ACTION);
                setSelectTextToClipboard((TextView)null);
                finish();
                return true;
            default:
                return false;
        }
    }

    protected void onPause() {
        super.onPause();
        this.overridePendingTransition(17432577, 17432577);
    }

    public interface OnCopyNodeViewClickCallback {
        void onCopyNodeViewLongClick(CopyNodeView var1, boolean var2);

        void onCopyNodeViewClick(CopyNodeView var1, boolean var2);
    }

    public class CopyNodeComparator implements Comparator<CopyNode> {

        public int compare(CopyNode var1, CopyNode var2) {
            long var3 = var1.caculateSize();
            long var5 = var2.caculateSize();
            return var3 < var5?-1:(var3 == var5?0:1);
        }
    }

    private class MySelectionActionModeCallback implements ActionMode.Callback {
        private TextView b;

        private MySelectionActionModeCallback(TextView var2) {
            this.b = var2;
        }

        public boolean onActionItemClicked(ActionMode var1, MenuItem var2) {
            switch(var2.getItemId()) {
                case R.id.fab_copy:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_COPY_FAB);
                    setSelectTextToClipboard(this.b);
                    finish();
                    return true;
                default:
                    return false;
            }
        }

        public boolean onCreateActionMode(ActionMode var1, Menu var2) {
            return true;
        }

        public void onDestroyActionMode(ActionMode var1) {
            if(CopyActivity.this.bottomSheetDialog != null && !CopyActivity.this.v) {
                CopyActivity.this.v = true;

                try {
                    CopyActivity.this.bottomSheetDialog.dismiss();
                } catch (IllegalArgumentException var2) {
//                    Crashlytics.a(var2);
                }
            }

            CopyActivity.this.v = false;
        }

        public boolean onPrepareActionMode(ActionMode var1, Menu var2) {
            for(int var3 = 0; var3 < var2.size(); ++var3) {
                var2.getItem(var3).setVisible(false);
            }

            return false;
        }

    }
}
