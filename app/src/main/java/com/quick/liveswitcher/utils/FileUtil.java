package com.quick.liveswitcher.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;

import androidx.core.content.FileProvider;


import com.quick.liveswitcher.BaseApp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @Description: 文件工具类
 */
public class FileUtil {
    private static final String VOICE_DIRNAME = "voice";
    private static final long MAX_SIZE = 2 * 1024 * 1024;//能够读取的最大文件 2M
    private static final float MAX_WIDTH = 1920f;
    private static final float MAX_HEIGHT = 1080f;
    /**
     * 删除7天外的底层log文件
     */
    private static final long day_7 = 3 * 24 * 60 * 60 * 1000;

    public static String getVersionName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前目录下的所有子文件并转换成集合
     *
     * @param dir
     * @return
     */
    public static List<File> getCurrentFilesList(File dir) {
        if (dir == null) return new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) return new ArrayList<>();
        return Arrays.asList(files);
    }

    public static String getPathByContentUri(Context context, Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    //                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    //                Log.i(TAG,"isMediaDocument***"+uri.toString());
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            } else {
                return uri.getPath();
            }
        }

        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        // final String column = MediaStore.Video.Media.DATA;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static String getPresetMaterialDir(Context context) {
        String modelPath = context.getExternalFilesDir(null).getAbsolutePath() + "/material/";
        return modelPath;
    }

    public static String getSceneShotDir(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath() + "/sceneShot/";
    }

    public static String getPresetAppInfoDir(Context context) {
        String modelPath = context.getExternalFilesDir(null).getAbsolutePath() + "/material/";
        return modelPath;
    }

    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            uri = Uri.fromFile(file);
        }
        Log.i("Live", "Live app install Uri:" + uri);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void copyPresetMaterialToFile(Context context) {
        String modelPath = getPresetMaterialDir(context);
        String[] presetDirs = {"background", "sound", "sticker", "media", "filter", "portrait"};
        for (String dir : presetDirs) {
            copyPresetMaterialToFile(context, modelPath + dir, dir);
        }
    }

    private static void copyPresetMaterialToFile(Context context, String modelPath, String modelName) {
        if (!isAssetFileExist(context, modelPath, modelName)) {
            final File modelDir = new File(modelPath);
            if (!modelDir.exists()) {
                modelDir.mkdirs();
            }
            AssetManager assetManager = context.getAssets();
            try {
                String[] children = assetManager.list(modelName);
                for (String child : children) {
                    File childFile = new File(modelPath + "/" + child);
                    child = modelName + "/" + child;
                    if (!childFile.exists() || assetManager.open(child).available() != childFile.length()) {
                        copyFile(assetManager.open(child), childFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyFile(InputStream in, File target) {
        if (target == null) return;
        boolean copySuccess = true;
        OutputStream out = null;
        try {
            out = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int counter;
            while ((counter = in.read(buffer)) != -1) {
                out.write(buffer, 0, counter);
            }
        } catch (IOException e) {
            e.printStackTrace();
            copySuccess = false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (!copySuccess) {
            target.delete();
        }
    }

    private static boolean isAssetFileExist(Context context, String modelPath, String modelName) {
        AssetManager assetManager = context.getAssets();
        String[] children;
        try {
            children = assetManager.list(modelName);
            for (String child : children) {
                File childFile = new File(modelPath + "/" + child);
                child = modelName + "/" + child;
                if (!childFile.exists() || assetManager.open(child).available() != childFile.length()) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 将assest目录下的文件拷贝到本地
     */
    private static String copyAssestToFile(String name, String filePath) {
        AssetManager assetManager = BaseApp.Companion.getInstance().getAssets();
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            inputStream = assetManager.open(name);
            fos = new FileOutputStream(filePath);
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            inputStream.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    /**
     * 创建抠图本地选择的文件夹
     */
    public static File cutImageDir(Context context) {
        File file = new File(context.getFilesDir(), "cutimgs");
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static String readAssetFile(String name) {
        AssetManager assetManager = BaseApp.Companion.getInstance().getAssets();
        String readStr = "";
        StringBuilder builder = new StringBuilder();
        try {
            InputStream open = assetManager.open(name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(open));
            while ((readStr = reader.readLine()) != null) {
                builder.append(readStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static void saveBitmapPNGFile(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteRecursively(File desDir) throws IOException {

        if (desDir == null || !desDir.exists()) {
            return;
        }
        if (desDir.isFile()) return;
        File[] subFile = desDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
        if (subFile == null) return;

        for (File f : subFile) {
            if (f.isDirectory()) {
                deleteRecursively(f);
                f.delete();
            } else {
                if (!f.getName().equals("resource.zip")) {
                    f.delete();
                }
            }
        }
        desDir.delete();
    }

    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                return false;
            } else if (!oldFile.isFile()) {
                return false;
            } else if (!oldFile.canRead()) {
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPathName);
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName, false);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean readFile(String file, byte[] buffer) {
        try {
            File oldFile = new File(file);
            if (!oldFile.exists()) {
                return false;
            } else if (!oldFile.isFile()) {
                return false;
            } else if (!oldFile.canRead()) {
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            if(fileInputStream.read(buffer) == -1) {
                fileInputStream.close();
                return false;
            }
            fileInputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取本地json
     * @param jsonPath
     * @return
     */
    public static String readLocalJson(String jsonPath) {
        StringBuffer sb = new StringBuffer();
        File file = new File(jsonPath);
        if (!file.exists()) {
            return sb.toString();
        }
        try {
            InputStream is = new FileInputStream(file);
            InputStreamReader isReader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isReader);
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    //判断编码格式方法

    private static String getFilecharset(InputStream inputStream) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; //文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; //文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; //文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; //文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                    {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                        // (0x80
                        // - 0xBF),也可能在GB编码内
                        {
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件后缀,不包括“.”
     */
    public static String getExtension(String pathOrUrl) {
        int dotPos = pathOrUrl.lastIndexOf('.');
        if (0 <= dotPos) {
            return pathOrUrl.substring(dotPos + 1);
        } else {
            return "ext";
        }
    }

    public static long getVideoDuration(String file) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(file);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int duration = player.getDuration();
        player.release();
        return duration;
    }

    public static String getAudioTitle(String file) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String result = new File(file).getName();
        try {
            mmr.setDataSource(file);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if(TextUtils.isEmpty(title) || TextUtils.isEmpty(artist)) return result;
            result = title + "-" + artist;
            mmr.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isImage(String fileNameExtension) {
        return (fileNameExtension.equals("jpg") || fileNameExtension.equals("jpeg") || fileNameExtension.equals("png")
                || fileNameExtension.equals("gif"));
    }

    public static boolean isVideo(String fileNameExtension) {
        return (fileNameExtension.equals("mp4") || fileNameExtension.equals("avi"));
    }

    public static boolean isAudio(String fileNameExtension) {
        return (fileNameExtension.equals("mp3") || fileNameExtension.equals("wav") || fileNameExtension.equals("m4a"));
    }


    private final static String[] documentWordExt = {"doc", "docx", "dot", "dotx", "wps", "wpt"};
    private final static String[] documentExcelExt = {"xls", "xlt", "xlsx", "xltx", "et", "ett"};
    private final static String[] documentPPTExt = {"ppt", "pptx", "pot", "potx", "pps", "dps", "dpt"};
    private final static String[] documentPDFExt = {"pdf"};
    private static final Set<String> sDocumentsMimeTypes = new ArraySet<>();
    static {
        sDocumentsMimeTypes.add("application/pdf");
        sDocumentsMimeTypes.add("application/vnd.ms-excel");
        sDocumentsMimeTypes.add("application/vnd.ms-excel.addin.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-excel.sheet.binary.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-excel.sheet.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-excel.template.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-powerpoint");
        sDocumentsMimeTypes.add("application/vnd.ms-powerpoint.addin.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-powerpoint.presentation.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-powerpoint.slideshow.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-powerpoint.template.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-word.document.macroenabled.12");
        sDocumentsMimeTypes.add("application/vnd.ms-word.template.macroenabled.12");
        sDocumentsMimeTypes.add(
                "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        sDocumentsMimeTypes.add(
                "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        sDocumentsMimeTypes.add(
                "application/vnd.openxmlformats-officedocument.presentationml.template");
        sDocumentsMimeTypes.add(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        sDocumentsMimeTypes.add(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        sDocumentsMimeTypes.add(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        sDocumentsMimeTypes.add(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
    }

    public static String getDocumentMimeType(String fileNameExt) {
        if(isPDF(fileNameExt)) return "application/pdf";
        return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    }

    public static String getWPSClassName(String fileNameExt) {
/*        if(isPPT(fileNameExt)) return "cn.wps.moffice.pdf.multiactivity.PDFReader1";
        else if(isExcel(fileNameExt)) return "cn.wps.moffice.spreadsheet.multiactivity.Spreadsheet1";
        else if(isPPT(fileNameExt)) return "cn.wps.moffice.presentation.multiactivity.Presentation1";
        else if(isWord(fileNameExt)) return "cn.wps.moffice.writer.multiactivity.Writer1";*/
        /*else*/ return "cn.wps.moffice.documentmanager.PreStartActivity2";
    }

    public static String[] getDocumentMime() {
        String[] y = sDocumentsMimeTypes.toArray(new String[0]);
        return y;
    }
    public static boolean isPDF(String fileNameExtension) {
        for(String ext : documentPDFExt) {
            if(fileNameExtension.equals(ext)) return true;
        }
        return false;
    }
    public static boolean isWord(String fileNameExtension) {
        for(String ext : documentWordExt) {
            if(fileNameExtension.equals(ext)) return true;
        }
        return false;
    }
    public static boolean isExcel(String fileNameExtension) {
        for(String ext : documentExcelExt) {
            if(fileNameExtension.equals(ext)) return true;
        }
        return false;
    }
    public static boolean isPPT(String fileNameExtension) {
        for(String ext : documentPPTExt) {
            if(fileNameExtension.equals(ext)) return true;
        }
        return false;
    }
    public static boolean isDocument(String fileNameExtension) {
        return isWord(fileNameExtension) || isExcel(fileNameExtension) || isPPT(fileNameExtension) || isPDF(fileNameExtension);
    }

    public static void saveNV21ToJpegFile(String fileName, byte[] nv21data, int width, int height, int quality) {
        File pictureFile = new File(fileName);
        FileOutputStream filecon = null;
        try {
            filecon = new FileOutputStream(pictureFile);
            YuvImage image = new YuvImage(nv21data, ImageFormat.NV21, width, height, null);
            Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
            image.compressToJpeg(
                    rect,
                    quality, filecon);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(filecon);
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static void dumpNV21(byte[] data, String fileName) {
        FileOutputStream fos = null;
        try {
            File file = new File(fileName);
            if(file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void appendDumpNV21(byte[] data, String fileName) {
        RandomAccessFile accessFile = null;
        try {
            File file = new File(fileName);
            if(file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            accessFile = new RandomAccessFile(fileName, "rw");
            long fileLength = accessFile.length();
            accessFile.seek(fileLength);
            accessFile.write(data);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
