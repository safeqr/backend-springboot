
package com.safeqr.app.qrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafeBrowsingResponse {
    private List<ThreatMatch> matches;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreatMatch {
        private Threat threat;
        private String threatType;
        private String platformType;
        private String threatEntryType;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Threat {
            private String url;
            private String hash;
        }
    }
}
