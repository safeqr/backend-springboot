package com.safeqr.app.user.controller;

import static com.safeqr.app.constants.APIConstants.*;
import static com.safeqr.app.constants.CommonConstants.HEADER_USER_ID;

import com.safeqr.app.qrcode.entity.QRCodeEntity;
import com.safeqr.app.user.dto.BaseResponse;
import com.safeqr.app.user.dto.BookmarkRequestDto;
import com.safeqr.app.user.dto.ScannedHistoriesDto;
import com.safeqr.app.user.dto.UserResponseDto;
import com.safeqr.app.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(API_VERSION)
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> version() {
        logger.info("Health Check");
        return ResponseEntity.ok(Map.of("version","SafeQR v1.0.2"));
    }

    @GetMapping(value = API_URL_USER_GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDto> getUser(@RequestHeader(name = HEADER_USER_ID) String userId) {
        logger.info("Invoking GET User endpoint");
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping(value = API_URL_USER_GET_SCANNED_HISTORIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScannedHistoriesDto>> getUserScannedHistories(@RequestHeader(name = HEADER_USER_ID) String userId) {
        logger.info("Invoking GET User Scanned Histories endpoint");
        return ResponseEntity.ok(userService.getUserScannedHistories(userId));
    }

    @PutMapping(value = API_URL_USER_DELETE_SCANNED_HISTORIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteScannedHistory(@RequestHeader(name = HEADER_USER_ID) String userId, @RequestBody BookmarkRequestDto bookmarkRequestDto) {
        logger.info("Invoking PUT Delete Single Scanned History endpoint");
        return ResponseEntity.ok(userService.deleteScannedHistory(userId, bookmarkRequestDto.getQrCodeId()));
    }

    @PutMapping(value = API_URL_USER_DELETE_ALL_SCANNED_HISTORIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteAllScannedHistories(@RequestHeader(name = HEADER_USER_ID) String userId) {
        logger.info("Invoking PUT Delete All Scanned Histories endpoint");
        return ResponseEntity.ok(userService.deleteAllScannedHistoriesByUserId(userId));
    }

    @GetMapping(value = API_URL_USER_GET_BOOKMARKS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QRCodeEntity>> getUserBookmarks(@RequestHeader(name = HEADER_USER_ID) String userId) {
        logger.info("Invoking GET User bookmarks endpoint");
        return ResponseEntity.ok(userService.getUserBookmarks(userId));
    }

    @PostMapping(value = API_URL_USER_SET_BOOKMARK, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> setBookmark(@RequestHeader(name = HEADER_USER_ID) String userId, @RequestBody BookmarkRequestDto bookmarkRequestDto) {
        logger.info("Invoking POST User bookmark endpoint");
        return ResponseEntity.ok(userService.setBookmark(userId, bookmarkRequestDto.getQrCodeId()));
    }

    @PutMapping(value = API_URL_USER_DELETE_BOOKMARK, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteBookmark(@RequestHeader(name = HEADER_USER_ID) String userId, @RequestBody BookmarkRequestDto bookmarkRequestDto) {
        logger.info("Invoking PUT Delete Single Bookmark endpoint");
        return ResponseEntity.ok(userService.deleteBookmark(userId, bookmarkRequestDto.getQrCodeId()));
    }

    @PutMapping(value = API_URL_USER_DELETE_ALL_BOOKMARK, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteAllBookmark(@RequestHeader(name = HEADER_USER_ID) String userId) {
        logger.info("Invoking PUT Delete All Bookmark endpoint");
        return ResponseEntity.ok(userService.deleteAllBookmarkByUserId(userId));
    }
}
