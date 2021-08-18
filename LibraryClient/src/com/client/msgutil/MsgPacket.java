package com.client.msgutil;

import com.alibaba.fastjson.JSON;
import com.client.library.Client;

import java.io.IOException;

public class MsgPacket {

    char magic;
    byte MsgType;
    String jsonMsg;

    public MsgPacket(char magic, byte msgType, String jsonMsg) {
        this.magic = magic;
        MsgType = msgType;
        this.jsonMsg = jsonMsg;
    }

    public char getMagic() {
        return magic;
    }

    public void setMagic(char magic) {
        this.magic = magic;
    }

    public byte getMsgType() {
        return MsgType;
    }

    public void setMsgType(byte msgType) {
        MsgType = msgType;
    }

    public String getJsonMsg() {
        return jsonMsg;
    }

    public void setJsonMsg(String jsonMsg) {
        this.jsonMsg = jsonMsg;
    }

    public void sendPacket() throws IOException {
        String msg = JSON.toJSONString(MsgPacket.this);
        Client.dataOutputStream.writeUTF(msg);
    }
}
