package com.safeqr.app.constants;

public class APIConstants {
    private APIConstants() {
        //private constructor to prevent instantiation
    }
    public static final String APPLICATION_NAME = "SafeQR";
    public static final String API_VERSION = "v1";
    public static final String API_URL_QRCODE_GET_ALL = "/qrcodetypes";
    public static final String API_URL_QRCODE_SCAN = "/qrcodetypes/scan";
    public static final String API_URL_QRCODE_DETECT = "/qrcodetypes/detect";
    public static final String API_URL_QRCODE_VERIFY_URL = "/qrcodetypes/verifyURL";
    public static final String API_URL_QRCODE_VIRUS_TOTAL_CHECK = "/qrcodetypes/virusTotalCheck";
    public static final String API_URL_QRCODE_REDIRECT_COUNT = "/qrcodetypes/checkRedirects";
    public static final String API_URL_QRCODE_GET_QR_DETAILS = "/qrcodetypes/getQRDetails";
    public static final String PREDICTION_API_URL = "http://localhost:8000/predict";


    public static final String API_URL_USER_GET = "/user/getUser";
    public static final String API_URL_USER_GET_SCANNED_HISTORIES = "/user/getScannedHistories";
    public static final String API_URL_USER_DELETE_SCANNED_HISTORIES = "/user/deleteScannedHistories";
    public static final String API_URL_USER_DELETE_ALL_SCANNED_HISTORIES = "/user/deleteAllScannedHistories";
    public static final String API_URL_USER_GET_BOOKMARKS = "/user/getBookmarks";
    public static final String API_URL_USER_SET_BOOKMARK = "/user/setBookmark";
    public static final String API_URL_USER_DELETE_BOOKMARK = "/user/deleteBookmark";
    public static final String API_URL_USER_DELETE_ALL_BOOKMARK = "/user/deleteAllBookmark";
    public static final String API_URL_GMAIL_GET_EMAILS = "/gmail/getEmails";
    public static final String API_URL_GMAIL_GET_SCANNED_EMAILS = "/gmail/getScannedEmails";



}
