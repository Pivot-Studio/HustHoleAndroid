package com.example.hustholetest1.model;

import org.json.JSONArray;

import java.util.ArrayList;

public class Forest {//存放所有小树林的未处理数据
    static JSONArray[] forest_list=new JSONArray[6];
    //static
    public static void setForest_list(int number,JSONArray forest_list_1){ forest_list[number]=forest_list_1;
    }
    public static JSONArray getForest_list(int number2){
        return forest_list[number2];
    }
}
