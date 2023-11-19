package com.cloud.savecloudmanager.controller;

import com.cloud.savecloudmanager.services.FileEncryptionService;
import com.cloud.savecloudmanager.services.S3Service;
import com.cloud.savecloudmanager.services.KmsKeyIdRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@RestController
public class KmsEncryptionController {

    @Autowired
    private FileEncryptionService fileEncryptionService;
    private KmsKeyIdRetriever kmsKeyService;
    private S3Service s3Service;

    @PostMapping("/file/uploadEncrypted")
    public ResponseEntity<String> uploadEncryptedFile(@RequestPart("file") MultipartFile file) {
        try {
            byte[] encryptedFile = fileEncryptionService.encryptFile(file.getBytes());
            fileEncryptionService.uploadEncryptedFileToS3(encryptedFile, file.getOriginalFilename());

            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded and encrypted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading and encrypting file");
        }
    }
//    @GetMapping(value = "/download/{filename}")
//    public ResponseEntity<String> downloadEncryptedFile(@RequestPart("fileName") String fileName) {
//        try {
//            fileEncryptionService.downloadDecryptedFileFromS3(fileName);
//            return ResponseEntity.status(HttpStatus.CREATED).body("File downloaded successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading and decrypting file");
//        }
//    }

    @GetMapping(value = "/downloadEncrypted/{filename}")
    public ResponseEntity<byte[]> downloadEncryptedFile(@PathVariable String filename) {
        ByteArrayOutputStream downloadInputStream = fileEncryptionService.downloadDecryptedFileFromS3(filename);

        if (downloadInputStream != null && downloadInputStream.size() > 0) {
            return ResponseEntity.ok()
                    .contentType(contentType(filename))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(downloadInputStream.toByteArray());
        } else {
            return ResponseEntity.notFound().build();
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
