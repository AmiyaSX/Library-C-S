package com.server.book;


import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;

@JSONType(seeAlso = {ComicBook.class,CuisineCookBook.class,ProgrammingBook.class})
public class Book implements Serializable {

    private static final long serialVersionUID = -6018967580768472098L;

    /*---------Manager设置----------*/
    private String bookName;
    private String pages;
    private String price;
    /*-----------------------------*/
    private String type;    //在library中的分类
    private String userTypeSet; //用户自定义分类
    /*-----------------------------*/
    private String paintingStyle;
    private String mainManRole;
    private String mainWomanRole;
    private String cuisineStyle;
    private String cuisineNum;
    private String language;
    private String authorUrl;

    public Book() {
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserTypeSet() {
        return userTypeSet;
    }

    public void setUserTypeSet(String userTypeSet) {
        this.userTypeSet = userTypeSet;
    }

    public String getPaintingStyle() {
        return paintingStyle;
    }

    public void setPaintingStyle(String paintingStyle) {
        this.paintingStyle = paintingStyle;
    }

    public String getMainManRole() {
        return mainManRole;
    }

    public void setMainManRole(String mainManRole) {
        this.mainManRole = mainManRole;
    }

    public String getMainWomanRole() {
        return mainWomanRole;
    }

    public void setMainWomanRole(String mainWomanRole) {
        this.mainWomanRole = mainWomanRole;
    }

    public String getCuisineStyle() {
        return cuisineStyle;
    }

    public void setCuisineStyle(String cuisineStyle) {
        this.cuisineStyle = cuisineStyle;
    }

    public String getCuisineNum() {
        return cuisineNum;
    }

    public void setCuisineNum(String cuisineNum) {
        this.cuisineNum = cuisineNum;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }
}
