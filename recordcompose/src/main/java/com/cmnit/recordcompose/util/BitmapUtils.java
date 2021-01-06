package com.cmnit.recordcompose.util;

import android.app.Activity;
import android.graphics.BitmapFactory;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2021/1/4 17:27
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class BitmapUtils {
    public static int getBitmapWidth(@NonNull Activity activity,@DrawableRes int drawableRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(),drawableRes,options);

        //获取图片的宽高
        return options.outWidth;
    }

    public static int getBitmapHeight(@NonNull Activity activity,@DrawableRes int drawableRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(),drawableRes,options);

        //获取图片的宽高
        return options.outHeight;
    }

}
