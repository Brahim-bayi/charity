package com.dev.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

/**
 * Gère l'upload et la récupération de fichiers (images, documents) liés aux projets.
 * Les fichiers sont stockés sur le disque local dans le répertoire configuré par app.upload.dir.
 */
@RestController
@RequestMapping("/api/media")
@Slf4j
public class MediaController {

    // Répertoire de stockage configurable via application.properties (défaut : "uploads")
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Uploade un fichier et retourne son URL publique.
     * Un UUID est préfixé au nom original pour éviter les collisions de noms de fichiers.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), dir.resolve(filename));
        log.info("Fichier uploadé : {}", filename);
        return ResponseEntity.ok(Map.of("url", "/api/media/" + filename));
    }

    // Sert le fichier brut depuis le disque ; retourne 404 si le fichier n'existe pas
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> get(@PathVariable String filename) throws IOException {
        Path file = Paths.get(uploadDir, filename);
        if (!Files.exists(file)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Files.readAllBytes(file));
    }
}
