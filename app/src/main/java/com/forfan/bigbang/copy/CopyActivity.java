
package com.forfan.bigbang.copy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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
import com.forfan.bigbang.util.ArcTipViewController;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.forfan.bigbang.util.ConstantUtil.IS_FULL_SCREEN_COPY;

public class CopyActivity extends BaseActivity {
    private FrameLayout copyNodeViewContainer;
    private FloatingActionButton copyFab;
    private FloatingActionButton exitFab;
    private FloatingActionButton exitFullScreenFab;
    private Menu menu;
    private List<CopyNodeView> selectedNodes;
    private OnCopyNodeViewClickCallback mOnCopyNodeViewClickCallback;
    private int actionBarHeight = 0;
    private BottomSheetDialog bottomSheetDialog;
    private boolean actionModeDestroying = false;
    private boolean isFullScreen = false;

    private void addCopyNodeView(CopyNode copyNode, int height) {
        (new CopyNodeView(this, copyNode, mOnCopyNodeViewClickCallback)).addToFrameLayout(copyNodeViewContainer, height);
    }

    private void adjustActionBar(boolean showTitle, boolean hadSelection) {
        menu.setGroupVisible(R.id.copy_actions, hadSelection);
        if(isFullScreen) {
            if(hadSelection) {
                exitFab.show();
                copyFab.show();
                exitFullScreenFab.show();
            } else {
                exitFab.show();
                copyFab.hide();
                exitFullScreenFab.show();
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            if(showTitle) {
                actionBar.setTitle(R.string.copy_title);
                actionBar.setSubtitle(R.string.copy_subtitle);
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
                return;
            }

            actionBar.setTitle((CharSequence)null);
            actionBar.setSubtitle((CharSequence)null);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
        }

    }

    private void setSelectTextToClipboard(TextView textView) {
//        ClipboardUtils.setText(this,getSelectedTextViewText(var1));

        Intent intent=new Intent(this, BigBangActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BigBangActivity.TO_SPLIT_STR, getSelectedTextViewText(textView));
        startActivity(intent);
    }


    private String getSelectedTextViewText(TextView textView) {
        if(textView == null) {
            return getSelectedText();
        } else {
            CharSequence text = textView.getText();
            if(textView.getSelectionStart() == textView.getSelectionEnd()) {
                return text.toString();
            } else {
                CharSequence selected = text.subSequence(textView.getSelectionStart(), textView.getSelectionEnd());
                return selected != null?selected.toString():text.toString();
            }
        }
    }

    private void fullScreenMode(boolean fullScreen) {
        ActionBar actionBar = getSupportActionBar();
        isFullScreen = fullScreen;
        if(fullScreen) {
            if(actionBar != null) {
                actionBar.hide();
            }

            adjustActionBarWrap();
        } else {
            if(actionBar != null) {
                actionBar.show();
            }

            copyFab.hide();
            exitFab.hide();
            exitFullScreenFab.hide();
        }
        SPHelper.save(IS_FULL_SCREEN_COPY,fullScreen);
    }

    private void showSelectedText() {
        actionModeDestroying = false;
        adjustActionBar(false, false);
        bottomSheetDialog = new BottomSheetDialog(this){
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                StatusBarCompat.setTranslucentStatus(getWindow(),true);
            }
        };
        View view = getLayoutInflater().inflate(R.layout.dialog_copy_text_editor, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        TextView textView = (TextView)view.findViewById(R.id.text);
        textView.setText(getSelectedText());
//        textView.setText(new SpannableString(getSelectedText()), TextView.BufferType.NORMAL);
        textView.setCustomSelectionActionModeCallback(new MySelectionActionModeCallback(textView));
        view.findViewById(R.id.fab_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectTextToClipboard(null);
                bottomSheetDialog.dismiss();
                finish();
            }
        });
        bottomSheetDialog.setContentView(view);
        ((View)view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                adjustActionBarWrap();
            }
        });
        bottomSheetDialog.show();

    }

    private void adjustActionBarWrap() {
        boolean showTitle = true;
        boolean hadSelection;
        if(selectedNodes.size() > 0) {
            hadSelection = true;
        } else {
            hadSelection = false;
        }

        if(hadSelection) {
            showTitle = false;
        }

        adjustActionBar(showTitle, hadSelection);
    }

    private String getSelectedText() {
        StringBuilder text = new StringBuilder();

        for(int i = 0; i < selectedNodes.size(); ++i) {
            text.append(((CopyNodeView)selectedNodes.get(i)).getText());
            if(i + 1 < selectedNodes.size()) {
                text.append("\n");
            }
        }

        return text.toString();
    }

    private int getStatusBarHeight() {
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resId > 0?getResources().getDimensionPixelSize(resId):(int)Math.ceil((double)(25.0F * getResources().getDisplayMetrics().density));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArcTipViewController.getInstance().showHideFloatImageView();

        setContentView(R.layout.activity_copy_overlay);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
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

        selectedNodes = new ArrayList();
        mOnCopyNodeViewClickCallback = new OnCopyNodeViewClickCallback() {
            @Override
            public void onCopyNodeViewLongClick(CopyNodeView nodeView, boolean status) {
                if (status){
                    selectedNodes.add(nodeView);
                    adjustActionBarWrap();
                    showSelectedText();
                }else {
                    selectedNodes.remove(nodeView);
                    adjustActionBarWrap();
                }
            }

            @Override
            public void onCopyNodeViewClick(CopyNodeView nodeView, boolean status) {
                if (status){
                    selectedNodes.add(nodeView);
                    adjustActionBarWrap();
                }else {
                    selectedNodes.remove(nodeView);
                    adjustActionBarWrap();
                }
            }
        };
        copyFab = (FloatingActionButton)findViewById(R.id.fab_copy_main);
        copyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_COPY_FAB);
                setSelectTextToClipboard(null);
                finish();
            }
        });
        exitFab = (FloatingActionButton)findViewById(R.id.exit_button);
        exitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_FAB);
                finish();
            }
        });
        exitFullScreenFab = (FloatingActionButton)findViewById(R.id.exit_full_screen_button);
        exitFullScreenFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_FULLSCREEN_FAB);
                fullScreenMode(false);
            }
        });

