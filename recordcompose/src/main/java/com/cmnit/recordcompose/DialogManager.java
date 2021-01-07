package com.cmnit.recordcompose;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2020/12/30 11:21
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class DialogManager {
    /**
     * 以下为dialog的初始化控件，包括其中的布局文件
     */

    private Dialog mDialog;
    private WaveView mVoice;

    private TextView mLable;

    private Context mContext;

    private DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.dialogTransparent);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 用layoutinflater来引用布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder, null);
        mDialog.setContentView(view);

        mVoice = mDialog.findViewById(R.id.dialog_voice);
        mLable = mDialog.findViewById(R.id.recorder_dialogtext);

        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        int width = ScreenUtil.getScreenWidth(mContext) / 2;
//        lp.width = width; // 宽度
//        lp.height = width; // 高度
//        dialogWindow.setAttributes(lp);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    /**
     * 设置正在录音时的dialog界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mLable.setText(R.string.move_up_to_cancel);
            mLable.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 取消界面
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);
            mLable.setText(R.string.release_to_cancel);
        }
    }

    // 隐藏dialog
    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }

    }


    public void updateVolumnTime(float vol, float time) {
        if (mDialog != null && mDialog.isShowing()) {
            mLable.setText(decimalFormat.format(time) + "S");
            mVoice.addData(vol);
        }
    }
}
