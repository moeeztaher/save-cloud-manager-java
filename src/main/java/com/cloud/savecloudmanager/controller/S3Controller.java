package com.cloud.savecloudmanager.controller;

import com.cloud.savecloudmanager.dao.S3Dao;
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
import java.util.Collections;
import java.util.List;


@RestController
public class S3Controller {

    @Autowired
    S3Dao s3Dao;

    private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);

    @GetMapping("/list/files")
    public ResponseEntity<List<String>> getListOfFiles() {
        try {
            List<String> fileList = s3Dao.listFiles();
            return new ResponseEntity<>(fileList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred in the getListOfFiles method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/file/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("fileName") String fileName,
                                             @RequestParam("file") MultipartFile file) {
        try {
            String result = s3Dao.uploadFile(fileName, file);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            
            logger.error("An error occurred in the uploadFile method", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    @PostMapping("/file/uploadDirectory")
    public ResponseEntity<String> uploadDirectory(@RequestParam("directoryName") String directoryName,
                                                  @RequestParam("directoryPath") String directoryPath) {
        try {
            String result = s3Dao.uploadDirectory(directoryName, directoryPath);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            
            logger.error("An error occurred in the uploadDirectory method", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading directory");
        }
    }

    @PostMapping("/file/downloadDirectory")
    public ResponseEntity<String> downloadDirectory(@RequestParam("directoryName") String directoryName,
                                                    @RequestParam("directoryPath") String directoryPath) {
        try {
            String result = s3Dao.downloadDirectory(directoryName, directoryPath);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            
            logger.error("An error occurred in the downloadDirectory method", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading directory");
        }
    }

    @GetMapping(value = "/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            ByteArrayOutputStream downloadInputStream = s3Dao.downloadFile(filename);

            return ResponseEntity.ok()
                    .contentType(contentType(filename))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(downloadInputStream.toByteArray());
        } catch (Exception e) {
            
            logger.error("An error occurred in the downloadFile method", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable("filename") String filename) {
        try {
            String result = s3Dao.deleteFile(filename);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            
            logger.error("An error occurred in the deleteFile method", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }

    private MediaType contentType(String filename) {
        try {
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
        } catch (Exception e) {
            
            logger.error("An error occurred in the contentType method", e);

            // Return a default content type in case of an exception
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
