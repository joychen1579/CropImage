package cn.joychen.crop;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import org.joychen.cropmodle.CropActivity;
import org.joychen.cropmodle.CropUtil;
import org.joychen.permisslib.PermissionManager;
import org.joychen.permisslib.PermissionRequest;
import org.joychen.permisslib.listeners.OnPermissionRequestorListener;
import org.joychen.sheetdialogios.IosSheetDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final int REQUEST_PERMISSION_CAMERA = 10;
    private static final int REQUEST_PERNISSION_GALLERY = 11;


    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    public static final int PHOTO_REQUEST_CUT = 3;// 剪裁结果

    private ImageView avaterImg;
    private IosSheetDialog selectDialog;


    private File mCameraPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        avaterImg = (ImageView) findViewById(R.id.avater_img);
        findViewById(R.id.item_rl).setOnClickListener(this);
        PermissionManager.config("提示", "获取权限", "取消", null);
    }


    @Override
    public void onClick(View view) {
        showSelectDialog();
    }


    private void showSelectDialog() {
        selectDialog = new IosSheetDialog(this).builder().setTitle("选择");
        selectDialog.addSheetItem("拍照", IosSheetDialog.SheetItemColor.Blue, new IosSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                final String[] permissionsNeeded = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                PermissionManager.requestPermission(MainActivity.this, new PermissionRequest(REQUEST_PERMISSION_CAMERA, "请求拍照权限", permissionsNeeded, new OnPermissionRequestorListener() {
                    @Override
                    public void gotPermissions() {
                        callSystemCamera();
                    }

                    @Override
                    public void rejectPermissions(List<String> rejects) {
                        Toast.makeText(MainActivity.this, "没有权访问", Toast.LENGTH_SHORT).show();
                    }
                }));
            }
        }).addSheetItem("选择图片", IosSheetDialog.SheetItemColor.Blue, new IosSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                final String[] permissionsNeeded = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                PermissionManager.requestPermission(MainActivity.this, new PermissionRequest(REQUEST_PERNISSION_GALLERY, "请求拍照权限", permissionsNeeded, new OnPermissionRequestorListener() {
                    @Override
                    public void gotPermissions() {
                        callSystemGallery();
                    }

                    @Override
                    public void rejectPermissions(List<String> rejects) {
                        Toast.makeText(MainActivity.this, "没有权访问", Toast.LENGTH_SHORT).show();
                    }
                }));

            }
        });
        selectDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 调用照相
     */
    private void callSystemCamera() {
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCameraPhotoFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraPhotoFile));
        startActivityForResult(cameraintent, PHOTO_REQUEST_TAKEPHOTO);
    }

    private void callSystemGallery() {
        Intent intent;
        intent = new Intent(Intent.ACTION_PICK); //兼容4.4及以下的API
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }


    /**
     * 此处写方法描述
     *
     * @param data
     * @return void
     * @Title: readLocalImage
     * @date 2012-12-12 上午11:26:35
     */
    private void readLocalImage(File data) {
        if (data == null) {
            return;
        }
        Uri uri = null;
        uri = Uri.fromFile(data);
        if (uri != null) {
            startPhotoCrop(uri, PHOTO_REQUEST_CUT); // 图片裁剪
        }
    }

    /**
     * 此处写方法描述
     *
     * @param data
     * @return void
     * @Title: readLocalImage
     * @date 2012-12-12 上午11:26:35
     */
    private void readLocalImage(Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = null;
        uri = data.getData();
        if (uri != null) {
            startPhotoCrop(uri, PHOTO_REQUEST_CUT); // 图片裁剪
        }
    }


    /**
     * 开始裁剪
     *
     * @param uri
     * @param reqCode
     * @return void
     * @Title: startPhotoCrop
     * @date 2012-12-12 上午11:15:38
     */
    private void startPhotoCrop(Uri uri, int reqCode) {
        Intent intent = new Intent(CropActivity.ACTION_CROP_IMAGE);
        intent.putExtra(CropActivity.IMAGE_URI, uri);
        startActivityForResult(intent, reqCode);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
                if (mCameraPhotoFile.length() > 0)
//                    startPhotoZoom(Uri.fromFile(mCameraPhotoFile));
                    readLocalImage(mCameraPhotoFile);
                break;
            case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
                // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                readLocalImage(data);
                break;
            case PHOTO_REQUEST_CUT:// 返回的结果
                if (data != null)
                    readCropImage(data);
                break;
        }
    }


    /**
     * 此处写方法描述
     *
     * @param data
     * @return void
     * @Title: readCropImage
     * @date 2012-12-12 上午11:27:52
     */
    @SuppressWarnings("deprecation")
    private void readCropImage(Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getParcelableExtra(CropActivity.CROP_IMAGE_URI);
        Bitmap photo = CropUtil.getBitmap(this, uri);
        avaterImg.setImageBitmap(photo);

    }
}
