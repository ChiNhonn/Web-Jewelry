package com.jewelry.store.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private VNPayProperties vnpay = new VNPayProperties();

    @Getter
    @Setter
    public static class VNPayProperties {
        private String tmnCode;
        private String hashSecret;
        private String payUrl;
        private String returnUrl;
        private String apiUrl;
    }
}
