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

    public static final String INFO_NON_SECURE_CONNECTION = "Not an HTTPS connection";
    public static final String INFO_NO_HSTS_HEADER = "No HSTS Header detected";
    public static final String INFO_HSTS_HEADER_PREFIX = "HSTS Header: ";
    public static final String INFO_HSTS_NOT_APPLICABLE = "N/A";

    public static final String CLASSIFY_SAFE = "SAFE";
    public static final String CLASSIFY_WARNING = "WARNING";
    public static final String CLASSIFY_UNSAFE = "UNSAFE";
    public static final String CLASSIFY_UNKNOWN = "UNKNOWN";

    public static final String CAT_BENIGN = "Benign";
    public static final String CAT_DEFACEMENT = "Defacement";
    public static final String CAT_MALWARE = "Malware";
    public static final String CAT_PHISHING = "Phishing";

    public static final Integer GMAIL_ACTIVE = 1;
}
