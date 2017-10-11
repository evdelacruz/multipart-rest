package com.evdelacruz.samples.multipartrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.springframework.http.ResponseEntity.*;

/**
 * Main and controller class.<br>
 * Suggested approach: {@link #uploadFile}
 *
 * @author Erick Vega De la Cruz
 * @since 1.0
 */
@SpringBootApplication
@RestController
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    /**
     * One by one.
     *
     * @param file File to upload.
     *
     * @return <code>200</code> (OK) if everything is fine, otherwise <code>400</code> (Bad Request).
     */
    @PostMapping("/upload/single-file")
    public ResponseEntity<?> uploadFile(@RequestParam(name="filename") MultipartFile file) {
        try {
            if (null == file || file.isEmpty()) {
                throw new IllegalArgumentException("File can't be null or empty !!!");
            }
            logger.debug("Received file: {}", file.getName());
            this.save(file);
            return ok(String.format("Successfully uploaded - %s", file.getOriginalFilename()));
        } catch (Exception ex) {
            logger.error("Unexpexted error !!!", ex);
            return badRequest().body("File upload attempt failed !!!");
        }
    }

    /**
     * Multiple files in one request.
     *
     * @param files Array of image files
     *
     * @return <code>200</code> (OK) if everything is fine, otherwise <code>400</code> (Bad Request).
     */
    @PostMapping("/upload/multiple-file")
    public ResponseEntity<?> uploadFiles(@RequestParam(name="filenames") MultipartFile[] files) {
        try {
            if (null == files || 0 == files.length) {
                throw new IllegalArgumentException("File can't be null or empty !!!");
            }
            String names = Stream.of(files)
                    .peek(this::save)
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.joining(", "));
            logger.debug("Received files: {}", names);
            return ResponseEntity.ok(String.format("Successfully uploaded - %s", names));
        } catch (Exception ex) {
            logger.info("Unexpexted error !!!", ex);
            return ResponseEntity.badRequest().body("File upload attempt failed !!!");
        }
    }

    //<editor-fold desc="Support methods">
    private void save(MultipartFile file) {
        try {
            Path path = Paths.get("H:\\TMP\\" + UUID.randomUUID());
            Files.write(path, file.getBytes());
        } catch (IOException ex) {
            logger.error("Unexpexted error !!!", ex);
        }
    }
    //</editor-fold>
}
