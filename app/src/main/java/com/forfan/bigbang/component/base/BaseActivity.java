package com.forfan.bigbang.component.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by penglu on 2016/4/27.
 */
public class BaseActivity extends PermissionActivity {

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void switchFragment(Fragment fragment){
        if (currentFragment!=null&&currentFragment==fragment){
            return;
        }
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if (currentFragment!=null){
            ft.hide(currentFragment);
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
        currentFragment=fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            MobclickAgent.onResume(this);
        } catch (Throwable e) {
        }
    }

    @Override
    protected void onPause() {
        try {
            MobclickAgent.onPause(this);
        } catch (Throwable e) {
        }
        super.onPause();
    }

    public void registerFragment(int id, Fragment fragment){
        if (currentFragment==fragment){
            return;

        }
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if (currentFragment!=null){
            ft.hide(currentFragment);
        }
        ft.add(id,fragment,fragment.getClass().getName());
        ft.commit();
        currentFragment=fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    protected void recoverFragment(String currentFragmentTag){
        if (currentFragmentTag==null){
            return;
        }
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        List<Fragment> fragments=fm.getFragments();
        for (Fragment fragment:fragments){
            if (fragment.getTag().equals(currentFragmentTag)){
                ft.show(fragment);
            }else {
                ft.hide(fragment);
            }
        }
        ft.commitAllowingStateLoss();
        Fragment current=fm.findFragmentByTag(currentFragmentTag);
        switchFragment(current);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        removeDialogFragment();
        super.onSaveInstanceState(outState);
    }

    protected void removeDialogFragment(){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        List<Fragment> fragments=fm.getFragments();
        if (fragments==null){
            return;
        }
        for (Fragment fragment:fragments){
            if (fragment instanceof DialogFragment){
                ft.remove(fragment);
            }
        }
        ft.commitAllowingStateLoss();
    }
}
