package com.server.book;

import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;

@JSONType(typeName = "programmingBook")
public class ProgrammingBook extends Book implements Serializable {
    private static final long serialVersionUID = -5576606865676605060L;

    String language;
    String authorUrl;

    public ProgrammingBook() {
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
