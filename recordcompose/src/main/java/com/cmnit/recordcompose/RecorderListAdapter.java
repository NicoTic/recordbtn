package com.cmnit.recordcompose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.cmnit.recordcompose.util.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2021/1/5 14:27
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class RecorderListAdapter extends RecyclerView.Adapter<RecorderListAdapter.RecorderListHolder> {
    private List<PlayRecorderHelper> playRecorderHelperList = new ArrayList<>();
    private LayoutInflater mInflater;
    private int maxCount = 1;
    private int maxTimes = 0;
    private Context mContext;

    public RecorderListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setList(List<PlayRecorderHelper> list) {
        this.playRecorderHelperList = list;
    }

    public List<PlayRecorderHelper> getData() {
        return playRecorderHelperList == null ? Collections.emptyList() : playRecorderHelperList;
    }

    public void addPlayRecordHelper(String filePath){
        if(playRecorderHelperList.size()<maxCount){
            PlayRecorderHelper playRecorderHelper = new PlayRecorderHelper()
                    .setPath(filePath)
                    .setMaxTimes(maxTimes);
            playRecorderHelperList.add(playRecorderHelper);
            notifyItemInserted(playRecorderHelperList.size()-1);
//            notifyItemRangeChanged(0,playRecorderHelperList.size());
        }else{
            Toast.makeText(mContext,"最多只能录"+maxCount+"条",Toast.LENGTH_SHORT).show();
//            FileUtils.deleteFile(filePath);
        }
    }

    @NonNull
    @Override
    public RecorderListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.play_recorder_view,
                parent, false);
        return new RecorderListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecorderListHolder holder, int position) {
        holder.deleteRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = holder.getAdapterPosition();
                if (index != RecyclerView.NO_POSITION && playRecorderHelperList.size() > index) {
                    PlayRecorderHelper currentPlayRecorder = playRecorderHelperList.get(index);
                    FileUtils.deleteFile(currentPlayRecorder.PATH);
                    playRecorderHelperList.remove(index);
                    notifyItemRemoved(index);
                    notifyItemRangeChanged(index, playRecorderHelperList.size());
                }
            }
        });
        holder.playRecorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<playRecorderHelperList.size();i++){
                    if(i!=position){
                        playRecorderHelperList.get(i).stopPlay();
                    }else{
                        if(playRecorderHelperList.get(i).isPlaying()){
                            playRecorderHelperList.get(i).stopPlay();
                        }else{
                            playRecorderHelperList.get(i).preparePlay();
                            playRecorderHelperList.get(i).startPlay();
                        }
                    }
                }

            }
        });
        if(playRecorderHelperList.size()>position){
            PlayRecorderHelper currentPlayRecorderHelper = playRecorderHelperList.get(position);
            holder.playRecorderBtn.setmPlayHelper(currentPlayRecorderHelper);
//            holder.playRecorderBtn.setMaxTimes(maxTimes);
        }
    }

    @Override
    public int getItemCount() {
        return playRecorderHelperList!=null ? playRecorderHelperList.size():0;
    }

    public void setMaxRecorderTimes(int maxRecorderTimes) {
        this.maxTimes = maxRecorderTimes;
    }

    static class RecorderListHolder extends RecyclerView.ViewHolder{
        PlayRecorderButton playRecorderBtn;
        AppCompatImageView deleteRecorder;
        public RecorderListHolder(@NonNull View itemView) {
            super(itemView);
            playRecorderBtn = itemView.findViewById(R.id.play_recorder_btn);
            deleteRecorder = itemView.findViewById(R.id.cancel_button);
        }
    }
}
