package cn.pivotstudio.modulec.homescreen.oldversion.network;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommenRequestManager {
    public static void DeleteRequest(Context context,RequestInterface request,String holenumber){
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                //Call<ResponseBody> call = request.delete_hole("http://hustholetest.pivotstudio.cn/api/holes/" + holenumber);//进行封装
                Call<ResponseBody> call = request.delete_hole(holenumber);//进行封装
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).start();
    }
    public static void ReportRequest(Context context,RequestInterface request,String holenumber,String replynumber){
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("hole_id", holenumber);
                map.put("reply_local_id", replynumber);
                Call<ResponseBody> call = request.report(map);//进行封装
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(context, "举报成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "举报失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).start();
    }
    /*
    public static void ThumbupRequest(Context context, RequestInterface request, TextView){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+ mJoinedHolesList.get(position-1)[6]+"/-1");//进行封装
                Log.e(TAG, "token2：");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        is_thumbup.setImageResource(R.mipmap.active);
                        mJoinedHolesList.get(position-1)[11]="true";
                        mJoinedHolesList.get(position-1)[13]=(Integer.parseInt(mJoinedHolesList.get(position-1)[13])+1)+"";

                        thumbup_num.setText(mJoinedHolesList.get(position-1)[13]);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(),"点赞失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
        }
     */

}
