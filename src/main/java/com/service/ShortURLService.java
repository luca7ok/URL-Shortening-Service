package com.service;

import com.Repository.ShortURLRepository;
import com.domain.ShortURL;
import com.exceptions.URLNotFoundException;
import com.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ShortURLService {
    private final ShortURLRepository shortURLRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public ShortURLService(ShortURLRepository shortURLRepository, ShortCodeGenerator shortCodeGenerator) {
        this.shortURLRepository = shortURLRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public ShortURL createShortURL(String longURL) {
        Optional<ShortURL> existingShortURL = shortURLRepository.findByLongURL(longURL);

        if (existingShortURL.isPresent()) {
            return existingShortURL.get();
        }

        String shortCode;
        do {
            shortCode = shortCodeGenerator.generateShortCode();
        } while (shortURLRepository.findByShortCode(shortCode).isPresent());

        ShortURL newShortURL = new ShortURL();
        newShortURL.setLongURL(longURL);
        newShortURL.setShortCode(shortCode);

        shortURLRepository.save(newShortURL);
        return newShortURL;
    }

    public String getLongURL(String shortCode) {
        return shortURLRepository.findByShortCode(shortCode)
                .map(ShortURL::getLongURL)
                .orElseThrow(() -> new URLNotFoundException("This short URL doesn't exist"));
    }
}
