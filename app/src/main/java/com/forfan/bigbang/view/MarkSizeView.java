package com.forfan.bigbang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.ViewUtil;

/**
 * Created by Administrator on 2016/11/28.
 */
public class MarkSizeView extends View {

    private static final int DEFAULT_MARKED_COLOR = Color.parseColor("#00000000");
    private static final int DEFAULT_UNMARKED_COLOR = Color.parseColor("#80000000");
    private static final int DEFAULT_STROKE_COLOR = Color.parseColor("#009688");
    private static final int DEFAULT_STROKE_WIDTH = 2;//dp
    private static final int DEFAULT_VERTEX_COLOR = Color.parseColor("#009688");
    private static final int DEFAULT_CONFIRM_BUTTON_RES = R.mipmap.ic_done_white_36dp;
    private static final int DEFAULT_CANCEL_BUTTON_RES = R.mipmap.ic_close_capture;

    private static final int DEFAULT_VERTEX_WIDTH=20;//dp


    private int markedColor = DEFAULT_MARKED_COLOR;
    private int unmarkedColor = DEFAULT_UNMARKED_COLOR;
    private int strokeColor = DEFAULT_STROKE_COLOR;
    private int strokeWidth = (int) ViewUtil.dp2px(DEFAULT_STROKE_WIDTH);//dp
    private int vertexColor = DEFAULT_VERTEX_COLOR;
    private int confirmButtonRes = DEFAULT_CONFIRM_BUTTON_RES;
    private int cancelButtonRes = DEFAULT_CANCEL_BUTTON_RES;
    private int vertexWidth = (int) ViewUtil.dp2px(DEFAULT_VERTEX_WIDTH);
    private int mActionGap;


    private Paint unMarkPaint,markPaint,vertexPaint, mBitPaint;

    private int downX,downY;
    private int startX,startY;
    private int endX,endY;

    private Rect markedArea;
    private Rect confirmArea,cancelArea;
    private RectF ltVer,rtVer,lbVer,rbVer;
    private boolean isValid=false;
    private boolean isUp=false;
    private boolean isMoveMode=false;
    private boolean isAdjustMode=false;
    private boolean isButtonClicked=false;
    private int adjustNum = 0;

    private Bitmap confirmBitmap,cancelBitmap;

    private onClickListener mOnClickListener;

    public MarkSizeView(Context context) {
        super(context);
        init(context,null);
    }

