package com.server.msgutil;

public class MsgPacket {

        private char magic;
        private byte MsgType;
        private String jsonMsg;

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



}
