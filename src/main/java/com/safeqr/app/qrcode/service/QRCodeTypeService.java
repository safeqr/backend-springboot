
package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.entity.QRCodeType;
import com.safeqr.app.qrcode.repository.QRCodeTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class QRCodeTypeService {

    @Autowired
    private QRCodeTypeRepository qrCodeTypeRepository;

    @Autowired
    private SafeBrowsingService safeBrowsingService;

    public List<QRCodeType> getAllTypes() {
        return qrCodeTypeRepository.findAll();
    }

    public Mono<String> detectType(QRCodePayload payload) {
        String data = payload.getData();
        List<QRCodeType> configs = qrCodeTypeRepository.findAll();

        for (QRCodeType config : configs) {
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
