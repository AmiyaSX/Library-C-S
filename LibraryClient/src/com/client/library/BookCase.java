package com.client.library;


import com.client.book.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.client.book.BookType.*;

public class BookCase implements Serializable {
    private static final long serialVersionUID = 5963650368912406232L;

    private String userId;
    private ArrayList<Book> books;
    private HashSet<String> bookTypes;
    private HashMap<String, ArrayList<Book>> bookTypeListHashMap;

    /* fastJson序列化时对构造方法有依赖 (要么只有默认 要么提供全参) */
    public BookCase(String userId, ArrayList<Book> books, HashSet<String> bookTypes, HashMap<String, ArrayList<Book>> bookTypeListHashMap) {
        this.userId = userId;
        this.books = books;
        this.bookTypes = bookTypes;
        this.bookTypeListHashMap = bookTypeListHashMap;
    }

    /**
     * 为每个书柜初始化一个默认分类，用来存放没有被用户分类的书籍。（可理解为无序状态）
     */
    public BookCase(String userId) {
        this.userId = userId;
        books = new ArrayList<>();
        bookTypes = new HashSet<>();
        bookTypeListHashMap = new HashMap<>();
        bookTypes.add("默认分类");
        ArrayList<Book> bookArrayList = new ArrayList<>();
        bookTypeListHashMap.put("默认分类", bookArrayList);
    }

    public void addBook(Book book) {
        if (bookTypes.contains(book.getUserTypeSet())) {
            switch (book.getType()) {
                case BOOK_COMIC:
                    ComicBook comicBook = (ComicBook) book;
                    bookTypeListHashMap.get(book.getUserTypeSet()).add(comicBook);
                    books.add(comicBook);
                    break;
                case BOOK_CUISINE:
                    CuisineCookBook cuisineCookBook = (CuisineCookBook) book;
                    bookTypeListHashMap.get(book.getUserTypeSet()).add(cuisineCookBook);
                    books.add(cuisineCookBook);
                    break;
                case BOOK_PROGRAMMING:
                    ProgrammingBook programmingBook = (ProgrammingBook) book;
                    bookTypeListHashMap.get(book.getUserTypeSet()).add(programmingBook);
                    books.add(programmingBook);
                    break;

            }
        }
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
