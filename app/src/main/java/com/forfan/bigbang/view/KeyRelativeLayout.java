package com.forfan.bigbang.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.ViewUtil;

public class KeyRelativeLayout extends RelativeLayout
{
    private View.OnKeyListener a;
    private int animationStep;
    private Drawable originBg;
    private ClipDrawable animationBg;
    private ImageView bgImage;

    public KeyRelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
    {
        if (((paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) || (paramKeyEvent.getKeyCode() == KeyEvent.KEYCODE_HOME)) && (this.a != null)) {
            this.a.onKey(this, paramKeyEvent.getKeyCode(), paramKeyEvent);
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

    public void setOnKeyListener(View.OnKeyListener paramOnKeyListener)
    {
        super.setOnKeyListener(paramOnKeyListener);
        this.a = paramOnKeyListener;
    }

    public int getAnimationStep() {
        return animationStep;
    }

    public void setAnimationStep(int animationStep) {
        this.animationStep = animationStep;
        animationBg.setLevel(animationStep);
    }

    public void showEnterAnimation(Animator.AnimatorListener listener){
        originBg = getContext().getResources().getDrawable(R.drawable.borders);
        animationBg=new ClipDrawable(originBg,Gravity.BOTTOM|Gravity.CLIP_VERTICAL,ClipDrawable.VERTICAL);

        bgImage = new ImageView(this.getContext());
        animationStep=0;
        animationBg.setLevel(animationStep);
        bgImage.setImageDrawable(animationBg);
        animationBg= (ClipDrawable) bgImage.getDrawable();
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtil.dp2px(90));
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        addView(bgImage,0,layoutParams);
        ObjectAnimator animator=ObjectAnimator.ofInt(this,"animationStep",0,10000);
//        animator.setDuration(300).addListener(listener);
        animator.start();
    }

    public void showExitAnimation(){

    }



}
