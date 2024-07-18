package com.safeqr.app.constants;

public class CommonConstants {
    private CommonConstants() {
        //private constructor to prevent instantiation
    }
    public static final String HEADER_USER_ID = "X-USER-ID";
    public static final String DEFAULT_QR_CODE_TYPE = "TEXT";
    public static final int MAX_REDIRECT_COUNT = 20;
    public static final String QR_CODE_TYPE_EMAIL = "EMAIL";
    public static final String QR_CODE_TYPE_PHONE = "PHONE";
    public static final String QR_CODE_TYPE_SMS = "SMS";
    public static final String QR_CODE_TYPE_URL = "URL";
    public static final String QR_CODE_TYPE_WIFI = "WIFI";
}
