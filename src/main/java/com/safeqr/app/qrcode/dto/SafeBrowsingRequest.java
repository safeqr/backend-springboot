package com.safeqr.app.qrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafeBrowsingRequest {
    private Client client;
    private ThreatInfo threatInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Client {
        private String clientId;
        private String clientVersion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreatInfo {
        private List<String> threatTypes;
        private List<String> platformTypes;
        private List<String> threatEntryTypes;
        private List<ThreatEntry> threatEntries;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ThreatEntry {
            private String url;
        }
    }
}