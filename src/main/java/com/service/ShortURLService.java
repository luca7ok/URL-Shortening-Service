package com.service;

import com.Repository.ShortURLRepository;
import com.domain.ShortURL;
import com.exceptions.URLNotFoundException;
import com.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ShortURLService {
    private final ShortURLRepository shortURLRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public ShortURLService(ShortURLRepository shortURLRepository, ShortCodeGenerator shortCodeGenerator) {
        this.shortURLRepository = shortURLRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public ShortURL createShortURL(String longURL) {
        String shortCode;
        do {
            shortCode = shortCodeGenerator.generateShortCode();
        } while (shortURLRepository.findByShortCode(shortCode).isPresent());


        ShortURL shortURL = new ShortURL();
        shortURL.setLongURL(longURL);
        shortURL.setShortCode(shortCode);

        shortURLRepository.save(shortURL);
        return shortURL;
    }

    public String getLongURL(String shortCode) {
        return shortURLRepository.findByShortCode(shortCode)
                .map(ShortURL::getLongURL)
                .orElseThrow(() -> new URLNotFoundException("URL not found for short code: " + shortCode));
    }
}
