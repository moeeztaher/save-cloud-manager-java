package com.cloud.savecloudmanager.dao;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface S3Dao {

    List<String> listFiles();

    String uploadFile(String fileName, MultipartFile file);

    String uploadDirectory(String directoryName, String directoryPath);

    String downloadDirectory(String directoryName, String directoryPath);

    ByteArrayOutputStream downloadFile(String filename);

    String deleteFile(String filename);
}
