
package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.dto.RedirectCountResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RedirectCountService {
    private static final Logger logger = LoggerFactory.getLogger(RedirectCountService.class);

    public Mono<RedirectCountResponse> countRedirects(QRCodePayload payload) {
        String url = payload.getData();
        logger.info("RedirectCountService: countRedirects: url={}", url);

        return WebClient.create()
                .get()
                .uri("https://google.com")// replace with url when logic is complete
                .exchangeToMono(response -> {
                    RedirectCountResponse redirectCountResponse = new RedirectCountResponse();
                    redirectCountResponse.setRedirectCount(response.cookies().size());
                    redirectCountResponse.setMessage("Redirect count calculated.");
                    return Mono.just(redirectCountResponse);
                });
    }
}
