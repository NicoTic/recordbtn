package com.cmnit.recordcompose;

import android.media.MediaRecorder;
import android.os.Handler;


import com.cmnit.recordcompose.util.FileUtils;

import java.io.File;
import java.util.UUID;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2020/12/31 15:13
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class RecorderHelper {
    private static final String TAG = "RecorderHelper";
    String PATH ;
    String DIR ;
    int MAX_RECORDER_TIME = 0;
    private double BASE = 0.1d;
    private int SPACE = 160;// 间隔取样时间
    private static volatile float mTime = 0;
    private MediaRecorder mMediaRecorder;
    private static volatile RecorderHelper sInst = null;
    onRecorderListener mListener;
    Handler mHandler = new Handler();

    public static RecorderHelper getInstance() {
        RecorderHelper inst = sInst;
        if (inst == null) {
            synchronized (RecorderHelper.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new RecorderHelper();
                    sInst = inst;
                }
            }
        }
        return inst;
    }
    public RecorderHelper setPath(String dir){
        this.DIR = dir;
        return this;
    }

    public RecorderHelper setMaxRecorderTime(int time){
        this.MAX_RECORDER_TIME = time;
        return this;
    }

    private RecorderHelper() {
    }

    public void startRecord() {
        try {
            File dir = new File(DIR);
            if (!dir.exists()) {
                dir.mkdir();
            }

            String fileName = generateFileName();
            File file = new File(dir, fileName);
            PATH = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            // 设置录音文件的保存位置
            mMediaRecorder.setOutputFile(PATH);
            // 设置录音的来源（从哪里录音）
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录音的保存格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // 设置录音的编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            if (null != mListener) {
                mListener.recorderStart();
            }
            mTime = 0f;
            updateMicStatus();
            mHandler.removeCallbacks(mTimeOut);
            mHandler.postDelayed(mTimeOut, MAX_RECORDER_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentPath(){
        return PATH;
    }

    private String generateFileName(){
        return UUID.randomUUID().toString() + ".amr";
    }

    void stop(){
        this.stopAndRelease();
        if (null != mListener) {
            mListener.recorderStop(mTime,PATH);
        }
    }

    void stopAndRelease() {
        if (null != mMediaRecorder) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void setRecorderListener(onRecorderListener listener) {
        this.mListener = listener;
    }

    public void cancel() {
        mHandler.removeCallbacksAndMessages(null);
        this.stopAndRelease();
        /*删除文件*/
        if (PATH != null) {
           FileUtils.deleteFile(PATH);
            /*删除文件后把路径对象设置为null*/
            PATH = null;
        }
        mTime = 0;
        if (null != mListener) {
            mListener.recorderCancel();
        }
    }

    public interface onRecorderListener {
        // 开始录音
        void recorderStart();
        // 正常结束录音
        void recorderStop(float time,String currentFilePath);
        // 取消录音
        void recorderCancel();
        // 音量、时间变化
        void volumeChange(float vol,float time);
    }

    private void updateMicStatus() {
        mTime += 0.16f;
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }
            if (null != mListener) {
                mListener.volumeChange((float) db,mTime);
            }
            // 每间隔160毫秒取一次音量和时间的变化
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private Runnable mTimeOut = new Runnable() {
        @Override
        public void run() {
            cancel();
        }
    };
}
