package com.client.library;

import com.alibaba.fastjson.JSON;
import com.client.book.*;
import com.client.msgutil.MsgConfig;
import com.client.msgutil.MsgPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static com.client.book.BookType.*;
import static com.client.library.Client.msgHandle;

public class UserSystem {
    /*用户与图书系统连接的桥梁，管理所有用户行为*/
    String id;
    BookCase bookCase;

    public UserSystem(String id, BookCase bookCase) {
        this.bookCase = bookCase;
        this.id = id;
    }

    public void userAction() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String choice = "1";
        while (!choice.equals("exit")) {
            System.out.println("欢迎进入个人界面！" + "\n" + "请选择您要进行的操作exit/1/2/3: exit.退出 1.借书 2.还书 3.查看个人书柜");
            choice = scanner.next();
            switch (choice) {
                case "1":
                    brwBook(scanner);
                    break;
                case "2":
                    rtnBook(scanner);
                    break;
                case "3":
                    checkBookCase(scanner);
                    break;
            }

        }

    }

    /*查看书柜*/
    private void checkBookCase(Scanner scanner) throws IOException {

        String choice;
        label:
        while (true) {
            System.out.println("请选择您要进行的操作0/1/2/3: 0.返回 1.直接查看 2.分类查看 3.分类管理");
            choice = scanner.next();
            switch (choice) {
                case "0":
                    break label;
                case "1":
                    showBooks();
                    break;
                case "2":
                    showBooksByTypes();
                    break;
                case "3":
                    System.out.println("请选择您要进行的个人书柜分类管理操作1/2/3: 1.新增分类 2.删除分类 3.书籍分类");
                    choice = scanner.next();
                    switch (choice) {
                        case "1":
                            addBookTypeSet(scanner);
                            break;
                        case "2":
                            delBookTypeSet(scanner);
                            break;
                        case "3":
                            sortBook(scanner);
                            break;
                        default:
                            System.out.println("错误的选择");
                            continue;
                    }
                    /* 触发服务端更新书柜操作 */
                    HashMap<String, String> hashMap = new HashMap<>();
                    String jsonBookcase = JSON.toJSONString(bookCase);
                    hashMap.put("id", id);
                    hashMap.put("bookCase", jsonBookcase);
                    String jsonString = JSON.toJSONString(hashMap);
                    MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_BOOKCASE_ALTER, jsonString);
                    msgPacket.sendPacket();
                    break;
                default:
                    System.out.println("错误的选择！");
                    break;
            }
        }

    }

    private void sortBook(Scanner scanner) {
        String bookName;
        String originBookType = "";
        String aimBookType;
        System.out.println("请输入您要进行分类的图书的名字：");
        bookName = scanner.next();
        System.out.println("请输入该图书要归入的类别名称：");
        aimBookType = scanner.next();
        if (!bookCase.getBookTypes().contains(aimBookType)) {
            System.out.println("该分类不存在！分类失败！");
            return;
        }
        for (Book book : bookCase.getBooks()) {
            if (book.getBookName().equals(bookName)) {
                originBookType = book.getUserTypeSet();
                book.setUserTypeSet(aimBookType);
                bookCase.getBookTypeListHashMap().get(aimBookType).add(book);
                break;
            }
        }
        if (originBookType.equals("")) {
            System.out.println("不存在这本书！分类失败！");
        } else {
            bookCase.getBookTypeListHashMap().get(originBookType).removeIf(book -> book.getBookName().equals(bookName));
            System.out.println("分类成功！");
        }
    }

    private void delBookTypeSet(Scanner scanner) {
        System.out.println("请输入您要删除的分类名称：");
        String bookTypeSet = scanner.next();
        if (!bookCase.getBookTypes().contains(bookTypeSet)) {
            System.out.println("分类不存在！删除失败！");
        } else {
            bookCase.getBookTypes().remove(bookTypeSet);
            for (Book book : bookCase.getBookTypeListHashMap().get(bookTypeSet)) {
                book.setUserTypeSet("默认分类");
                bookCase.getBookTypeListHashMap().get("默认分类").add(book);
            }
            bookCase.getBookTypeListHashMap().remove(bookTypeSet);
            for (Book book : bookCase.getBooks()) {
                if (book.getUserTypeSet().equals(bookTypeSet)) {
                    book.setUserTypeSet("默认分类");
                }
            }
        }
    }

    private void addBookTypeSet(Scanner scanner) {
        System.out.println("请输入您要增加的分类名称：");
        String bookTypeSet = scanner.next();
        ArrayList<Book> books = new ArrayList<>();
        bookCase.getBookTypes().add(bookTypeSet);
        bookCase.getBookTypeListHashMap().put(bookTypeSet, books);
    }

    /*按分类显示书柜中的图书*/
    private void showBooksByTypes() {
        for (String bookTypeSet : bookCase.getBookTypes()) {
            System.out.println(bookTypeSet + ":");
            for (Book book : bookCase.getBookTypeListHashMap().get(bookTypeSet)) {
                System.out.print(bookCase.getBookTypeListHashMap().get(bookTypeSet).indexOf(book) + 1 + "." + "书名：");
                showBookInformation(book);
            }
            System.out.println("-----------------------------------------");
        }
    }

    /*不按分类显示书柜中全部图书*/
    private void showBooks() {
        for (Book book : bookCase.getBooks()) {
            System.out.print(bookCase.getBooks().indexOf(book) + 1 + "." + "书名：");
            showBookInformation(book);
        }
        System.out.println("-----------------------------------------");
    }

    /* 对特定的一本书，针对不同的种类来进行信息显示 */
    private void showBookInformation(Book book) {
        System.out.println(book.getBookName());
        System.out.println("类型：" + book.getType() + "   页数：" + book.getPages() + "   价格：" + book.getPrice());
        switch (book.getType()) {
            case BOOK_COMIC:
                showComicInformation(book);
                break;
            case BOOK_PROGRAMMING:
                showProgrammingInformation( book);
                break;
            case BOOK_CUISINE:
                showCuisineInformation(book);
                break;
        }
    }

    private void showCuisineInformation(Book book) {
        /*特殊信息*/
        System.out.println("所属菜系：" + book.getCuisineStyle() + "   菜数：" + book.getCuisineNum());
    }

    private void showProgrammingInformation(Book book) {
        /*特殊信息*/
        System.out.println("编程语言：" + book.getLanguage() + "   作者博客链接：" + book.getAuthorUrl());
    }

    private void showComicInformation(Book book) {
        /*特殊信息*/
        System.out.println("画风：" + book.getPaintingStyle() + "   男主：" + book.getMainManRole() + "   女主" + book.getMainWomanRole());
    }

    /*还书操作*/
    private void rtnBook(Scanner scanner) throws IOException {

        System.out.println("请输入您要归还的书籍的名字：");
        String bookName = scanner.next();
        String bookCustomType = null;
        for (Book book : bookCase.getBooks()) {
            if (book.getBookName().equals(bookName)) {
                bookCustomType = book.getUserTypeSet();   //拿到书柜里的Key是用户设置的分类名
                /* 发送还书请求及信息包 */
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("bookName", bookName);
                hashMap.put("bookCustomType", bookCustomType);
                MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_USER_RETURN, JSON.toJSONString(hashMap));
                msgPacket.sendPacket();
                if (msgHandle.receiveAnswer().equals("true")) {
                    bookCase.getBooks().remove(book);
                    bookCase.getBookTypeListHashMap().get(bookCustomType).removeIf(book1 -> book1.getBookName().equals(bookName));
                    System.out.println(bookName + "还书成功！");
                    break;
                }
            }
            //抛出过ConcurrentModificationException
        }
        if (bookCustomType == null) {
            System.out.println("该书不存在！还书失败！");
            return;
        }



    }

    /*借入书籍操作*/
    private void brwBook(Scanner scanner) throws IOException {
        String choice;
        String bookType;
        System.out.println("请输入您要借阅的书籍的种类1/2/3：1.漫画书 2.菜谱 3.编程书：");
        choice = scanner.next();
        System.out.println("请输入您要借阅的书籍的名字：");
        String bookName = scanner.next();

        switch (choice) {
            case "1":
                bookType = BOOK_COMIC;
                if (brwFromLibrary(bookType, bookName)) System.out.println("借书成功！");
                else System.out.println("借书失败！该书籍不存在");
                break;
            case "2":
                bookType = BOOK_CUISINE;
                if (brwFromLibrary(bookType, bookName)) System.out.println("借书成功！");
                else System.out.println("借书失败！该书籍不存在");
                break;
            case "3":
                bookType = BOOK_PROGRAMMING;
                if (brwFromLibrary(bookType, bookName)) System.out.println("借书成功！");
                else System.out.println("借书失败！该书籍不存在");
                break;
            default:
                System.out.println("借书失败！该书籍不存在");
                break;
        }

    }

    private boolean brwFromLibrary(String bookType, String bookName) throws IOException {
        /* 发送借书请求和信息 */
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("bookType", bookType);
        hashMap.put("bookName", bookName);
        String jsonString = JSON.toJSONString(hashMap);
        MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_USER_BORROW, jsonString);
        msgPacket.sendPacket();
        String answer = msgHandle.receiveAnswer();
        if (!answer.equals("false")) {
            switch (bookType) {
                case BOOK_COMIC:
                    ComicBook comicBook = JSON.parseObject(answer, ComicBook.class);
                    classify(comicBook);
                    bookCase.addBook(comicBook);
                    break;
                case BOOK_CUISINE:
                    CuisineCookBook cuisineCookBook = JSON.parseObject(answer, CuisineCookBook.class);
                    classify(cuisineCookBook);
                    bookCase.addBook(cuisineCookBook);
                    break;
                case BOOK_PROGRAMMING:
                    ProgrammingBook programmingBook = JSON.parseObject(answer, ProgrammingBook.class);
                    classify(programmingBook);
                    bookCase.addBook(programmingBook);
                    break;
            }
            /*更新服务端保存的书柜*/
            renewServerBookcase();
            return true;
        } else return false;
    }

    private void classify(Book book) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("您要为该书籍分类吗？Y(y)/N(n)：Y.是 N.否");
            String choice = scanner.next();
            if (choice.equals("Y") || choice.equals("y"))
                classification(scanner, book);
            else if (choice.equals("N") || choice.equals("n"))
                book.setUserTypeSet("默认分类");
            else {
                System.out.println("错误的选择!");
                continue;
            }
            break;
        }

    }

    private void renewServerBookcase() throws IOException {
        HashMap<String, String> hashMap = new HashMap<>();
        String jsonBookcase = JSON.toJSONString(bookCase);
        hashMap.put("bookCase", jsonBookcase);
        String jsonString = JSON.toJSONString(hashMap);
        MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_BOOKCASE_ALTER, jsonString);
        msgPacket.sendPacket();
    }

    private void classification(Scanner scanner, Book book) {
        System.out.println("请输入您要将该书归入的分类名称：");
        String userTypeSet = scanner.next();
        /*判断该分类是否已经存在，不存在则创建新的分类*/
        if (!bookCase.getBookTypes().contains(userTypeSet)) {
            ArrayList<Book> books = new ArrayList<>();
            book.setUserTypeSet(userTypeSet);
            bookCase.getBookTypes().add(userTypeSet);
            bookCase.getBookTypeListHashMap().put(userTypeSet, books);
        } else {
            book.setUserTypeSet(userTypeSet);

        }
    }


}


