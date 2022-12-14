package com.example.player.controller;

import com.example.player.service.PlayerInterface;
import com.example.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

@RequiredArgsConstructor
@RestController
public class PlayerController {

    private final PlayerInterface playerInterface;

    @PostMapping(value="/player", produces = "text/csv")
    public ResponseEntity<Resource> parseCsv(@RequestParam(value="file") MultipartFile file) {
        return playerInterface.readCsv(file);
    }
}
