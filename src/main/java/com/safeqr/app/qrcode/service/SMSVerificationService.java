package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.InvalidFormatExceptions;
import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.entity.SMSEntity;
import com.safeqr.app.qrcode.repository.SMSRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.safeqr.app.constants.CommonConstants.CLASSIFY_SAFE;
import static com.safeqr.app.constants.CommonConstants.CLASSIFY_WARNING;

@Service
public class SMSVerificationService {
    private final SMSRepository smsRepository;
    private static final Logger logger = LoggerFactory.getLogger(SMSVerificationService.class);

    // Define phishing keywords categories
    private static final Map<String, List<String>> PHISHING_KEYWORDS_MAP = new HashMap<>();

    static {
        PHISHING_KEYWORDS_MAP.put("Generic", Arrays.asList("password", "verify", "urgent", "prize", "account update"));
        PHISHING_KEYWORDS_MAP.put("Tax Refund", Arrays.asList("tax refund", "claim your refund", "tax return"));
        PHISHING_KEYWORDS_MAP.put("Suspicious Activity", Arrays.asList("suspicious activity detected", "action required", "account compromised"));
        PHISHING_KEYWORDS_MAP.put("Social Media", Arrays.asList("social media account", "unauthorized login attempt", "verify your account"));
        PHISHING_KEYWORDS_MAP.put("Bogus Payment", Arrays.asList("payment confirmation", "transaction details", "payment receipt"));
        PHISHING_KEYWORDS_MAP.put("Incorrect Billing", Arrays.asList("incorrect billing information", "update billing details", "billing account"));
        PHISHING_KEYWORDS_MAP.put("iCloud", Arrays.asList("icloud account", "update your icloud", "icloud security alert"));
        PHISHING_KEYWORDS_MAP.put("HR Survey", Arrays.asList("human resources survey", "employee feedback", "survey participation"));
        PHISHING_KEYWORDS_MAP.put("Google Docs", Arrays.asList("google docs", "view shared document", "google drive"));
        PHISHING_KEYWORDS_MAP.put("USPS", Arrays.asList("usps delivery", "package tracking", "shipping details"));
        PHISHING_KEYWORDS_MAP.put("Voicemail", Arrays.asList("voicemail notification", "missed call", "listen to voicemail"));
        PHISHING_KEYWORDS_MAP.put("Bogus Invoice", Arrays.asList("invoice details", "view invoice", "payment invoice"));
        PHISHING_KEYWORDS_MAP.put("Email Upgrade", Arrays.asList("email account upgrade", "email settings update", "upgrade your email"));
        PHISHING_KEYWORDS_MAP.put("Dropbox", Arrays.asList("dropbox", "view shared file", "dropbox account"));
        PHISHING_KEYWORDS_MAP.put("CEO Phishing", Arrays.asList("ceo email", "urgent message from ceo", "ceo authorization"));
        PHISHING_KEYWORDS_MAP.put("Costco", Arrays.asList("costco", "costco membership", "costco rewards"));
        PHISHING_KEYWORDS_MAP.put("Bank", Arrays.asList("bank account", "unusual activity", "account login"));
        PHISHING_KEYWORDS_MAP.put("Fake App", Arrays.asList("app purchase", "app subscription", "confirm your purchase"));
        PHISHING_KEYWORDS_MAP.put("Advanced Fee", Arrays.asList("advance fee", "processing fee", "fee payment"));
        PHISHING_KEYWORDS_MAP.put("Account Suspension", Arrays.asList("account suspension", "suspend your account", "account deactivation"));
    }

    @Autowired
    public SMSVerificationService(SMSRepository smsRepository) {
        this.smsRepository = smsRepository;
    }

    public SMSEntity getSMSEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
        return smsRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("SMS not found for QR Code id: " + qrCodeId));
    }
    public void insertDB(SMSEntity smsEntity) {
        smsRepository.save(smsEntity);
    }

    public void parseSMSString(SMSEntity smsEntity, String smsto) throws IllegalArgumentException{
        // Validate the string format
        if (smsto == null || smsto.isEmpty()) {
            throw new InvalidFormatExceptions("sms cannot be null or empty.");
        }
        // Remove the "SMSTO:" prefix
        String data = smsto.substring(6);

        // Split the data into phone number and message
        String[] parts = data.split(":", 2);

        // If both phone number and message are available
        if (parts.length == 2) {
            String phone = parts[0];
            String message = parts[1];

            // Populate the SMSEntity object
            smsEntity.setPhone(phone);
            smsEntity.setMessage(message);
        } else {
            // Handle the case where the format is invalid
            throw new InvalidFormatExceptions("Invalid SMSTO format. Expected format: SMSTO:<phone>:<message>");
        }
    }
    @Transactional
    public String getClassification (SMSEntity smsEntity) {

        String lowerCaseSms = smsEntity.getMessage().toLowerCase();
        logger.info("Sms: {}", lowerCaseSms);

        // Iterate over the map of phishing keywords
        for (Map.Entry<String, List<String>> entry : PHISHING_KEYWORDS_MAP.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            // Check if the SMS contains any of the phishing keywords
            for (String keyword : keywords) {
                if (lowerCaseSms.contains(keyword)) {
                    logger.info("Phishing keyword detected: {}", keyword);
                    smsEntity.setKeywordDetected("Potential Phishing - " + category);
                    return CLASSIFY_WARNING;
                }
            }
        }

        // If no phishing keywords are found
        return CLASSIFY_SAFE;
    }

}