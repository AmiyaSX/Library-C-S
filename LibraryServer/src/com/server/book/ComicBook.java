package com.server.book;

import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;

@JSONType(typeName = "comicBook")
public class ComicBook extends Book implements Serializable {
    private static final long serialVersionUID = -7038013250969447902L;

    String paintingStyle;
    String mainManRole;
    String mainWomanRole;

    public ComicBook() {
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
}
