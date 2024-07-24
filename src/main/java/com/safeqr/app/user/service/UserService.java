package com.safeqr.app.user.service;

import com.safeqr.app.exceptions.ResourceAlreadyExists;
import com.safeqr.app.exceptions.ResourceNotFoundExceptions;
import com.safeqr.app.qrcode.entity.ScanBookmarkEntity;
import com.safeqr.app.qrcode.entity.ScanHistoryEntity;
import com.safeqr.app.qrcode.repository.ScanBookmarkRepository;
import com.safeqr.app.qrcode.repository.ScanHistoryRepository;
import com.safeqr.app.user.dto.BaseResponse;
import com.safeqr.app.user.dto.ScannedHistoriesDto;
import com.safeqr.app.user.dto.UserResponseDto;
import com.safeqr.app.user.entity.UserEntity;
import com.safeqr.app.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
                .orElseThrow(() -> new ResourceNotFoundExceptions("User id not found: " + userId));

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
    public List<ScannedHistoriesDto> getUserScannedHistories(String userId) {
        return scanHistoryRepository.findAllQRCodesByUserId(userId);
    }
    @Transactional
    public BaseResponse deleteScannedHistory(String userId, UUID qrCodeId) {
        int updatedCount = scanHistoryRepository.updateScannedHistoryToInactive(userId, qrCodeId);
        // throw exception if bookmark not found
        if (updatedCount < 1)
            throw new ResourceNotFoundExceptions("Scanned QR Code not found");

        return BaseResponse.builder().message("Scanned QR Code deleted successfully").build();
    }

    @Transactional
    public BaseResponse deleteAllScannedHistoriesByUserId(String userId) {
        int updatedCount = scanHistoryRepository.updateScannedHistoriesToInactiveByUserId(userId);

        return (updatedCount < 1) ?
                BaseResponse.builder().message("No QR Code not found").build():
                BaseResponse.builder().message("All scanned QR Code deleted successfully").build();
    }
    public List<ScannedHistoriesDto> getUserBookmarks(String userId) {
        return scanHistoryRepository.findAllBookmarksByUserId(userId);
    }
    @Transactional
    public BaseResponse setBookmark(String userId, UUID qrCodeId) {
        // Check if the bookmark already exists
        Optional<ScanHistoryEntity> existingBookmark = scanHistoryRepository.findByUserIdAndQrCodeId(userId, qrCodeId);

        // throw exception if bookmark already exists
        if (existingBookmark.isPresent()) {
            throw new ResourceAlreadyExists("Bookmark already exists!");
        }

        // Save bookmark by updating booked to true
        int updatedCount = scanHistoryRepository.updateBookmarkStatusToActive(userId, qrCodeId);

        if (updatedCount < 1)
            throw new ResourceNotFoundExceptions("Unable to create bookmark. The QR code may not exist.");

        return BaseResponse.builder().message("Bookmark saved successfully").build();

    }

    @Transactional
    public BaseResponse deleteBookmark(String userId, UUID qrCodeId) {
        int updatedCount = scanHistoryRepository.updateBookmarkStatusToInactive(userId, qrCodeId);
        // throw exception if bookmark not found
        if (updatedCount < 1)
            throw new ResourceNotFoundExceptions("Bookmark not found");

        return BaseResponse.builder().message("Bookmark deleted successfully").build();
    }

    @Transactional
    public BaseResponse deleteAllBookmarkByUserId(String userId) {
        int updatedCount = scanHistoryRepository.updateBookmarkStatusToInactiveByUserId(userId);

        return (updatedCount < 1) ?
                BaseResponse.builder().message("No Bookmark not found").build():
                BaseResponse.builder().message("All Bookmarks deleted successfully").build();
    }
}
