package com.safeqr.app.constants;

public class APIConstants {
    private APIConstants() {
        //private constructor to prevent instantiation
    }
    public static final String API_VERSION = "v1";
    public static final String API_URL_QRCODE_GET_ALL = "/qrcodetypes";
    public static final String API_URL_QRCODE_SCAN = "/qrcodetypes/scan";
    public static final String API_URL_QRCODE_DETECT = "/qrcodetypes/detect";
    public static final String API_URL_QRCODE_VERIFY_URL = "/qrcodetypes/verifyurl";
    public static final String API_URL_QRCODE_VIRUS_TOTAL_CHECK = "/qrcodetypes/virustotalcheck";
    public static final String API_URL_QRCODE_REDIRECT_COUNT = "/qrcodetypes/redirectcount";
    public static final String API_URL_QRCODE_GET_SCANNED_DETAILS = "/qrcodetypes/getScannedDetails";

}
