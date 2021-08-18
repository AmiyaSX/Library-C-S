package com.client.msgutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.client.library.Client;

import java.io.IOException;

import static com.client.msgutil.MsgConfig.*;
import static com.client.msgutil.MsgConfig.MSG_SIGN_IN;
import static com.client.msgutil.MsgConfig.MSG_USER_BORROW;
import static com.client.msgutil.MsgConfig.STATE_NEGATIVE;
import static com.client.msgutil.MsgConfig.STATE_POSITIVE;

public class MsgHandle implements Client.MsgHandleFunction {

    /*打包*/
    @Override
    public MsgPacket msgEncode(char magic, byte cmdMsg, String body) {
        return new MsgPacket(magic, cmdMsg, body);
    }

    /*拆包*/
    @Override
    public String msgDecode(MsgPacket msgPacket) {

        if (msgPacket.magic != MAGIC) return null;
        JSONObject jsonObject = JSON.parseObject(msgPacket.getJsonMsg());

        switch (msgPacket.MsgType) {
            case MSG_SIGN_IN:
                String stateSignIn = jsonObject.getString("state");
                if (stateSignIn.equals("true")) {
                    return jsonObject.getString("bookCase");
                }else return stateSignIn;
            case MSG_USER_BORROW:
                String stateBorrow = jsonObject.getString("state");
                if (stateBorrow.equals("true")) {
                    return jsonObject.getString("Book");
                }else return stateBorrow;
            case STATE_POSITIVE:
                return "true";
            case STATE_NEGATIVE:
                return "false";
        }
        return null;
    }

    public String receiveAnswer() throws IOException {
        String msg, msgAnswer;
        msg = Client.dataInputStream.readUTF();
        MsgPacket msgPacket = JSONObject.parseObject(msg, MsgPacket.class);
        msgAnswer = msgDecode(msgPacket);
        return msgAnswer;
    }
}
