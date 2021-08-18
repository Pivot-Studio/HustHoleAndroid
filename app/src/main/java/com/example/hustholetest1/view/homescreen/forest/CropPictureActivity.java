package com.example.hustholetest1.view.homescreen.forest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import com.example.hustholetest1.R;
import com.githang.statusbar.StatusBarCompat;

import java.io.File;

public class CropPictureActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GALLERY = 0x10;// 图库选取图片标识请求码
    private static final int CROP_PHOTO = 0x12;// 裁剪图片标识请求码
    private static final int STORAGE_PERMISSION = 0x20;// 动态申请存储权限标识

    //@BindView(R.id.iv_pic)ImageView iv_pic;// imageView控件

    private File imageFile = null;// 声明File对象
    private Uri imageUri = null;// 裁剪后的图片uri
    private String path = "";
    private Button yes,no;
    private TextView titleBarTitle;
    private ImageView photo,titleBarBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_croppicture);
        //ButterKnife.bind(this);// 控件绑定
        // 动态申请存储权限，后面读取文件有用
        requestStoragePermission();
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        titleBarTitle=(TextView)findViewById(R.id.tv_titlebargreen_title);
        titleBarTitle.setText("裁剪图片");
        titleBarBack=(ImageView) findViewById(R.id.iv_titlebargreen_back);
        titleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        yes=(Button)findViewById(R.id.btn_croppicture_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });
     no=(Button)findViewById(R.id.btn_croppicture_no);
     no.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          finish();
        }
    });
     photo=(ImageView) findViewById(R.id.iv_croppicture_photo);
}
    /**
     * 单击事件绑定
     */
    /*@OnClick({R.id.btn_gallery})
    public void doClick(View view){

        switch (view.getId()){

            case R.id.btn_gallery:// 图库选择


                break;

        }

    }*/


    /**
     * 图库选择图片
     */
    private void gallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 以startActivityForResult的方式启动一个activity用来获取返回的结果
        startActivityForResult(intent, REQUEST_CODE_GALLERY);

    }

    /**
     * 接收#startActivityForResult(Intent, int)调用的结果
     * @param requestCode 请求码 识别这个结果来自谁
     * @param resultCode    结果码
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){// 操作成功了

            switch (requestCode){

                case REQUEST_CODE_GALLERY:// 图库选择图片

                    Uri uri = data.getData();// 获取图片的uri

                    Intent intent_gallery_crop = new Intent("com.android.camera.action.CROP");
                    intent_gallery_crop.setDataAndType(uri, "image/*");

                    // 设置裁剪
                    intent_gallery_crop.putExtra("crop", "true");
                    intent_gallery_crop.putExtra("scale", true);
                    // aspectX aspectY 是宽高的比例
                    intent_gallery_crop.putExtra("aspectX", 1);
                    intent_gallery_crop.putExtra("aspectY", 1);
                    // outputX outputY 是裁剪图片宽高
                    intent_gallery_crop.putExtra("outputX", 400);
                    intent_gallery_crop.putExtra("outputY", 400);

                    intent_gallery_crop.putExtra("return-data", false);

                    // 创建文件保存裁剪的图片
                    createImageFile();
                    imageUri = Uri.fromFile(imageFile);

                    if (imageUri != null){
                        intent_gallery_crop.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent_gallery_crop.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    }

                    startActivityForResult(intent_gallery_crop, CROP_PHOTO);

                    break;

                case CROP_PHOTO:// 裁剪图片

                    try{

                        if (imageUri != null){
                            displayImage(imageUri);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;

            }

        }
    }

    /**
     * Android6.0后需要动态申请危险权限
     * 动态申请存储权限
     */
    private void requestStoragePermission() {

        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG","开始" + hasCameraPermission);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED){
            // 拥有权限，可以执行涉及到存储权限的操作
            Log.e("TAG", "你已经授权了该组权限");
        }else {
            // 没有权限，向用户申请该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "向用户申请该组权限");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            }
        }

    }

    /**
     * 动态申请权限的结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // 用户同意，执行相应操作
                Log.e("TAG","用户已经同意了存储权限");
            }else {
                // 用户不同意，向用户展示该权限作用
            }
        }

    }

    /**
     * 创建File保存图片
     */
    private void createImageFile() {

        try{

            if (imageFile != null && imageFile.exists()){
                imageFile.delete();
            }
            // 新建文件
            imageFile = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + "galleryDemo.jpg");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 显示图片
     * @param imageUri 图片的uri
     */
    private void displayImage(Uri imageUri) {
        try{
            // glide根据图片的uri加载图片
            Glide.with(this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher)// 占位图设置：加载过程中显示的图片
                    .error(R.mipmap.ic_launcher)// 异常占位图
                    .transform(new CenterCrop())
                    .into(photo);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}


