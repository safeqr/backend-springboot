
package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.dto.QRCodePayload;
import com.safeqr.app.qrcode.dto.RedirectCountResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RedirectCountService {

    public Mono<RedirectCountResponse> countRedirects(QRCodePayload payload) {
        String url = payload.getData();

        return WebClient.create()
                .get()
                .uri(url)
                .exchangeToMono(response -> {
                    RedirectCountResponse redirectCountResponse = new RedirectCountResponse();
                    redirectCountResponse.setRedirectCount(response.cookies().size());
                    redirectCountResponse.setMessage("Redirect count calculated.");
                    return Mono.just(redirectCountResponse);
                });
    }
}
