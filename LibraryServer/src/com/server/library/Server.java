package com.server.library;

import com.alibaba.fastjson.JSONObject;
import com.server.msgutil.MsgHandle;
import com.server.msgutil.MsgPacket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//采用socket TCP 进行通信，服务端采用多线程处理方式。
public class Server {

    public static Library library;
    public static MsgHandle msgHandle;
    public static int connection = 0;
    public static int PORT = 9999;
    public static ServerSocket serverSocket;

    public interface MsgHandleFunction {
        MsgPacket msgEncode(char magic, byte cmdMsg, String body);

        String msgDecode(MsgPacket msgPacket, DataOutputStream dataOutputStream) throws IOException;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectInputStream oisLibrary;
        serverSocket = new ServerSocket(PORT);

        File file = new File("Library.data");
        if (!file.exists()) {
            /* 第一次初始化 */
            System.out.println("第一次初始化，请先上架图书！");
            new ObjectOutputStream(new FileOutputStream("Library.data"));
            library = new Library();
        } else {
            oisLibrary = new ObjectInputStream(new FileInputStream("Library.data"));
            try {
                library = (Library) oisLibrary.readObject();
            } catch (EOFException e) {
                new ObjectOutputStream(new FileOutputStream("Library.data"));
                library = new Library();
            }
        }

        msgHandle = new MsgHandle();
        System.out.println("正在尝试连接客户端，允许多个客户端同时连接");
        do {
            Socket socket = serverSocket.accept();
            connection++;
            if (connection == 0) break;
            System.out.println("第" + connection + "个客户端 " + socket.getRemoteSocketAddress() + " 连接成功！");
            Thread thread = new Thread(new ThreadTask(socket));
            thread.start();
        } while (connection != 0);
        /*序列化保存数据library系统*/
        ObjectOutputStream opsLibrary = new ObjectOutputStream(new FileOutputStream("Library.data"));
        opsLibrary.writeObject(library);
        System.out.println("连接已经断开");

    }

    private static class ThreadTask implements Runnable {
        Socket socket;

        public ThreadTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String msg = " ";
            OutputStream outputStream = null;
            InputStream inputStream = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
                dataOutputStream = new DataOutputStream(outputStream);
                /*监听客户端消息*/
                while (!msg.equals("exit") && socket != null) {
                    msg = dataInputStream.readUTF();
                    MsgPacket msgPacket = JSONObject.parseObject(msg, MsgPacket.class);
                    /* 接收包 分解业务逻辑 */
                    msg = msgHandle.receivePacket(msgPacket, dataOutputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert dataOutputStream != null;
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    dataInputStream.close();
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
    }

}
