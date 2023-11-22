package com.cloud.savecloudmanager.controller;

import com.cloud.savecloudmanager.dao.EncryptionDao;
import com.cloud.savecloudmanager.services.EncryptionDaoImpl;
import com.cloud.savecloudmanager.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

@RestController
public class KmsEncryptionController {

    private final EncryptionDao encryptionDao;
    private final EncryptionDaoImpl encryptionDaoImpl;

    @Autowired
    public KmsEncryptionController(
            EncryptionDao encryptionDao,
            EncryptionDaoImpl encryptionDaoImpl,
            S3Service s3Service) {
        this.encryptionDao = encryptionDao;
        this.encryptionDaoImpl = encryptionDaoImpl;
    }
    private static final Logger logger = LoggerFactory.getLogger(KmsEncryptionController.class);



    @PostMapping("/file/uploadEncrypted")
    public ResponseEntity<String> uploadEncryptedFile(@RequestPart("file") MultipartFile file) {
        try {
            byte[] encryptedFile = encryptionDaoImpl.encryptFile(file.getBytes());
            encryptionDao.uploadEncryptedFile(encryptedFile, file.getOriginalFilename());

            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded and encrypted successfully");
        } catch (Exception e) {
            logger.error("An error occurred in the uploadEncryptedFile method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading and encrypting file");
        }
    }

    @GetMapping(value = "/downloadEncrypted/{filename}")
    public ResponseEntity<byte[]> downloadEncryptedFile(@PathVariable String filename) {
        try {
            ByteArrayOutputStream downloadInputStream = encryptionDao.downloadDecryptedFile(filename);

            if (downloadInputStream != null && downloadInputStream.size() > 0) {
                return ResponseEntity.ok()
                        .contentType(contentType(filename))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(downloadInputStream.toByteArray());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log the exception using your preferred logging framework
            // For now, let's print the stack trace
            logger.error("An error occurred in the downloadEncryptedFile method", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading file".getBytes());
        }
    }


    private MediaType contentType(String filename) {
        String[] fileArrSplit = filename.split("\\.");
        String fileExtension = fileArrSplit[fileArrSplit.length - 1];
        switch (fileExtension) {
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
