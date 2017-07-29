package com.android.prospect.entity;

import java.io.Serializable;

public class Picture implements  Serializable{
 private String thumb;
 private String title;
 private String url;
 private String id;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getThumb() {
	return thumb;
}
public void setThumb(String thumb) {
	this.thumb = thumb;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
}
