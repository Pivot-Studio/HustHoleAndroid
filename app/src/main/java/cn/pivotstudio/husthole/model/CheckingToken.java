package cn.pivotstudio.husthole.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckingToken {
    private static Context context;
    public static void getContext(Context contexts){
        context=contexts;
    }
    public static Boolean IfTokenExist(){
        SharedPreferences editor = context.getSharedPreferences("Depository", Context.MODE_PRIVATE);//
        String token = editor.getString("token", "");
        if(token.equals("")){
        return false;
        }else{
        return true;
        }

    }
    public static void IfSessionExpired(){
        /*
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

         */
    }
    //public static Boolean IfFirstLogin(){

    //}
}

