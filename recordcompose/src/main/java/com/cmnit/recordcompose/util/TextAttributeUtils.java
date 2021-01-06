package com.cmnit.recordcompose.util;

import android.graphics.Paint;

import androidx.annotation.NonNull;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2021/1/4 17:38
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class TextAttributeUtils {
    public static float getTextWidth(@NonNull Paint paint, String textStr) {
        return paint.measureText(textStr);
    }
}
