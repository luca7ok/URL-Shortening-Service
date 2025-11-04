package com.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class ShortCodeGenerator {
    private final SecureRandom random = new SecureRandom();

    public String generateShortCode() {
        byte[] bytes = new byte[5];
        random.nextBytes(bytes);
        String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return base64.substring(0, 7);
    }
}
