package com.cmnit.recordcompose.util;

import java.io.File;

/**
 * @类名:
 * @类描述:
 * @创建者: JXQ
 * @创建时间: 2021/1/5 9:46
 * @更新者:
 * @更新时间:
 * @更新说明:
 * @版本: 1.0
 */
public class FileUtils {
    public static void deleteFile(String path){
        /*删除文件*/
        if (path != null) {
            File file = new File(path);
            file.delete();
        }
    }
}
