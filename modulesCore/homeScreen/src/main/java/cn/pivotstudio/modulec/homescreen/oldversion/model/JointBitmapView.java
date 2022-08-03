package cn.pivotstudio.modulec.homescreen.oldversion.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class JointBitmapView extends View {
    private static Bitmap bitmap;

    public JointBitmapView(Context context, Bitmap bit1, Bitmap bit2) {
        super(context);
        bitmap = newBitmap(bit1, bit2);
    }

    /**
     * 拼接图片
     *
     * @return 返回拼接后的Bitmap
     */
    private static Bitmap newBitmap(Bitmap bit1, Bitmap bit2) {

        int width = bit1.getWidth();
        int height = bit1.getHeight() + bit2.getHeight();
        //创建一个空的Bitmap(内存区域),宽度等于第一张图片的宽度，高度等于两张图片高度总和
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //将bitmap放置到绘制区域,并将要拼接的图片绘制到指定内存区域
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bit1, 0, 0, null);
        canvas.drawBitmap(bit2, 0, bit1.getHeight(), null);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        bitmap.recycle();
    }
}

