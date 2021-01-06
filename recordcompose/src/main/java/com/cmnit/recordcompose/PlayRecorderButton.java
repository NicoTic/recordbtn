package com.cmnit.recordcompose;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cmnit.recordcompose.util.BitmapUtils;
import com.cmnit.recordcompose.util.FileUtils;
import com.cmnit.recordcompose.util.ScreenUtils;
import com.cmnit.recordcompose.util.TextAttributeUtils;


/**
 * @类名:
 * @类描述: 播放录音
 * @创建者: JXQ
 * @创建时间: 2021/1/4 14:55
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class PlayRecorderButton extends AppCompatTextView implements PlayRecorderHelper.onPlayRecorderListener{
    String mFileName;
    private Handler handler;
    private Runnable runnable;
    private int i;
    public PlayRecorderHelper mPlayHelper;
    private int maxRecorderTime;

    private int[] drawLefts = new int[]{R.mipmap.sound_voice_1, R.mipmap.sound_voice_2,R.mipmap.sound_voice_3};

    public PlayRecorderButton(Context context) {
        this(context,null);
    }

    public PlayRecorderButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayRecorderButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayRecorderHelper getmPlayHelper() {
        return mPlayHelper;
    }

    public void setmPlayHelper(PlayRecorderHelper playHelper) {
        this.mPlayHelper = playHelper;
        playHelper.setPlayRecorderListener(this);
        playHelper.preparePlay();
//        setClick();
    }

    private void setClick() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayHelper.isPlaying()){
                    mPlayHelper.stopPlay();
                }else {
                    mPlayHelper.preparePlay();
                    mPlayHelper.startPlay();
                }
            }
        });
    }


    public void setFileName(String fileName){
        mFileName = fileName;
        mPlayHelper.setPath(fileName);
        mPlayHelper.preparePlay();
    }

    public void resetFileName(String fileName){
        if(mPlayHelper==null){
            return;
        }
        if (TextUtils.isEmpty(fileName)|| mPlayHelper.isPrepared){
            mPlayHelper.reset();
        }

        if(!TextUtils.isEmpty(mFileName) && !mFileName.equals(fileName)){
            FileUtils.deleteFile(mFileName);
            mFileName = null;
        }
        setFileName(fileName);
    }

    @Override
    public void preparedPlay() {
        setBackgroundHeight(mPlayHelper.getRecorderDuration());
        setText(getDuration());
        setDrawableLeft(drawLefts[2]);
    }

    private void setBackgroundHeight(int duration) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getLayoutParams();

        float section = duration/1000f;
        float sec = section%60;
        float factor = sec / (mPlayHelper.MAX_RECORDER_TIME/1000f);

        int screenWidth = ScreenUtils.getScreenWidth((Activity) getContext());
        int drawableLeftWidth = BitmapUtils.getBitmapWidth((Activity) getContext(),drawLefts[2]);
        float textWidth = TextAttributeUtils.getTextWidth(this.getPaint(),"30''");

        float minWidth = drawableLeftWidth+textWidth+getCompoundDrawablePadding()+
                getPaddingLeft()+getPaddingRight();
        float contentWidth = (screenWidth-params.leftMargin-params.rightMargin)*factor;
        float backgroundWidth = 0f;
        if(contentWidth<=minWidth){
            backgroundWidth = minWidth;
        }else{
            backgroundWidth = contentWidth;
        }

        params.width = (int) backgroundWidth;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            params.setMarginStart(20);
            params.setMarginEnd(20);
        }else{
            params.setMargins(20,15,20,15);
        }

        this.setLayoutParams(params);
    }

    @Override
    public void startPlay() {
       startAnim();
    }

    @Override
    public void stopPlay() {
        stopAnim();
    }

    public String getDuration(){
        String minutesStr = "";
        String secondsStr = "";
        int duration = -1;
        if(mPlayHelper!=null){
           duration = mPlayHelper.getRecorderDuration();
        }

        if (duration == -1){
            return "";
        }else {
            int sec = duration/1000;

//            int m = sec/60;
//            if(m<10){
//                minutesStr = "0"+m;
//            }else{
//                minutesStr = String.valueOf(m);
//            }

            int s = sec%60;
            secondsStr = String.valueOf(s);
            return secondsStr + "''";
        }
    }

    /**
     *  设置 drawableLeft
     *
     * @param
     * @param id
     */
    private void setDrawableLeft(@DrawableRes int id) {
        Drawable leftDrawable = getResources().getDrawable(id);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        setCompoundDrawables(leftDrawable, null, null, null);
    }

    private void startAnim() {
        i = 0;
        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 500);
                    setDrawableLeft(drawLefts[i % 3]);
                    i++;
                }
            };
        }

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 500);
    }

    private void stopAnim(){
        setDrawableLeft(drawLefts[2]);
        if (handler != null){
            handler.removeCallbacks(runnable);
        }
    }

//    public void setMaxTimes(int maxTimes) {
//        this.maxRecorderTime = maxTimes;
//    }
}
