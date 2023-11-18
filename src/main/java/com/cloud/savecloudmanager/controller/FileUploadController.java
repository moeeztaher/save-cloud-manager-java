package com.cloud.savecloudmanager.controller;

import com.cloud.savecloudmanager.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;


@RestController
public class FileUploadController {

    @Autowired
    S3Service service;

    @GetMapping("/list/files")
    public ResponseEntity<List<String>> getListOfFiles() {
        return new ResponseEntity<>(service.listFiles(), HttpStatus.OK);
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/file/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("fileName") String fileName,
                                             @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(service.uploadFile(fileName, file), HttpStatus.OK);
    }

    @PostMapping("/file/uploadDirectory")
    public ResponseEntity<String> uploadDirectory(@RequestParam("directoryName") String directoryName,
                                                  @RequestParam("directoryPath") String directoryPath) {
        return new ResponseEntity<>(service.uploadDirectory(directoryName, directoryPath), HttpStatus.OK);
    }
    @PostMapping("/file/downloadDirectory")
    public ResponseEntity<String> downloadDirectory(@RequestParam("directoryName") String directoryName,
                                                  @RequestParam("directoryPath") String directoryPath) {
        return new ResponseEntity<>(service.downloadDirectory(directoryName, directoryPath), HttpStatus.OK);
    }

    @GetMapping(value = "/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        ByteArrayOutputStream downloadInputStream = service.downloadFile(filename);

        return ResponseEntity.ok()
                .contentType(contentType(filename))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(downloadInputStream.toByteArray());
    }

    @GetMapping(value = "/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable("filename") String filename) {
        return new ResponseEntity<>(service.deleteFile(filename), HttpStatus.OK);
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
