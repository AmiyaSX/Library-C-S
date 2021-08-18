package com.client.library;

import com.alibaba.fastjson.JSON;
import com.client.book.*;
import com.client.msgutil.MsgConfig;
import com.client.msgutil.MsgPacket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Manager {

    public static void manageBooks(Scanner scanner) throws IOException {
        String choice;
        do {
            System.out.println("请选择您要进行的操作0/1/2：0.返回 1.上架图书 2.下架图书");
            choice = scanner.next();
            switch (choice) {
                case "0":
                    System.out.println("返回主菜单！");
                    break;
                case "1":
                    bookAdd(scanner);
                    break;
                case "2":
                    bookDel(scanner);
                    break;
                default:
                    System.out.println("错误的选择！");
                    break;
            }
        } while (!choice.equals("0"));

    }

    private static void bookAdd(Scanner scanner) throws IOException {
        String choice;
        System.out.println("请输入上架书籍的信息!");
        System.out.println("请选择上架图书的种类1/2/3：1.漫画书 2.编程书 3.菜谱");
        choice = scanner.next();
        switch (choice) {
            case "1":
                ComicBook book1 = new ComicBook();
                initGeneralInformation(book1, scanner);
                comicInit(book1, scanner);
                sendRequest(BookType.BOOK_COMIC, book1);
                break;
            case "2":
                ProgrammingBook book2 = new ProgrammingBook();
                initGeneralInformation(book2, scanner);
                programmingInit(book2, scanner);
                sendRequest(BookType.BOOK_PROGRAMMING, book2);
                break;
            case "3":
                CuisineCookBook book = new CuisineCookBook();
                initGeneralInformation(book, scanner);
                cuisineInit(book, scanner);
                sendRequest(BookType.BOOK_CUISINE, book);
                break;
            default:
                System.out.println("错误的选择！");
                break;
        }
    }


    private static void cuisineInit(Book book, Scanner scanner) {
        book.setType(BookType.BOOK_CUISINE);
        System.out.print("请输入图书的所属菜系：");
        book.setCuisineStyle(scanner.next());
        System.out.print("请输入图书的菜品总数：");
        book.setCuisineNum(scanner.next());
        System.out.print("请输入上架图书的书名：");
        book.setBookName(scanner.next());
    }

    private static void programmingInit(Book book, Scanner scanner) {
        book.setType(BookType.BOOK_PROGRAMMING);
        System.out.print("请输入图书的编程语言类型：");
        book.setLanguage(scanner.next());
        System.out.print("请输入图书作者的博客链接：");
        book.setAuthorUrl(scanner.next());
        System.out.print("请输入上架图书的书名：");
        book.setBookName(scanner.next());
    }

    private static void comicInit(Book book, Scanner scanner) {
        book.setType(BookType.BOOK_COMIC);
        System.out.print("请输入图书的绘画风格：");
        book.setPaintingStyle(scanner.next());
        System.out.print("请输入图书的男主角：");
        book.setMainManRole(scanner.next());
        System.out.print("请输入图书的女主角：");
        book.setMainWomanRole(scanner.next());
        System.out.print("请输入上架图书的书名：");
        book.setBookName(scanner.next());
    }

    private static void initGeneralInformation(Book book, Scanner scanner) {
        System.out.print("请输入图书的页数：");
        book.setPages(scanner.next());
        System.out.print("请输入图书的定价：");
        book.setPrice(scanner.next());
    }

    private static void bookDel(Scanner scanner) throws IOException {
        System.out.print("请输入您要下架的书籍的种类名称(comic/cuisine/programming)：");
        String delBookType = scanner.next();
        System.out.print("请输入您要下架的书籍的名称：");
        String delBookName = scanner.next();
        HashMap<String, String> map = new HashMap<>();
        map.put("bookName", delBookName);
        map.put("bookType", delBookType);
        String jsonString = JSON.toJSONString(map);
        MsgPacket msgPacket = Client.msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_MANAGER_OFF, jsonString);
        msgPacket.sendPacket();
        String answer = Client.msgHandle.receiveAnswer();
        if (answer.equals("true")) System.out.println(delBookName + "下架成功");
        else System.out.println(delBookName + "下架失败");

    }

    private static void sendRequest(String type, Book book) throws IOException {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("bookType", type);
        switch (type) {
            case BookType.BOOK_COMIC:
                ComicBook book1 = (ComicBook) book;
                hashMap.put("Book", JSON.toJSONString(book1));
                break;
            case BookType.BOOK_PROGRAMMING:
                ProgrammingBook book2 = (ProgrammingBook) book;
                hashMap.put("Book", JSON.toJSONString(book2));
                break;
            case BookType.BOOK_CUISINE:
                CuisineCookBook book3 = (CuisineCookBook) book;
                hashMap.put("Book", JSON.toJSONString(book3));
                break;
        }
        MsgPacket msgPacket = Client.msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_MANAGER_ADD, JSON.toJSONString(hashMap));
        msgPacket.sendPacket();

    }

}
