package com.cmnit.recordcompose;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2021/1/4 14:36
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class PlayRecorderHelper {
    private static final String TAG = "PlayRecorderHelper";
    String PATH ;
    int MAX_RECORDER_TIME = 600000;
    private MediaPlayer mMediaPlayer;
    onPlayRecorderListener mListener;
    boolean isPrepared = false;

    public PlayRecorderHelper setPath(String path){
        this.PATH = path;
        return this;
    }

    public PlayRecorderHelper setMaxTimes(int maxTimes){
        this.MAX_RECORDER_TIME = maxTimes;
        return this;
    }

    public PlayRecorderHelper() {
    }

    public void setPlayRecorderListener(onPlayRecorderListener mListener) {
        this.mListener = mListener;
    }

    int getRecorderDuration(){
        if(mMediaPlayer!=null){
           return mMediaPlayer.getDuration();
        }
        return 0;
    }

    void reset(){
        if(mMediaPlayer!=null){
            mMediaPlayer.reset();
        }
    }

    void preparePlay(){
        if(TextUtils.isEmpty(PATH)){
            return;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(PATH);
            mMediaPlayer.prepare();
            if(mListener!=null){
                mListener.preparedPlay();
            }
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    isPrepared = true;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(mListener!=null){
                        mListener.stopPlay();
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    if(mListener!=null){
                        mListener.stopPlay();
                    }
                    return false;
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    void startPlay(){
        if(mMediaPlayer!=null && isPrepared){
            mMediaPlayer.start();
        }

        if(mListener!=null){
            mListener.startPlay();
        }
    }

    void stopPlay(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if(mListener!=null){
            mListener.stopPlay();
        }
    }

    boolean isPlaying(){
        if(mMediaPlayer!=null){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    interface onPlayRecorderListener{
        void preparedPlay();
        void startPlay();
        void stopPlay();
    }
}
