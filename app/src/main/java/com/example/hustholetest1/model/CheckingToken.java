package com.example.hustholetest1.model;

import android.content.Context;
import android.content.SharedPreferences;

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
    //public static Boolean IfFirstLogin(){

    //}
}
