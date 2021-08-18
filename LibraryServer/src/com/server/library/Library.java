package com.server.library;


import com.server.book.Book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.server.library.Server.library;


public class Library implements Serializable {
    private static final long serialVersionUID = -1318915050301893151L;

    private HashMap<String, String> users;
    private HashMap<String, BookCase> bookCases;
    /**
     * 1、馆内所有书的分类
     * 2、所有的库存图书（按分类存放）
     */
    private final HashSet<String> bookTypes;
    private final HashMap<String, ArrayList<Book>> bookTypeListHashMap;

    /*Serializable反序列化对象不会调用构造方法*/
    Library() {
        bookTypes = new HashSet<>();
        bookTypeListHashMap = new HashMap<>();
        users = new HashMap<>();
        bookCases = new HashMap<>();
        bookTypeListHashMap.put("comic", new ArrayList<>());
        bookTypeListHashMap.put("cuisine", new ArrayList<>());
        bookTypeListHashMap.put("programming", new ArrayList<>());
        bookTypes.add("comic");
        bookTypes.add("cuisine");
        bookTypes.add("programming");
    }

    //入库操作，书籍上架或归还
    public synchronized void addBook(Book book) {
        if (!bookTypeListHashMap.containsKey(book.getType())) {
            ArrayList<Book> lists = new ArrayList<>();
            lists.add(book);
            bookTypeListHashMap.put(book.getType(), lists);
            System.out.println("图书" + book.getBookName() + "上架成功！");
        } else if (!bookTypes.contains(book.getType())) {
            System.out.println("没有属于该书的分类." + book.getBookName() + "上架失败!");
        } else {
            bookTypeListHashMap.get(book.getType()).add(book);
            System.out.println("图书" + book.getBookName() + "上架成功！");
        }
        showBookTypes();
        showBooks();

    }


    private void showBookTypes() {
        System.out.println("---------library现有图书种类---------");
        for (String bookType : bookTypes) {
            System.out.println(bookType);
        }
        System.out.println("-----------------------------------");
    }

    private void showBooks() {
        for (String bookType : bookTypeListHashMap.keySet()) {
            System.out.println(bookType + "：");
            for (Book book : bookTypeListHashMap.get(bookType))
                System.out.print(book.getBookName() + "  ");
            System.out.println();
        }
        System.out.println("-----------------------------------");
    }

    //增加书籍分类
    public void addType(String bookType) {
        bookTypes.add(bookType);
    }

    //出库操作，书籍下架或借出
    public synchronized boolean delBook(String bookName, String bookType) {

        if (bookTypeListHashMap.containsKey(bookType)) {
            boolean b = bookTypeListHashMap.get(bookType).removeIf(book -> book.getBookName().equals(bookName));
            showBookTypes();
            showBooks();
            return b;
        }
        return false;
    }

    public HashMap<String, String> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, String> users) {
        this.users = users;
    }

    public HashMap<String, BookCase> getBookCases() {
        return bookCases;
    }

    public void setBookCases(HashMap<String, BookCase> bookCases) {
        this.bookCases = bookCases;
    }

    public HashSet<String> getBookTypes() {
        return bookTypes;
    }

    public HashMap<String, ArrayList<Book>> getBookTypeListHashMap() {
        return bookTypeListHashMap;
    }

}
