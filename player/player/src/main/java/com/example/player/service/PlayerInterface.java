package com.example.player.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PlayerInterface {
    ResponseEntity<Resource> readCsv(MultipartFile file);
}
