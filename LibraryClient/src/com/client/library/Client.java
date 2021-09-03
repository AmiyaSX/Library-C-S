package com.client.library;

import com.alibaba.fastjson.JSON;
import com.client.msgutil.MsgConfig;
import com.client.msgutil.MsgPacket;
import com.client.msgutil.MsgHandle;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
    public static MsgHandle msgHandle = new MsgHandle();
    static Socket socket = null;
    static OutputStream outputStream = null;
    static InputStream inputStream = null;
    static Boolean connect = false;
    static Scanner scanner = new Scanner(System.in);
    public static DataInputStream dataInputStream = null;
    public static DataOutputStream dataOutputStream = null;

    public interface MsgHandleFunction {

        MsgPacket msgEncode(char magic, byte cmdMsg, String body);

        String msgDecode(MsgPacket msgPacket);
    }

    public static void main(String[] args) throws IOException {
        socket = new Socket("1.15.72.112", 22);
        connect = true;
        System.out.println("与服务器1.15.72.112:22连接成功");
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
        Entry entry = new Entry();  //初始化登陆系统

        String choice;
        do {
            System.out.println("欢迎使用图书系统，请选择您要进行的操作exit/1/2/3: exit.退出 1.登录 2.注册 3.我是管理员");
            choice = scanner.next();
            if (choice.equals("1")) {
                entry.userSignIn();
            } else if (choice.equals("2"))
                entry.userRegister();
            else if (choice.equals("3"))
                /*这里可以增加对User和Manager的权限判断*/
                Manager.manageBooks(scanner);
            else if (!choice.equals("exit")) System.out.println("错误的选择");
            if (choice.equals("exit")) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("cmd", choice);
                MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.EXIT, JSON.toJSONString(hashMap));
                msgPacket.sendPacket();
                release();
            }
        } while (!choice.equals("exit"));

        System.out.println("谢谢使用，再见");
    }

    private static void release() {
        try {
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
