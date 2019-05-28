package com.example.tomindapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonWikiParse {
    String bigTitle;
    ArrayList<String> title;
    ArrayList<String> desc;
    ArrayList<String> link;

    public JsonWikiParse(String s){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList>(){}.getType();
        ArrayList read = gson.fromJson(s,type);
        bigTitle = read.get(0).toString();
        title = (ArrayList<String>) read.get(1);
        desc = (ArrayList<String>) read.get(2);
        link = (ArrayList<String>) read.get(3);


    }
}
