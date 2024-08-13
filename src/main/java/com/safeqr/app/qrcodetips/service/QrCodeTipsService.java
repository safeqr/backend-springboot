package com.safeqr.app.qrcodetips.service;

import com.safeqr.app.qrcodetips.entity.QrCodeTipEntity;
import com.safeqr.app.qrcodetips.repository.QrCodeTipRepository;
import org.springframework.stereotype.Service;

@Service
public class QrCodeTipsService {
    QrCodeTipRepository qrCodeTipRepository;
    public QrCodeTipsService (QrCodeTipRepository qrCodeTipRepository) { this.qrCodeTipRepository = qrCodeTipRepository; }
    public QrCodeTipEntity getTips() {
        return qrCodeTipRepository.findRandomTip();
    }
}
