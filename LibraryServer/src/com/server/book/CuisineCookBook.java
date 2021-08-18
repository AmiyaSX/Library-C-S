package com.server.book;

import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;

@JSONType(typeName = "cuisineCookBook")
public class CuisineCookBook extends Book implements Serializable {
    private static final long serialVersionUID = 8464406368752013584L;

    String cuisineStyle;
    String cuisineNum;

    public CuisineCookBook() {
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
}
