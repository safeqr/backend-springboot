package com.safeqr.app.qrcode.service;

import com.safeqr.app.exceptions.InvalidFormatExceptions;
import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.entity.PhoneEntity;
import com.safeqr.app.qrcode.repository.PhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.safeqr.app.constants.CommonConstants.*;

@Service
public class PhoneVerificationService {
    private final PhoneRepository phoneRepository;
    private static final Logger logger = LoggerFactory.getLogger(PhoneVerificationService.class);

    @Autowired
    public PhoneVerificationService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }
    public PhoneEntity getPhoneEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
        return phoneRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("Phone not found for QR Code id: " + qrCodeId));
    }
    public void insertDB(PhoneEntity phoneEntity) {
        phoneRepository.save(phoneEntity);
    }

    public void parsePhoneString(PhoneEntity phoneEntity, String phoneString) {
        // Validate the string format
        if (phoneString == null || phoneString.isEmpty()) {
            throw new InvalidFormatExceptions("Phone string cannot be null or empty.");
        }

        // Remove the "TEL:" prefix
        String phoneNumber = phoneString.substring(4);

        // Further validation for phone number can be done here (optional)
        if (phoneNumber.matches("\\+?[0-9]*")) {
            // Populate the PhoneEntity object
            phoneEntity.setPhone(phoneNumber);
        } else {
            throw new InvalidFormatExceptions("Invalid phone number format.");
        }

    }

    public String checkPhoneNumber(PhoneEntity phoneEntity) {
        // Remove any spaces, dashes, parentheses, and trim the ends
        String phoneNumber = phoneEntity.getPhone().replaceAll("[\\s\\-()]", "").trim();

        // Check if the number starts with +65 or just 65
        if (phoneNumber.startsWith("+65")) {
            phoneNumber = phoneNumber.substring(3);  // Remove the "+65"
        } else if (phoneNumber.startsWith("65")) {
            phoneNumber = phoneNumber.substring(2);  // Remove the "65"
        }

        // Check if it's a valid Singapore mobile or landline number
        if (phoneNumber.matches("^[689]\\d{7}$")) {
            if (phoneNumber.startsWith("8") || phoneNumber.startsWith("9")) {
                phoneEntity.setRemarks("Singapore mobile number - This number has not been scanned for scam. Please do not divulge your personal information.");
            } else if (phoneNumber.startsWith("6")) {
                phoneEntity.setRemarks("Singapore landline number - This phone number has not been scanned for scam. Please do not divulge your personal information.");
            }
            return CLASSIFY_UNKNOWN;
        }

        // If it doesn't match mobile or landline pattern
        phoneEntity.setRemarks("Warning: This is either an overseas number or an invalid Singapore number. Please exercise caution.");
        return CLASSIFY_WARNING;
    }

}