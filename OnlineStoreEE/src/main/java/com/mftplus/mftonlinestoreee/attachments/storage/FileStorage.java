package com.mftplus.mftonlinestoreee.attachments.storage;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@ApplicationScoped
public class FileStorage {

    private Path directory;

    public FileStorage() {
        this(defaultDirectory());
    }

    public FileStorage(Path directory) {
        this.directory = directory;
    }

    public StoredFile save(InputStream content) {
        String name = UUID.randomUUID().toString();
        try {
            Files.createDirectories(directory);
            long size = Files.copy(content, directory.resolve(name));
            return new StoredFile(name, size);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save file " + name, e);
        }
    }

    public InputStream open(String name) {
        try {
            return Files.newInputStream(directory.resolve(name));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read file " + name, e);
        }
    }

    private static Path defaultDirectory() {
        String configured = System.getProperty("STORE_UPLOAD_DIR", System.getenv("STORE_UPLOAD_DIR"));
        if (configured != null && !configured.isBlank()) {
            return Path.of(configured);
        }
        return Path.of(System.getProperty("user.home"), "mft-store", "uploads");
    }

    public record StoredFile(String name, long size) {
    }
}
