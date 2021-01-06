package com.cmnit.recordcompose;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cmnit.recordcompose.util.FileUtils;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2021/1/5 15:18
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class RecorderPlayCompose extends ConstraintLayout {
    RecyclerView recorderList;
    RecorderButton recorderButton;
    RecorderListAdapter recorderListAdapter;
    int maxRecorderCount = 0;
    int maxRecorderTimes = 0;


    public RecorderPlayCompose(Context context) {
        this(context,null);
    }

    public RecorderPlayCompose(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecorderPlayCompose(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RecorderPlayCompose,
                0, 0);

        try {
            maxRecorderCount = a.getInteger(R.styleable.RecorderPlayCompose_maxRecordsCount, 1);
            maxRecorderTimes = a.getInteger(R.styleable.RecorderPlayCompose_maxRecordTimes,30000);
        } finally {
            a.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.record_play_compose, this, true);
        recorderList = findViewById(R.id.record_recycler_view);
        recorderButton = findViewById(R.id.record_button);
        recorderButton.setMaxRecorderTime(maxRecorderTimes);
        recorderButton.setAudioFinishRecorderListener(new RecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Log.d("sadsadsad","用时："+seconds+"秒,路径：" + filePath);
                recorderListAdapter.addPlayRecordHelper(filePath);
                recorderList.setAdapter(recorderListAdapter);
            }
        });
        recorderListAdapter = new RecorderListAdapter(context);
        recorderListAdapter.setMaxCount(maxRecorderCount);
        recorderListAdapter.setMaxRecorderTimes(maxRecorderTimes);
    }

    void cancel(){
        if(recorderListAdapter!=null && recorderListAdapter.getData()!=null){
            for(PlayRecorderHelper playRecorderHelper:recorderListAdapter.getData()){
                if(playRecorderHelper!=null){
                    playRecorderHelper.stopPlay();
                }
            }
        }
    }

    void deleteFiles(){
        if(recorderListAdapter!=null && recorderListAdapter.getData()!=null){
            for(PlayRecorderHelper playRecorderHelper:recorderListAdapter.getData()){
                if(playRecorderHelper!=null){
                    FileUtils.deleteFile(playRecorderHelper.PATH);
                }
            }
        }
    }
}
