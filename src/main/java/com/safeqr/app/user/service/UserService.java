package com.safeqr.app.user.service;

import com.safeqr.app.exceptions.CustomNotFoundExceptions;
import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.qrcode.repository.ScanBookmarkRepository;
import com.safeqr.app.qrcode.repository.ScanHistoryRepository;
import com.safeqr.app.user.dto.UserResponseDto;
import com.safeqr.app.user.entity.UserEntity;
import com.safeqr.app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    public UserService(UserRepository userRepository,
                       ScanHistoryRepository scanHistoryRepository,
                       ScanBookmarkRepository scanBookmarkRepository) {
        this.userRepository = userRepository;
        this.scanHistoryRepository = scanHistoryRepository;
        this.scanBookmarkRepository = scanBookmarkRepository;
    }
    private final UserRepository userRepository;
    private final ScanHistoryRepository scanHistoryRepository;
    private final ScanBookmarkRepository scanBookmarkRepository;

    public UserResponseDto getUserById(String userId) {
        // Find user by id
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundExceptions("User id not found: " + userId));

        // Map to DTO before returning to controller
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .dateJoined(userEntity.getDateCreated())
                .dateUpdated(userEntity.getDateUpdated())
                .roles(userEntity.getRoles())
                .status(userEntity.getStatus())
                .build();
    }
    public List<QRCodeEntity> getUserScannedHistories(String userId) {
        return scanHistoryRepository.findAllQRCodesByUserId(userId);
    }
    public List<QRCodeEntity> getUserBookmarks(String userId) {
        return scanBookmarkRepository.findAllBookmarksByUserId(userId);
    }
}
