package cn.pivotstudio.husthole.model;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SaveImgUtil {
    /**
     * 保存图片到公共目录
     * 29 以下，需要提前申请文件读写权限
     * 29及29以上的，不需要权限
     * 保存的文件在 DCIM 目录下
     *
     * @param context 上下文
     * @param bitmap  需要保存的bitmap
     * @param format  图片格式
     * @param quality 压缩的图片质量
     * @param recycle 完成以后，是否回收Bitmap，建议为true
     * @return 文件的 uri
     */
    @Nullable
    public static Uri saveAlbum(Context context, Bitmap bitmap, Bitmap.CompressFormat format, int quality, boolean recycle) {
        String suffix;
        if (Bitmap.CompressFormat.JPEG == format)
            suffix = "JPG";
        else
            suffix = format.name();
        String fileName = System.currentTimeMillis() + "_" + quality + "." + suffix;
        if (Build.VERSION.SDK_INT < 29) {
            if (!isGranted(context)) {
                Log.e("ImageUtils", "save to album need storage permission");
                return null;
            }
            File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File destFile = new File(picDir, fileName);
            if (!save(bitmap, destFile, format, quality, recycle))
                return null;
            Uri uri = null;
            if (destFile.exists()) {
                uri = Uri.parse("file://" + destFile.getAbsolutePath());
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(uri);
                context.sendBroadcast(intent);
            }
            return uri;
        } else {
            // Android 10 使用
            Uri contentUri;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else
                contentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/");
            // 告诉系统，文件还未准备好，暂时不对外暴露
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
            Uri uri = context.getContentResolver().insert(contentUri, contentValues);
            if (uri == null) return null;
            OutputStream os = null;
            try {
                os = context.getContentResolver().openOutputStream(uri);
                bitmap.compress(format, quality, os);
                // 告诉系统，文件准备好了，可以提供给外部了
                contentValues.clear();
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                context.getContentResolver().update(uri, contentValues, null, null);
                return uri;
            } catch (Exception e) {
                e.printStackTrace();
                // 失败的时候，删除此 uri 记录
                context.getContentResolver().delete(uri, null, null);
                return null;
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private static boolean save(Bitmap bitmap, File file, Bitmap.CompressFormat format, int quality, boolean recycle) {
        if (isEmptyBitmap(bitmap)) {
            Log.e("ImageUtils", "bitmap is empty.");
            return false;
        }
        if (bitmap.isRecycled()) {
            Log.e("ImageUtils", "bitmap is recycled.");
            return false;
        }
        if (!createFile(file, true)) {
            Log.e("ImageUtils", "create or delete file <$file> failed.");
            return false;
        }
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = bitmap.compress(format, quality, os);
            if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return ret;
    }

    private static boolean isEmptyBitmap(Bitmap bitmap) {
        return bitmap == null || bitmap.isRecycled() || bitmap.getWidth() == 0 || bitmap.getHeight() == 0;
    }

    private static boolean createFile(File file, boolean isDeleteOldFile) {
        if (file == null) return false;
        if (file.exists()) {
            if (isDeleteOldFile) {
                if (!file.delete()) return false;
            } else
                return file.isFile();
        }
        if (!createDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean createDir(File file) {
        if (file == null) return false;
        if (file.exists())
            return file.isDirectory();
        else
            return file.mkdirs();
    }

    private static boolean isGranted(Context context) {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }
}
