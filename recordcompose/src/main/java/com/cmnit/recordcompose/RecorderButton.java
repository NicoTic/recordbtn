package com.cmnit.recordcompose;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.cmnit.recordcompose.util.ScreenUtils;


/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2020/12/31 16:00
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class RecorderButton extends AppCompatButton implements RecorderHelper.onRecorderListener{
    private static float DISTANCE_Y_CANCEL = 50f;
    private static float DISTANCE_X_CANCEL = 50f;

    private final DialogManager mDialogManager;
    private final RecorderHelper mRecorderHelper;

    private final boolean isHasRecorderPermission = false;

    /**
     * 先实现两个参数的构造方法，布局会默认引用这个构造方法， 用一个 构造参数的构造方法来引用这个方法 * @param context
     */

    public RecorderButton(Context context) {
        this(context, null);
    }

    public RecorderButton(final Context context, AttributeSet attrs) {
        super(context, attrs);

        String dir = context.getExternalCacheDir().getAbsolutePath() + "/recorder";
        Log.i("info", dir);
        mRecorderHelper = RecorderHelper.getInstance().setPath(dir);
        mRecorderHelper.setRecorderListener(this);

        mDialogManager = new DialogManager(getContext());
        setOnLongClickListener(view -> {
            startRecording();
            return false;
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundResource(R.mipmap.record_pressed);
                break;
            case MotionEvent.ACTION_MOVE:
                //根据x，y的坐标判断是否想要取消
                if (wantToCancel(x, y)) {
                    mRecorderHelper.cancel();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                // 取消录音
                mRecorderHelper.cancel();
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundResource(R.mipmap.record_normal);
                // 停止录音，发送录音
                mRecorderHelper.stop();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startRecording() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{5, 9, 5, 9, 5, 9}, -1);
        if (!isHasRecorderPermission) {
            PackageManager pkm = getContext().getPackageManager();
            if(pkm==null){
                return;
            }
            boolean hasPermission = (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", getContext().getPackageName()));
            if (!hasPermission) {
                Toast.makeText(getContext(), R.string.Recording_without_permission,Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mRecorderHelper.startRecord();
    }


    private boolean wantToCancel(int x, int y) {
        if (x < -DISTANCE_X_CANCEL || x > (getWidth()+DISTANCE_X_CANCEL)) {
            return true;
        }
        int screenHeight = ScreenUtils.getScreenHeight((Activity) getContext());
        DISTANCE_Y_CANCEL = screenHeight / 3f;
        return y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL;
    }

    public void setMaxRecorderTime(int maxRecorderTimes) {
        if(mRecorderHelper!=null){
            mRecorderHelper.setMaxRecorderTime(maxRecorderTimes);
        }
    }

    /*录音结束后的回调*/
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    @Override
    public void recorderStart() {
        mDialogManager.showRecordingDialog();
    }

    @Override
    public void recorderStop(float time, String currentFilePath) {
        if(time>0 && time<1){
            mRecorderHelper.cancel();
            Toast.makeText(getContext(),"录音时间太短！",Toast.LENGTH_SHORT).show();
            return;
        }else if(time==0){
            mRecorderHelper.cancel();
            return;
        }
        mDialogManager.dismissDialog();
        //callbackToActivity
        if (mListener != null) {
            mListener.onFinish(time,currentFilePath);
        }
    }

    @Override
    public void recorderCancel() {
        mDialogManager.dismissDialog();
    }

    @Override
    public void volumeChange(float vol,float time) {
        mDialogManager.updateVolumnTime(vol,time);
    }
}
