package com.example.harmonia;

public class Upload {

    private String mName;
    private String mImageUrl;

    Upload(){

    }

    public Upload(String name, String imageUrl){
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public String getmImageUrl(){
        return mImageUrl;
    }

    public void setmImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }
}