    public MarkSizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        if (attrs!=null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarkSizeView);
            markedColor=typedArray.getColor(R.styleable.MarkSizeView_markedColor,DEFAULT_MARKED_COLOR);
            unmarkedColor=typedArray.getColor(R.styleable.MarkSizeView_unMarkedColor,DEFAULT_UNMARKED_COLOR);
            strokeColor=typedArray.getColor(R.styleable.MarkSizeView_strokeColor,DEFAULT_STROKE_COLOR);
            strokeWidth=typedArray.getDimensionPixelSize(R.styleable.MarkSizeView_strokeWidth, (int) ViewUtil.dp2px(DEFAULT_STROKE_WIDTH));
            vertexColor=typedArray.getColor(R.styleable.MarkSizeView_vertexColor,DEFAULT_VERTEX_COLOR);
            vertexWidth=typedArray.getDimensionPixelSize(R.styleable.MarkSizeView_vertexWidth, (int) ViewUtil.dp2px(DEFAULT_VERTEX_WIDTH));
            confirmButtonRes =typedArray.getResourceId(R.styleable.MarkSizeView_confirmButtonRes,DEFAULT_CONFIRM_BUTTON_RES);
            cancelButtonRes=typedArray.getResourceId(R.styleable.MarkSizeView_cancleButtonRes,DEFAULT_CANCEL_BUTTON_RES);
        }

        unMarkPaint=new Paint();
        unMarkPaint.setColor(unmarkedColor);
        unMarkPaint.setAntiAlias(true);

        markPaint=new Paint();
        markPaint.setColor(markedColor);
        markPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        markPaint.setColor(markedColor);
        markPaint.setStrokeWidth(strokeWidth);
        markPaint.setAntiAlias(true);

        vertexPaint=new Paint();
        vertexPaint.setColor(vertexColor);
        vertexPaint.setAntiAlias(true);

        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);

        markedArea=new Rect();
        confirmArea=new Rect();
        cancelArea=new Rect();

        ltVer=new RectF();
        rtVer=new RectF();
        lbVer=new RectF();
        rbVer=new RectF();

        confirmBitmap= BitmapFactory.decodeResource(getResources(), confirmButtonRes);
        cancelBitmap = BitmapFactory.decodeResource(getResources(),cancelButtonRes);

        mActionGap = (int) ViewUtil.dp2px(15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height=getHeight();
        //draw unmarked
        canvas.drawRect(0,0,width,height,unMarkPaint);

        //draw maeked

        if (isValid) {
            canvas.drawRect(markedArea, markPaint);
        }

        //draw vertex
        if (isValid&&isUp) {
            canvas.drawOval(ltVer, vertexPaint);
            canvas.drawOval(rtVer, vertexPaint);
            canvas.drawOval(lbVer, vertexPaint);
            canvas.drawOval(rbVer, vertexPaint);
        }

        //draw button
        if (isValid&&isUp) {
            canvas.drawBitmap(confirmBitmap,null,confirmArea,mBitPaint);
            canvas.drawBitmap(cancelBitmap,null,cancelArea,mBitPaint);
        }

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x= (int) event.getX();
        int y= (int) event.getY();
        switch ( event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isUp=false;
                isAdjustMode=false;
                isMoveMode=false;
                isButtonClicked=false;
                isValid=false;
                adjustNum=0;
                downX=x;
                downY=y;
                if (mOnClickListener!=null){
                    mOnClickListener.onTouch();
                }
                if (confirmArea.contains(x,y)){
                    isButtonClicked=true;
                    isValid=true;
                    if (mOnClickListener!=null){
                        mOnClickListener.onConfirm(markedArea);
                    }
                }else if (cancelArea.contains(x,y)){
                    isButtonClicked=true;
                    isValid=true;
                    if (mOnClickListener!=null){
                        mOnClickListener.onCancel();
                        isValid=false;
                        startX=startY=endX=endY=0;
                        adjustMark(0,0);
                    }
                }else if (ltVer.contains(x,y)){
                    isAdjustMode=true;
                    adjustNum=1;
                }else if (rtVer.contains(x,y)){
                    isAdjustMode=true;
                    adjustNum=2;
                }else if (lbVer.contains(x,y)){
                    isAdjustMode=true;
                    adjustNum=3;
                }else if (rbVer.contains(x,y)){
                    isAdjustMode=true;
                    adjustNum=4;
                }else if (markedArea.contains(x,y)){
                    isMoveMode=true;
                }else {
                    isMoveMode=false;
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    endX = startX;
                    endY = startY;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isButtonClicked){
                    break;
                }
                adjustMark(x, y);
                break;
            case MotionEvent.ACTION_UP:
                isUp=true;
                if (isButtonClicked){
                    break;
                }
                adjustMark(x, y);
                startX=markedArea.left;
                startY=markedArea.top;
                endX=markedArea.right;
                endY=markedArea.bottom;

                if (markedArea.width()>confirmBitmap.getWidth()*3+mActionGap*3&&markedArea.height()>confirmBitmap.getHeight()*5){
                    //显示在选区的内底部
                    confirmArea.set(endX-confirmBitmap.getWidth()-mActionGap,endY-confirmBitmap.getHeight()-mActionGap,endX-mActionGap,endY-mActionGap);
                    cancelArea.set(endX-2*confirmBitmap.getWidth()-mActionGap*2,endY-confirmBitmap.getHeight()-mActionGap,endX-confirmBitmap.getWidth()-mActionGap*2,endY-mActionGap);
                }else if (endY>getHeight()-confirmBitmap.getHeight()*3) {
                    //显示在选区的上面
                    confirmArea.set(endX-confirmBitmap.getWidth()-mActionGap,startY-confirmBitmap.getHeight()-mActionGap,endX-mActionGap,startY-mActionGap);
                    cancelArea.set(endX-2*confirmBitmap.getWidth()-mActionGap*2,startY-confirmBitmap.getHeight()-mActionGap,endX-confirmBitmap.getWidth()-mActionGap*2,startY-mActionGap);
                }else {
                    //显示在选区的下面
                    confirmArea.set(endX-confirmBitmap.getWidth()-mActionGap,endY+mActionGap,endX-mActionGap,endY+confirmBitmap.getHeight()+mActionGap);
                    cancelArea.set(endX-2*confirmBitmap.getWidth()-mActionGap*2,endY+mActionGap,endX-confirmBitmap.getWidth()-mActionGap*2,endY+confirmBitmap.getHeight()+mActionGap);
                }


                break;
            case MotionEvent.ACTION_CANCEL:
                isUp=true;
                break;
        }
        postInvalidate();
        return true;
    }

    private void adjustMark(int x, int y) {
        if (isAdjustMode){
            int moveMentX = x-downX;
            int moveMentY = y-downY;

            switch (adjustNum){
                case 1:
                    startX=startX+moveMentX;
                    startY=startY+moveMentY;
                    break;
                case 2:
                    endX=endX+moveMentX;
                    startY=startY+moveMentY;
                    break;
                case 3:
                    startX=startX+moveMentX;
                    endY=endY+moveMentY;
                    break;
                case 4:
                    endX=endX+moveMentX;
                    endY=endY+moveMentY;
                    break;
            }
            downX=x;
            downY=y;
        }else if (isMoveMode){
            int moveMentX = x-downX;
            int moveMentY = y-downY;

            startX=startX+moveMentX;
            startY=startY+moveMentY;

            endX=endX+moveMentX;
            endY=endY+moveMentY;

            downX=x;
            downY=y;
        }else {
            endX = x;
            endY = y;
        }
        markedArea.set(Math.min(startX, endX), Math.min(startY, endY), Math.max(startX, endX), Math.max(startY, endY));
        ltVer.set(markedArea.left - vertexWidth / 2, markedArea.top - vertexWidth / 2, markedArea.left + vertexWidth / 2, markedArea.top + vertexWidth / 2);
        rtVer.set(markedArea.right - vertexWidth / 2, markedArea.top - vertexWidth / 2, markedArea.right + vertexWidth / 2, markedArea.top + vertexWidth / 2);
        lbVer.set(markedArea.left - vertexWidth / 2, markedArea.bottom - vertexWidth / 2, markedArea.left + vertexWidth / 2, markedArea.bottom + vertexWidth / 2);
        rbVer.set(markedArea.right - vertexWidth / 2, markedArea.bottom - vertexWidth / 2, markedArea.right + vertexWidth / 2, markedArea.bottom + vertexWidth / 2);
        if (markedArea.height()*markedArea.width()>100){
            isValid=true;
        }
    }

    public interface onClickListener{
        void onConfirm(Rect markedArea);
        void onCancel();
        void onTouch();
    }

    public void setmOnClickListener(onClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void setUnmarkedColor(int unmarkedColor) {
        this.unmarkedColor = unmarkedColor;
        unMarkPaint.setColor(unmarkedColor);
        invalidate();
    }

    public void reset(){
        isUp=false;
        isValid=false;
        startX=startY=endX=endY=0;
        adjustMark(0,0);
    }
}
