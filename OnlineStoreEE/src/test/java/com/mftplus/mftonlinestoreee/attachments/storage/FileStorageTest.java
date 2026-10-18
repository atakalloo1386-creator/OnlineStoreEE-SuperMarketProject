package com.mftplus.mftonlinestoreee.attachments.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {

    @TempDir
    Path directory;             // JUnit creates an empty folder for each test and deletes it afterwards

    private FileStorage storage;

    @BeforeEach
    void setUp() {
        storage = new FileStorage(directory);
    }

    @Test
    void saveWritesTheFileUnderANewNameAndCountsItsSize() {
        FileStorage.StoredFile stored = storage.save(stream("hello"));

        assertEquals(5, stored.size());
        assertTrue(Files.exists(directory.resolve(stored.name())));
    }

    @Test
    void openReadsWhatWasSaved() throws IOException {
        String name = storage.save(stream("hello")).name();

        try (InputStream content = storage.open(name)) {
            assertEquals("hello", new String(content.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    private InputStream stream(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }
}
