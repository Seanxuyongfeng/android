package com.android.prospect.entity;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Movice implements Parcelable,Serializable{
private String imgPath;
private int id;
private String title;
private String desc;
private String url;
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getImgPath() {
	return imgPath;
}
public void setImgPath(String imgPath) {
	this.imgPath = imgPath;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getDesc() {
	return desc;
}
public void setDesc(String desc) {
	this.desc = desc;
}

public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}

public void writeToParcel(Parcel arg0, int arg1) {
	// TODO Auto-generated method stub
	
}
}
