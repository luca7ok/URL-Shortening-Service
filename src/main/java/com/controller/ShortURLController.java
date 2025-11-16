package com.controller;

import com.domain.ShortURL;
import com.dto.ShortURLRequest;
import com.dto.ShortURLResponse;
import com.service.ShortURLService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/")
@SuppressWarnings("unused")
public class ShortURLController {
    private final ShortURLService shortURLService;

    public ShortURLController(ShortURLService shortURLService) {
        this.shortURLService = shortURLService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortURLResponse> createShortURL(@RequestBody ShortURLRequest shortURLRequest, HttpServletRequest request) {
        ShortURL shortURL = shortURLService.createShortURL(shortURLRequest.longURL());
        String fullShortURL = ServletUriComponentsBuilder.fromContextPath(request)
                .path("/{shortCode}")
                .buildAndExpand(shortURL.getShortCode())
                .toUriString();

        ShortURLResponse response = new ShortURLResponse(shortURLRequest.longURL(), shortURL.getShortCode(), fullShortURL);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirectToLongURL(@PathVariable String shortCode) {
        try {
            String longURL = shortURLService.getLongURL(shortCode);

            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .location(URI.create(longURL))
                    .build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
