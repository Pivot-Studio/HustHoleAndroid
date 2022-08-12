package cn.pivotstudio.modulec.homescreen.oldversion.forest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.githang.statusbar.StatusBarCompat;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.MyImageView;

public class CropPictureActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final int REQUEST_CODE_GALLERY = 0x10;// 图库选取图片标识请求码
    private static final int CROP_PHOTO = 0x12;// 裁剪图片标识请求码
    private static final int STORAGE_PERMISSION = 0x20;// 动态申请存储权限标识

    /**
     * 控件宽度
     */
    private int mWidth;
    /**
     * 控件高度
     */
    private int mHeight;
    /**
     * 拿到src的图片
     */
    private Drawable mDrawable;
    /**
     * 图片宽度（使用前判断mDrawable是否null）
     */
    private int mDrawableWidth;
    /**
     * 图片高度（使用前判断mDrawable是否null）
     */
    private int mDrawableHeight;

    /**
     * 初始化缩放值
     */
    private float mScale;

    /**
     * 双击图片的缩放值
     */

    private File imageFile = null;// 声明File对象
    private Uri imageUri = null;// 裁剪后的图片uri
    private String path = "";
    private Button yes, no;
    private TextView titleBarTitle;
    private MyImageView photo;
    private ConstraintLayout titleBarBack;
    private ImageView mRevolve, mCropPicture;
    private AVLoadingIndicatorView mAVLoadingIndicatorView;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // 不同状态的表示：
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // 定义第一个按下的点，两只接触点的重点，以及出事的两指按下的距离：
    private PointF startPoint = new PointF();
    private PointF midPoint = new PointF();
    private float oriDis = 1f;
    private int number;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_croppicture);
        //ButterKnife.bind(this);// 控件绑定
        // 动态申请存储权限，后面读取文件有用
        requestStoragePermission();
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mAVLoadingIndicatorView =
            (AVLoadingIndicatorView) findViewById(R.id.titlebargreen_AVLoadingIndicatorView);
        mAVLoadingIndicatorView.hide();
        mAVLoadingIndicatorView.setVisibility(View.GONE);
        titleBarTitle = (TextView) findViewById(R.id.tv_titlebargreen_title);
        titleBarTitle.setText("裁剪图片");
        titleBarBack = (ConstraintLayout) findViewById(R.id.cl_titlebargreen_back);
        titleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        yes = (Button) findViewById(R.id.btn_croppicture_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAVLoadingIndicatorView.setVisibility(View.VISIBLE);
                mAVLoadingIndicatorView.show();
                titleBarTitle.setText("裁剪中...");
                try {
                    Bitmap bitmap1 = captureView(photo);
                    photo.init(CropPictureActivity.this);
                    photo.setImageBitmap(bitmap1);
                    ApplyForestActivity.setIcon(bitmap1);
                    titleBarTitle.setText("裁剪成功");
                    finish();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    mAVLoadingIndicatorView.hide();
                    mAVLoadingIndicatorView.setVisibility(View.GONE);
                    titleBarTitle.setText("裁剪失败");
                }
            }
        });
        no = (Button) findViewById(R.id.btn_croppicture_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCropPicture = (ImageView) findViewById(R.id.iv_croppicture_photo2);
        mRevolve = (ImageView) findViewById(R.id.iv_croppicture_revolve);
        mRevolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap != null) {
                    bitmap = rotateBmp(bitmap, 90);
                    photo = null;
                    photo = (MyImageView) findViewById(R.id.iv_croppicture_photo);
                    photo.init(CropPictureActivity.this);
                    photo.setImageBitmap(bitmap);
                }
           /*
             RotateAnimation rotate;

             rotate =new RotateAnimation(number*90f,number*90f+90f, Animation.RELATIVE_TO_SELF,
                     0.5f,Animation.RELATIVE_TO_SELF,0.5f);
             rotate.setDuration(200);
             rotate.setFillAfter(true);
             number++;
             photo.startAnimation(rotate);


            */

            }
        });
        photo = (MyImageView) findViewById(R.id.iv_croppicture_photo);
        //photo.setOnTouchListener(this);
        gallery();
    }

    public static Bitmap captureView(View view) throws Throwable {
        Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bm));
        return bm;
    }

    public static Bitmap rotateBmp(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {

            }
        }
        return b;
    }

    private void gallery() {
        Intent intent =
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 以startActivityForResult的方式启动一个activity用来获取返回的结果
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    /**
     * 接收#startActivityForResult(Intent, int)调用的结果
     *
     * @param requestCode 请求码 识别这个结果来自谁
     * @param resultCode 结果码
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {// 操作成功了

            switch (requestCode) {

                case REQUEST_CODE_GALLERY:// 图库选择图片

                    Uri uri = data.getData();// 获取图片的uri

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null,
                        null);// 从系统表中查询指定Uri对应的照片
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex); // 获取照片路径
                    cursor.close();
                    bitmap = BitmapFactory.decodeFile(picturePath);
                    photo.setImageBitmap(bitmap);

/*
                    mWidth = photo.getWidth();
                     mHeight = photo.getHeight();

                    //通过getDrawable获得Src的图片
                     mDrawable = photo.getDrawable();
                    if (mDrawable == null)
                        return;
                     mDrawableWidth = mDrawable.getIntrinsicWidth();
                     mDrawableHeight = mDrawable.getIntrinsicHeight();

                    initImageViewSize();
                    moveToCenter();


 */

                    break;

               /* case CROP_PHOTO:// 裁剪图片

                    try{

                        if (imageUri != null){
                            displayImage(imageUri);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;


                */
            }
        }
    }

    private void initImageViewSize() {
        if (mDrawable == null) {
            return;
        }

        // 缩放值
        float scale = 1.0f;
        // 图片宽度大于控件宽度，图片高度小于控件高度
        if (mDrawableWidth > mWidth && mDrawableHeight < mHeight)
        //scale = mWidth * 1.0f / mDrawableWidth;
        {
            scale = mHeight * 1.0f / mDrawableHeight;
        }
        // 图片高度度大于控件宽高，图片宽度小于控件宽度
        else if (mDrawableHeight > mHeight && mDrawableWidth < mWidth)
        //scale = mHeight * 1.0f / mDrawableHeight;
        {
            scale = mWidth * 1.0f / mDrawableWidth;
        }
        // 图片宽度大于控件宽度，图片高度大于控件高度
        else if (mDrawableHeight > mHeight && mDrawableWidth > mWidth) {
            scale = Math.max(mHeight * 1.0f / mDrawableHeight, mWidth * 1.0f / mDrawableWidth);
        }
        // 图片宽度小于控件宽度，图片高度小于控件高度
        else if (mDrawableHeight < mHeight && mDrawableWidth < mWidth) {
            scale = Math.max(mHeight * 1.0f / mDrawableHeight, mWidth * 1.0f / mDrawableWidth);
        }
        mScale = scale;
        //mMaxScale = mScale * 8.0f;
        // mMinScale = mScale * 0.5f;
    }

    /**
     * 移动控件中间位置
     */
    private void moveToCenter() {
        final float dx = mWidth / 2 - mDrawableWidth / 2;
        final float dy = mHeight / 2 - mDrawableHeight / 2;
        matrix = new Matrix();
        // 平移至中心
        matrix.postTranslate(dx, dy);
        // 以控件中心作为缩放
        matrix.postScale(mScale, mScale, mWidth / 2, mHeight / 2);
        photo.setImageMatrix(matrix);
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Float.valueOf(String.valueOf(Math.sqrt(x * x + y * y)));
    }

    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 单指
            case MotionEvent.ACTION_DOWN:
                matrix.set(view.getImageMatrix());
                savedMatrix.set(matrix);
                startPoint.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            // 双指
            case MotionEvent.ACTION_POINTER_DOWN:
                oriDis = distance(event);
                if (oriDis > 10f) {
                    savedMatrix.set(matrix);
                    midPoint = middle(event);
                    mode = ZOOM;
                }
                break;
            // 手指放开
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            // 单指滑动事件
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // 是一个手指拖动
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                } else if (mode == ZOOM) {
                    // 两个手指滑动
                    float newDist = distance(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oriDis;
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;
        }
        // 设置ImageView的Matrix
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * Android6.0后需要动态申请危险权限
     * 动态申请存储权限
     */
    private void requestStoragePermission() {

        int hasCameraPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG", "开始" + hasCameraPermission);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            // 拥有权限，可以执行涉及到存储权限的操作
            Log.e("TAG", "你已经授权了该组权限");
        } else {
            // 没有权限，向用户申请该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "向用户申请该组权限");
                requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    STORAGE_PERMISSION);
            }
        }
    }

    /**
     * 动态申请权限的结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意，执行相应操作
                Log.e("TAG", "用户已经同意了存储权限");
            } else {
                // 用户不同意，向用户展示该权限作用
            }
        }
    }

    /**
     * 创建File保存图片
     */
    private void createImageFile() {

        try {

            if (imageFile != null && imageFile.exists()) {
                imageFile.delete();
            }
            // 新建文件
            imageFile = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + "galleryDemo.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示图片
     *
     * @param imageUri 图片的uri
     */
    private void displayImage(Uri imageUri) {
        try {
            // glide根据图片的uri加载图片
            Glide.with(this)
                .load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_launcher)// 占位图设置：加载过程中显示的图片
                .error(R.mipmap.ic_launcher)// 异常占位图
                .transform(new CenterCrop())
                .into(photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


