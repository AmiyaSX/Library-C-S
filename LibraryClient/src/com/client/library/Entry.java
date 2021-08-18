package com.client.library;

import com.alibaba.fastjson.JSON;
import com.client.msgutil.MsgConfig;
import com.client.msgutil.MsgPacket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.client.library.Client.msgHandle;

public final class Entry {
    String id, passWord, checkPassWord;
    private final Scanner scanner;

    public Entry() {
        scanner = new Scanner(System.in);
    }

    void userSignIn() throws IOException {

        do {
            System.out.println("您正在进行登录操作，请输入您的账号和密码：");
            System.out.print("账号：");
            id = scanner.next();
            System.out.print("密码：");
            passWord = scanner.next();

            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("pwd", passWord);
            String jsonString = JSON.toJSONString(map);

            /*登录验证请求*/
            MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_SIGN_IN, jsonString);
            msgPacket.sendPacket();
            String msgAnswer = msgHandle.receiveAnswer();
            if (msgAnswer.equals("false")) {
                System.out.println("账号或密码错误！请重新操作1/2: 1.重新登录 2.注册 ");
                String choice = scanner.next();
                if (choice.equals("1")) {
                    userSignIn();
                    return;
                } else if (choice.equals("2")) {
                    userRegister();
                    return;
                }
            } else {
                BookCase bookCase = initUser(msgAnswer);
                setUser(id, bookCase);
                break;
            }
        } while (true);
    }

    private BookCase initUser(String msgAnswer) {
        BookCase bookCase = JSON.parseObject(msgAnswer, BookCase.class); /* 子类数据丢失处 */
        System.out.println("登陆成功！");
        return bookCase;
    }

    void userRegister() throws IOException {
        System.out.println("您正在进行注册操作，请输入您的账号和密码：");
        /*设置账号*/
        do {
            System.out.print("账号：");
            id = scanner.next();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", id);
            MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_CHECK, JSON.toJSONString(hashMap));
            msgPacket.sendPacket();
            String answer = msgHandle.receiveAnswer();
            if (answer.equals("false")) System.out.println("该账号已存在，请重新设置账号！");
            else if (answer.equals("true")) break;
        } while (true);
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        /*设置密码*/
        do {
            System.out.print("密码：");
            passWord = scanner.next();
            System.out.print("请再次输入密码：");
            checkPassWord = scanner.next();
            if (!passWord.equals(checkPassWord)) {
                System.out.println("两次密码输入不相符，请重新设置密码! ");
                continue;
            }
            map.put("pwd", passWord);
            String string = JSON.toJSONString(map);
            MsgPacket msgPacket = msgHandle.msgEncode(MsgConfig.MAGIC, MsgConfig.MSG_REGISTER, string);
            msgPacket.sendPacket();
            break;
        } while (true);
        System.out.println("注册成功！请返回登录。");
    }

    //完成登陆操作并设置当前用户系统
    private void setUser(String id, BookCase bookCase) throws IOException {
        UserSystem userSystem = new UserSystem(id, bookCase);   //每个用户对应一个 个人的系统管理
        userSystem.userAction();    //进入个人系统
    }


}
