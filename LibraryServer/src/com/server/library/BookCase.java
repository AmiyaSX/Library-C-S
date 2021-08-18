package com.server.library;


import com.server.book.Book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BookCase implements Serializable {
    private static final long serialVersionUID = -6463528357540615930L;
    private String userId;
    private ArrayList<Book> books;
    private HashSet<String> bookTypes;
    private HashMap<String, ArrayList<Book>> bookTypeListHashMap;

    public BookCase() {
    }

    /*为每个书柜初始化一个默认分类，用来存放没有被用户分类的书籍。（可理解为无序状态）*/
    public BookCase(String userId) {
        books = new ArrayList<>();
        bookTypes = new HashSet<>();
        this.userId = userId;
        bookTypeListHashMap = new HashMap<>();
        bookTypes.add("默认分类");
        ArrayList<Book> bookArrayList = new ArrayList<>();
        bookTypeListHashMap.put("默认分类",bookArrayList);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    public void setBookTypes(HashSet<String> bookTypes) {
        this.bookTypes = bookTypes;
    }

    public void setBookTypeListHashMap(HashMap<String, ArrayList<Book>> bookTypeListHashMap) {
        this.bookTypeListHashMap = bookTypeListHashMap;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public HashSet<String> getBookTypes() {
        return bookTypes;
    }

    public HashMap<String, ArrayList<Book>> getBookTypeListHashMap() {
        return bookTypeListHashMap;
    }
}
