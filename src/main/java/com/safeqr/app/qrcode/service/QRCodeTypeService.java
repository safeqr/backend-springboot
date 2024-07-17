
package com.safeqr.app.qrcode.service;

import com.safeqr.app.constants.CommonConstants;
import com.safeqr.app.qrcode.dto.QRCodePayload;
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

@Service
public class QRCodeTypeService {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeTypeService.class);

    private final QRCodeFactoryProvider qrCodeFactoryProvider;

    @Autowired
    public QRCodeTypeService(QRCodeFactoryProvider qrCodeFactoryProvider) {
        this.qrCodeFactoryProvider = qrCodeFactoryProvider;
    }



    @Autowired
    private QRCodeTypeRepository qrCodeTypeRepository;
    @Autowired
    private ScanHistoryRepository scanHistoryRepository;
    @Autowired
    private QRCodeRepository qrCodeRepository;

    @Autowired
    private SafeBrowsingService safeBrowsingService;

    private List<QRCodeTypeEntity> configs;
    private QRCodeTypeEntity defaultQRCodeTypeEntity;

    @PostConstruct
    public void loadQRCodeTypes() {
        // Fetch all QR Code Types from the database
        configs = qrCodeTypeRepository.findAll();
        // Set the default QR Code Type
        defaultQRCodeTypeEntity = configs.stream()
                .filter(config -> config.getType().equals(CommonConstants.DEFAULT_QR_CODE_TYPE))
                .findFirst()
                .orElse(null);
    }

    public List<QRCodeTypeEntity> getAllTypes() {
        return configs;
    }

    public BaseScanResponse scanQRCode(String userId, QRCodePayload payload) {
        String data = payload.getData();
        logger.info("scanQRCode: userId={}, data={}", userId, data);

        // Get the QR Code Type
        QRCodeTypeEntity qrType = getQRCodeType(data);

        // Insert the QR Code into main qrcode table
        QRCodeEntity scannedQR = qrCodeRepository.save(QRCodeEntity.builder()
                .userId(userId)
                .contents(data)
                .qrCodeTypeId(qrType.getId())
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

        QRCodeModel qrCodeModel = qrCodeFactoryProvider.createQRCodeInstance(scannedQR, qrType);
        qrCodeModel.insertDB();

        return BaseScanResponse.builder().qrcode(qrCodeModel).build();
    }
    private QRCodeTypeEntity getQRCodeType(String data) {
        return configs.stream()
                .filter(config -> data.toLowerCase().startsWith(config.getPrefix().toLowerCase()))
                .findFirst()
                .orElse(defaultQRCodeTypeEntity);
    }

//    private BaseScanResponse insertIntoRespectiveTable(QRCodeEntity qrCodeEntity, QRCodeTypeEntity qrCodeTypeEntity) {
//        String contents = qrCodeEntity.getContents();
//        try {
//            QRCodeURLEntity urlObj = urlVerificationService.breakdownURL(contents);
//            List<String> redirectChain = urlVerificationService.countAndTrackRedirects(contents);
//            urlObj.setQrCodeId(qrCodeEntity.getId());
//            urlObj.setRedirect(redirectChain.size() - 1);
//            urlObj.setRedirectChain(redirectChain);
//
//            // Insert into URL table
//            urlRepository.save(urlObj);
//
//            return URLResponse.builder().scannedQRCode(qrCodeEntity).qrCode(qrCodeTypeEntity).details(urlObj).build();
//        } catch (IOException | URISyntaxException e) {
//            logger.error("Error: ", e);
//        }
//
//        return BaseScanResponse.builder()
//                .scannedQRCode(qrCodeEntity)
//                .qrCode(qrCodeTypeEntity)
//                .build();
//    }

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
