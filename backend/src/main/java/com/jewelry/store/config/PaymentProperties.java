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
    private VietQRProperties vietqr = new VietQRProperties();

    @Getter
    @Setter
    public static class VNPayProperties {
        private String tmnCode;
        private String hashSecret;
        private String payUrl;
        private String returnUrl;
        private String apiUrl;
    }

    @Getter
    @Setter
    public static class VietQRProperties {
        private String bankId;
        private String accountNo;
        private String accountName;
    }
}