//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int statusBarHeight = getStatusBarHeight();
        int height = displayMetrics.heightPixels;
        copyNodeViewContainer = (FrameLayout)findViewById(R.id.overlay_root);
        TypedValue typedValue = new TypedValue();
        if(getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }

        Bundle extras = getIntent().getExtras();
        if (extras==null){
            finish();
            return;
        }
        extras.setClassLoader(CopyNode.class.getClassLoader());

        String packageName = extras.getString("source_package");
        height = statusBarHeight;
        if(packageName != null) {
            height = statusBarHeight;
            if("com.android.chrome".equals(packageName)) {
                height = (int) (actionBarHeight - statusBarHeight - ViewUtil.dp2px(7));
            }
        }

        ArrayList nodesList = extras.getParcelableArrayList("copy_nodes");
        if(nodesList != null && nodesList.size() > 0) {
            CopyNode[] nodes = (CopyNode[])nodesList.toArray(new CopyNode[0]);
            Arrays.sort(nodes, new CopyNodeComparator());
            for(int i  = 0; i < nodes.length; ++i) {
                addCopyNodeView(nodes[i], height);
            }
        } else {
            ToastUtil.show(R.string.error_in_copy);
            finish();
        }
        exitFab.postDelayed(new Runnable() {
            @Override
            public void run() {
                fullScreenMode(SPHelper.getBoolean(IS_FULL_SCREEN_COPY,false));
            }
        }, 10);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.universal_copy, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case android.R.id.home:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT);
                // TODO: 2016/11/19  
                if(selectedNodes.size() <= 0) {
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT);
                    finish();
                    return true;
                }
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_RETUN);
                Iterator iterator = selectedNodes.iterator();

                while(iterator.hasNext()) {
                    ((CopyNodeView)iterator.next()).setActiveState(false);
                }

                selectedNodes.clear();
                adjustActionBarWrap();
                return true;
            case R.id.action_full_screen:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EXIT_FULLSCREEN_ACTION);
                fullScreenMode(true);
                return true;
            case R.id.action_select_mode:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_EDIT);
                showSelectedText();
                return true;
            case R.id.action_select_all:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_SELECT_ALL);
                selectAll();
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

    private void selectAll(){
        int length=copyNodeViewContainer.getChildCount();
        int nodeLength=0;
        for (int i = 0; i < length; i++) {
            View view = copyNodeViewContainer.getChildAt(i);
            if (view instanceof CopyNodeView) {
                nodeLength++;
            }
        }
        if (selectedNodes.size()==nodeLength && nodeLength!=0){
            selectedNodes.clear();
            for (int i = 0; i < length; i++) {
                View view = copyNodeViewContainer.getChildAt(i);
                if (view instanceof CopyNodeView) {
                    ((CopyNodeView) view).setActiveState(false);
                }
            }
        }else {
            for (int i = 0; i < length; i++) {
                View view = copyNodeViewContainer.getChildAt(i);
                if (view instanceof CopyNodeView) {
                    ((CopyNodeView) view).setActiveState(true);
                    if (!selectedNodes.contains(view)) {
                        selectedNodes.add((CopyNodeView) view);
                    }
                }
            }
        }
        adjustActionBarWrap();
    }

    public interface OnCopyNodeViewClickCallback {
        void onCopyNodeViewLongClick(CopyNodeView nodeView, boolean status);

        void onCopyNodeViewClick(CopyNodeView nodeView, boolean status);
    }

    public class CopyNodeComparator implements Comparator<CopyNode> {

        public int compare(CopyNode o1, CopyNode o2) {
            long o1Size = o1.caculateSize();
            long o2Size = o2.caculateSize();
            return o1Size < o2Size?-1:(o1Size == o2Size?0:1);
        }
    }

    private class MySelectionActionModeCallback implements ActionMode.Callback {
        private TextView textView;

        private MySelectionActionModeCallback(TextView view) {
            textView = view;
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.fab_copy:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_UNIVERSAL_COPY_COPY_FAB);
                    setSelectTextToClipboard(textView);
                    finish();
                    return true;
                default:
                    return false;
            }
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            if(CopyActivity.this.bottomSheetDialog != null && !CopyActivity.this.actionModeDestroying) {
                CopyActivity.this.actionModeDestroying = true;

                try {
                    CopyActivity.this.bottomSheetDialog.dismiss();
                } catch (IllegalArgumentException e) {
                }
            }

            CopyActivity.this.actionModeDestroying = false;
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            for(int i = 0; i < menu.size(); ++i) {
                menu.getItem(i).setVisible(false);
            }

            return false;
        }

    }
}
