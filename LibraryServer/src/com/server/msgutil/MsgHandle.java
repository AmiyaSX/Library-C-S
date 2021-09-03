package com.server.msgutil;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.book.*;
import com.server.library.BookCase;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import static com.server.book.BookType.*;
import static com.server.library.Server.*;
import static com.server.msgutil.MsgConfig.*;

public class MsgHandle implements MsgHandleFunction {

    @Override
    public synchronized MsgPacket msgEncode(char magic, byte cmdMsg, String body) {
        return new MsgPacket(magic, cmdMsg, body);
    }

    @Override
    public synchronized String msgDecode(MsgPacket msgPacket, DataOutputStream dataOutputStream) throws IOException {
        String msg = " ";
        String userId;
        String bookName;
        String bookType;
        BookCase bookCase;
        if (msgPacket.getMagic() != MsgConfig.MAGIC) return null;
        JSONObject jsonObject = JSON.parseObject(msgPacket.getJsonMsg());

        switch (msgPacket.getMsgType()) {
            case MSG_SIGN_IN:
                /* check id & pwd */
                String id = jsonObject.getString("id");
                String pwd = jsonObject.getString("pwd");
                if (library.getUsers().containsKey(id) && pwd.equals(library.getUsers().get(id))) {
                    HashMap<String, String> map = new HashMap<>();
                    bookCase = library.getBookCases().get(id);
                    String userString = JSON.toJSONString(bookCase);
                    map.put("state", "true");
                    map.put("bookCase", userString);
                    String s = JSON.toJSONString(map);
                    MsgPacket msgAnswerPacket = this.msgEncode(MAGIC, MSG_SIGN_IN, s);
                    this.sendPacket(msgAnswerPacket,dataOutputStream);
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("state", "false");
                    String s = JSON.toJSONString(map);
                    MsgPacket msgAnswerPacket = this.msgEncode(MAGIC, MSG_SIGN_IN, s);
                    this.sendPacket(msgAnswerPacket,dataOutputStream);
                }
                break;
            case MSG_REGISTER:
                /* create user */
                String registerId = jsonObject.getString("id");
                String registerPwd = jsonObject.getString("pwd");
                library.getUsers().put(registerId, registerPwd);
                library.getBookCases().put(registerId,new BookCase(registerId));
                break;
            case MSG_CHECK:
                /* check exist id */
                if (library.getUsers().containsKey(jsonObject.getString("id"))) {
                    sendStateMsg(STATE_NEGATIVE,dataOutputStream);
                } else {
                    sendStateMsg(STATE_POSITIVE,dataOutputStream);
                }
                break;
            case MSG_BOOKCASE_ALTER:
                /* renew bookcase */
                String jsonBookcase = jsonObject.getString("bookCase");
                BookCase newBookCase = JSON.parseObject(jsonBookcase, BookCase.class);
                BookCase oldBookCase = library.getBookCases().get(newBookCase.getUserId());
                library.getBookCases().replace(newBookCase.getUserId(),oldBookCase,newBookCase);
                break;
            case MSG_USER_BORROW:
                /* borrow from library */
                bookType = jsonObject.getString("bookType");
                bookName = jsonObject.getString("bookName");
                boolean b = true;
                if(library.getBookTypes().contains(bookType)) {
                    for (Book book : library.getBookTypeListHashMap().get(bookType)) {
                        if (book.getBookName().equals(bookName)) {
                            b = false;
                            HashMap<String,String> brwInfo = new HashMap<>();
                            brwInfo.put("state","true");
                            String jsonString = JSON.toJSONString(book);
                            brwInfo.put("Book",jsonString);
                            String jsonBody = JSON.toJSONString(brwInfo);
                            MsgPacket answerPacket = msgEncode(MAGIC,MSG_USER_BORROW,jsonBody);
                            msgHandle.sendPacket(answerPacket,dataOutputStream);
                            library.getBookTypeListHashMap().get(bookType).remove(book);
                            break;
                        }
                    }
                }
                if (b) {    /* 馆内没有要借的书 */
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("state","false");
                    String jsonString = JSON.toJSONString(hashMap);
                    MsgPacket answerPacket = msgEncode(MAGIC,MSG_USER_BORROW,jsonString);
                    msgHandle.sendPacket(answerPacket,dataOutputStream);
                }
                break;
            case MSG_USER_RETURN:
                /* return book */
                userId = jsonObject.getString("id");
                bookName = jsonObject.getString("bookName");
                String bookCustomType = jsonObject.getString("bookCustomType");
                bookCase = library.getBookCases().get(userId);
                for (Book aimBook : bookCase.getBooks()) {
                    if (aimBook.getBookName().equals(bookName) && aimBook.getUserTypeSet().equals(bookCustomType)) {
                        sendStateMsg(STATE_POSITIVE,dataOutputStream);
                        library.addBook(aimBook);
                        bookCase.getBooks().remove(aimBook);
                        bookCase.getBookTypeListHashMap().get(bookCustomType).removeIf(book -> book.getBookName().equals(bookName));
                        return msg;
                    }
                }
                sendStateMsg(STATE_NEGATIVE,dataOutputStream);
                break;
            case MSG_MANAGER_ADD:
                /* add book to library */
                bookType = jsonObject.getString("bookType");
                String jsonBook = jsonObject.getString("Book");
                switch (bookType) {
                    case BOOK_COMIC:
                        ComicBook comicBook = JSON.parseObject(jsonBook, ComicBook.class);
                        library.addBook(comicBook);
                        break;
                    case BOOK_CUISINE:
                        CuisineCookBook cuisineCookBook = JSON.parseObject(jsonBook, CuisineCookBook.class);
                        library.addBook(cuisineCookBook);
                        break;
                    case BOOK_PROGRAMMING:
                        ProgrammingBook programmingBook = JSON.parseObject(jsonBook, ProgrammingBook.class);
                        library.addBook(programmingBook);
                        break;
                    default:
                        System.out.println("上架失败");
                        break;
                }

                break;
            case MSG_MANAGER_OFF:
                /* del book from library */
                bookName = jsonObject.getString("bookName");
                bookType = jsonObject.getString("bookType");
                if (library.delBook(bookName, bookType)) {
                    sendStateMsg(STATE_POSITIVE,dataOutputStream);
                    System.out.println(bookName + "下架成功");
                } else {
                    sendStateMsg(STATE_NEGATIVE,dataOutputStream);
                    System.out.println(bookName + "下架失败");
                }
                break;
            case EXIT:
                System.out.println("第" + connection + "个客户端已经断开");
                connection --;
//                if (connection == 0) {
//                    new Socket("1.15.72.112",PORT);
//                    connection--;
//                }
                return"exit";
        }
        return msg;

    }

    public synchronized void sendPacket(MsgPacket msgPacket, DataOutputStream dataOutputStream) throws IOException {
        String msg = JSON.toJSONString(msgPacket);
        dataOutputStream.writeUTF(msg);
    }
    public synchronized String receivePacket(MsgPacket msgPacket, DataOutputStream dataOutputStream) throws IOException {
        return msgDecode(msgPacket, dataOutputStream);
    }

    private synchronized void sendStateMsg(int state,DataOutputStream dataOutputStream) throws IOException {
        HashMap<String, String> stateMap = new HashMap<>();
        MsgPacket msgPacket;
        switch (state) {
            case STATE_POSITIVE:
                stateMap.put("state", "true");
                msgPacket = msgHandle.msgEncode(MAGIC, STATE_POSITIVE, JSON.toJSONString(stateMap));
                msgHandle.sendPacket(msgPacket, dataOutputStream);
                break;
            case STATE_NEGATIVE:
                stateMap.put("state", "false");
                msgPacket = msgHandle.msgEncode(MAGIC, STATE_NEGATIVE, JSON.toJSONString(stateMap));
                msgHandle.sendPacket(msgPacket, dataOutputStream);
                break;
        }

    }

}







