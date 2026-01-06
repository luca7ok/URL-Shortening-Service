package com.service;

import com.Repository.ShortURLRepository;
import com.domain.ShortURL;
import com.exceptions.URLNotFoundException;
import com.utils.ShortCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.RedisClient;

import java.util.Optional;


@Service
public class ShortURLService {
    private final ShortURLRepository shortURLRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final RedisClient redisClient;
    private static final Logger logger = LoggerFactory.getLogger(ShortURLService.class);

    public ShortURLService(ShortURLRepository shortURLRepository, ShortCodeGenerator shortCodeGenerator, RedisClient redisClient) {
        this.shortURLRepository = shortURLRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.redisClient = redisClient;
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
        
        try{
            redisClient.setex(newShortURL.getShortCode(), 86400, longURL);
        } catch (Exception e) {
            logger.warn("Failed to cache new URL");
        }
        return newShortURL;
    }

    public String getLongURL(String shortCode) {
        try {
            String cachedURL = redisClient.get(shortCode);
            if (cachedURL != null) {
                logger.info("Cache hit for: {}", shortCode);
                return cachedURL;
            }
        } catch (Exception e) {
            logger.error("Redis connection failed: {}", e.getMessage());
        }
        
        logger.info("Cache miss");
        ShortURL shortURL = shortURLRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new URLNotFoundException("This short URL doesn't exist"));

        try {
            redisClient.setex(shortCode, 86400, shortURL.getLongURL());
            logger.debug("Cache update for: {}", shortCode);
        } catch (Exception e) {
            logger.warn("Could not update cache {}", e.getMessage());
        }
        return shortURL.getLongURL();
    }
}
