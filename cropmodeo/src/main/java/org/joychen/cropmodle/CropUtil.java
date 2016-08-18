package org.joychen.cropmodle;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 16/8/18
 *
 * @author joyChen
 * @version 1.0
 * @email joychen1579@163.com
 */
public class CropUtil {


    /**
     * 剪裁之前的预处理
     * @param uri
     * @param duplicatePath
     * @return
     */
    public static Uri preCrop(Context context,Uri uri, String duplicatePath) {
        Uri duplicateUri = null;

        if (duplicatePath == null) {
            duplicateUri = getDuplicateUri(context,uri);
        } else {
            duplicateUri = getDuplicateUri(context,uri, duplicatePath);
        }

        //rotateImage();

        return duplicateUri;
    }


    /**
     * 此处写方法描述
     *
     * @return void
     * @uri: getBitmap
     * @date 2012-12-13 下午8:22:23
     */
    public  static Bitmap getBitmap(Context context,Uri uri) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {

            is = getInputStream(context,uri);

            bitmap = BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }


    /**
     * 获取输入流
     *
     * @param mUri
     * @return InputStream
     * @Title: getInputStream
     * @date 2012-12-14 上午9:00:31
     */
    private  static InputStream getInputStream(Context context,Uri mUri) throws IOException {
        try {
            if (mUri.getScheme().equals("file")) {
                return new FileInputStream(mUri.getPath());
            } else {
                return context.getContentResolver().openInputStream(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }




    /**
     * 设置获取裁剪后图像的uri
     *
     * @param uri
     * @return Uri
     * @Title: getDuplicateUri
     * @date 2012-11-26 下午12:25:16
     */
    private static Uri getDuplicateUri(Context contexdt,Uri uri) {
        Uri duplicateUri = null;

        String uriString = getUriString(contexdt,uri);

        duplicateUri = getDuplicateUri(contexdt,uri, uriString);

        return duplicateUri;
    }


    /**
     * 如果是拍照的话就直接获取了
     *
     * @param uri
     * @param uriString
     * @return Uri
     * @Title: getDuplicateUri
     * @date 2012-11-28 下午6:30:38
     */
    private  static Uri getDuplicateUri(Context context,Uri uri, String uriString) {

        Uri duplicateUri = null;
        String duplicatePath = null;
        duplicatePath = uriString.replace(".", "_duplicate.");

        //cropImagePath = uriString;
        //判断原图是否旋转，旋转了进行修复
        rotateImage(uriString);

        duplicateUri = Uri.fromFile(new File(duplicatePath));

        return duplicateUri;
    }

    /**
     * 旋转图象
     *
     * @return void
     * @Title: rotateImage
     * @date 2012-12-4 上午10:18:53
     */
    private static void rotateImage(String uriString) {

        try {
            ExifInterface exifInterface = new ExifInterface(uriString);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
                    orientation == ExifInterface.ORIENTATION_ROTATE_180 ||
                    orientation == ExifInterface.ORIENTATION_ROTATE_270) {

                String value = String.valueOf(orientation);
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, value);
                //exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
                exifInterface.saveAttributes();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据Uri获取文件的路径
     *
     * @param uri
     * @return String
     * @Title: getUriString
     * @date 2012-11-28 下午1:19:31
     */
    private static String getUriString(Context context, Uri uri) {
        String imgPath = null;
        if (uri != null) {
            String uriString = uri.toString();
            //小米手机的适配问题，小米手机的uri以file开头，其他的手机都以content开头
            //以content开头的uri表明图片插入数据库中了，而以file开头表示没有插入数据库
            //所以就不能通过query来查询，否则获取的cursor会为null。
            if (uriString.startsWith("file")) {
                //uri的格式为file:///mnt....,将前七个过滤掉获取路径
                imgPath = uriString.substring(7, uriString.length());

                return imgPath;
            }
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            imgPath = cursor.getString(1); // 图片文件路径

        }
        return imgPath;
    }

}
