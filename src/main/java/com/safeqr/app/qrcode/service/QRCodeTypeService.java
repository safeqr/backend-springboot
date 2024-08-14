
package com.safeqr.app.qrcode.service;

import static com.safeqr.app.constants.CommonConstants.*;

import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.dto.request.QRCodePayload;
import com.safeqr.app.qrcode.dto.response.BaseScanResponse;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.entity.QRCodeTypeEntity;
import com.safeqr.app.qrcode.entity.ScanHistoryEntity;
import com.safeqr.app.qrcode.model.factory.QRCodeFactoryProvider;
import com.safeqr.app.qrcode.model.QRCodeModel;
import com.safeqr.app.qrcode.repository.QRCodeRepository;
import com.safeqr.app.qrcode.repository.QRCodeTypeRepository;
import com.safeqr.app.qrcode.repository.ScanHistoryRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QRCodeTypeService {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeTypeService.class);

    private final QRCodeFactoryProvider qrCodeFactoryProvider;
    private final QRCodeTypeRepository qrCodeTypeRepository;
    private final ScanHistoryRepository scanHistoryRepository;
    private final QRCodeRepository qrCodeRepository;
    private final SafeBrowsingService safeBrowsingService;
    @Autowired
    public QRCodeTypeService(QRCodeFactoryProvider qrCodeFactoryProvider,
                             QRCodeTypeRepository qrCodeTypeRepository,
                             ScanHistoryRepository scanHistoryRepository,
                             QRCodeRepository qrCodeRepository,
                             SafeBrowsingService safeBrowsingService
                             ) {
        this.qrCodeFactoryProvider = qrCodeFactoryProvider;
        this.qrCodeTypeRepository = qrCodeTypeRepository;
        this.scanHistoryRepository = scanHistoryRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.safeBrowsingService = safeBrowsingService;
    }
    private List<QRCodeTypeEntity> configs;
    private QRCodeTypeEntity defaultQRCodeTypeEntity;

    @PostConstruct
    public void loadQRCodeTypes() {
        // Fetch all QR Code Types from the database
        configs = qrCodeTypeRepository.findAll();
        // Set the default QR Code Type
        defaultQRCodeTypeEntity = configs.stream()
                .filter(config -> config.getType().equals(DEFAULT_QR_CODE_TYPE))
                .findFirst()
                .orElse(null);
    }

    public List<QRCodeTypeEntity> getAllTypes() {
        return configs;
    }
    // Get scanned qrcode details
    public BaseScanResponse getScannedQRCodeDetails(UUID qrCodeId){
        return BaseScanResponse.builder().qrcode(getScannedQRCodeDetailsInModel(qrCodeId)).build();
    }

    public QRCodeModel<?> getScannedQRCodeDetailsInModel(UUID qrCodeId){
        // Find scanned qr code in qr code table
        QRCodeEntity qrCodeEntity = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("QR Code not found with id: " + qrCodeId));
        logger.info("qrCodeEntity: {}", qrCodeEntity);
        QRCodeModel<?> qrCodeModel = qrCodeFactoryProvider.createQRCodeInstance(qrCodeEntity);
        logger.info("Retrieved details: {}", qrCodeModel.getDetails());

        return qrCodeModel;
    }

    // Process Scanned QR Code
    @Transactional
    public BaseScanResponse scanQRCode(String userId, QRCodePayload payload) {
        String data = payload.getData();
        logger.info("scanQRCode: userId={}, data={}", userId, data);

        QRCodeModel<?> qrCodeModel = scanAndClassify(userId, data);
        UUID qrId = qrCodeModel.getData().getId();

        // Insert into Scan History table if userId is not null
        logger.info("scanQRCode: scannedQR new ID={}", qrId);
        if (userId != null) {
            scanHistoryRepository.save(ScanHistoryEntity.builder()
                    .qrCodeId(qrId)
                    .userId(userId)
                    .scanStatus(ScanHistoryEntity.ScanStatus.ACTIVE)
                    .build());
        }

        return BaseScanResponse.builder().qrcode(qrCodeModel).build();
    }

    // Scan decoded contents from email message
    @Transactional
    public QRCodeModel<?> scanGmailDecodedContents(String userId, String data) {
        logger.info("Scan Gmail content: userId={}, data={}", userId, data);

        return scanAndClassify(userId, data);
    }

    // ScanAndClassify

    private QRCodeModel<?> scanAndClassify(String userId, String data) {
        // Get the QR Code Type
        QRCodeTypeEntity qrType = getQRCodeType(data);

        // Insert the QR Code into main qrcode table
        QRCodeEntity scannedQR = qrCodeRepository.save(QRCodeEntity.builder()
                .userId(userId)
                .contents(data)
                .info(qrType)
                .createdAt(LocalDateTime.now())
                .build());

        // Create the QR Code Instance based on the QR Code Type & insert into the respective table
        QRCodeModel<?> qrCodeModel = qrCodeFactoryProvider.createQRCodeInstance(scannedQR);
        qrCodeModel.setDetails();

        // Get classifications based on verifications
        scannedQR.setResult(qrCodeModel.retrieveClassification());

        return qrCodeModel;
    }

    // Returns Default type as text if it does not fit into any of the category
    private QRCodeTypeEntity getQRCodeType(String data) {
        return configs.stream()
                .filter(config -> data.toLowerCase().startsWith(config.getPrefix().toLowerCase()))
                .findFirst()
                .orElse(defaultQRCodeTypeEntity);
    }
}
