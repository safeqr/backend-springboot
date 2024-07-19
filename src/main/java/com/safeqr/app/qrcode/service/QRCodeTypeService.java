
package com.safeqr.app.qrcode.service;

import static com.safeqr.app.constants.CommonConstants.*;

import com.safeqr.app.exceptions.CustomNotFoundExceptions;
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
        // Find scanned qr code in qr code table
        QRCodeEntity qrCodeEntity = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new CustomNotFoundExceptions("QR Code not found with id: " + qrCodeId));
        logger.info("qrCodeEntity: {}", qrCodeEntity);
        QRCodeModel<?> qrCodeModel = qrCodeFactoryProvider.createQRCodeInstance(qrCodeEntity);
        logger.info("Retrieved details: {}", qrCodeModel.getDetails());
        return BaseScanResponse.builder().qrcode(qrCodeModel).build();
    }

    // Process Scanned QR Code
    public BaseScanResponse scanQRCode(String userId, QRCodePayload payload) {
        String data = payload.getData();
        logger.info("scanQRCode: userId={}, data={}", userId, data);

        // Get the QR Code Type
        QRCodeTypeEntity qrType = getQRCodeType(data);

        // Insert the QR Code into main qrcode table
        QRCodeEntity scannedQR = qrCodeRepository.save(QRCodeEntity.builder()
                .userId(userId)
                .contents(data)
                .info(qrType)
                .createdAt(LocalDateTime.now())
                .build());

        // Insert into Scan History table if userId is not null
        logger.info("scanQRCode: scannedQR new ID={}", scannedQR.getId());
        if (userId != null) {
            scanHistoryRepository.save(ScanHistoryEntity.builder()
                    .qrCodeId(scannedQR.getId())
                    .userId(userId)
                    .scanStatus(ScanHistoryEntity.ScanStatus.ACTIVE)
                    .build());
        }
        // Create the QR Code Instance based on the QR Code Type & insert into the respective table
        QRCodeModel<?> qrCodeModel = qrCodeFactoryProvider.createQRCodeInstance(scannedQR);
        qrCodeModel.setDetails();

        return BaseScanResponse.builder().qrcode(qrCodeModel).build();
    }
    // Returns Default type as text if it does not fit into any of the category
    private QRCodeTypeEntity getQRCodeType(String data) {
        return configs.stream()
                .filter(config -> data.toLowerCase().startsWith(config.getPrefix().toLowerCase()))
                .findFirst()
                .orElse(defaultQRCodeTypeEntity);
    }

    public Mono<String> detectType(QRCodePayload payload) {
        String data = payload.getData();

        for (QRCodeTypeEntity config : configs) {
            if (data.startsWith(config.getPrefix())) {
                if ("URL".equals(config.getType())) {
                    try
                    {
                        return safeBrowsingService.isSafeUrl(data)
                                .map(isSafe -> isSafe ? "Safe URL" : "Unsafe URL");
                    } catch (NoSuchAlgorithmException e)
                    {
                        // TODO Auto-generated catch block
                        return Mono.just("Error checking URL safety: " + e.getMessage());
                    }
                }
                return Mono.just(config.getType());
            }
        }

        return Mono.just("Unknown");
    }
}
