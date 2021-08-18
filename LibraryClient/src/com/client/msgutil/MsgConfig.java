package com.client.msgutil;

/**
 * 业务逻辑
 * */
public class MsgConfig {

    public static final char MAGIC = 0xabc;

    public static final byte MSG_SIGN_IN = 1;

    public static final byte MSG_REGISTER = 2;

    public static final byte MSG_CHECK = 3;

    public static final byte MSG_BOOKCASE_ALTER = 4;

    public static final byte MSG_USER_BORROW = 5;

    public static final byte MSG_USER_RETURN = 6;

    public static final byte MSG_MANAGER_ADD = 7;

    public static final byte MSG_MANAGER_OFF = 8;

    public static final byte STATE_POSITIVE = 9;

    public static final byte STATE_NEGATIVE = 10;

    public static final byte EXIT = 11;

}
