package com.android.prospect.entity;

import java.io.Serializable;


public class Food implements  Serializable{
	
  private String thumb;
  private String id;
  private String title;
  private String desc;
  private String reqirment;
  private String content;
  private String url;
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getThumb() {
	return thumb;
}
public void setThumb(String thumb) {
	this.thumb = thumb;
}
public String getId() {
	return id;
}
public void setId(String id) {
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
public String getReqirment() {
	return reqirment;
}
public void setReqirment(String reqirment) {
	this.reqirment = reqirment;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
  
}
