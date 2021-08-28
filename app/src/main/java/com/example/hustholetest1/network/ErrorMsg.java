package com.example.hustholetest1.network;


import android.content.Context;
import android.widget.Toast;

import com.example.hustholetest1.R;
import com.example.hustholetest1.view.homescreen.forest.AllForestsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ErrorMsg {
    public static void getErrorMsg(Response<ResponseBody> response, Context context){
        String json = "null";
        String returncondition = null;
        if (response.errorBody() != null) {
            try {
                json = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(json);
                returncondition = jsonObject.getString("msg");
                Toast.makeText(context, returncondition, Toast.LENGTH_SHORT).show();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context, R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
        }
    }
}
