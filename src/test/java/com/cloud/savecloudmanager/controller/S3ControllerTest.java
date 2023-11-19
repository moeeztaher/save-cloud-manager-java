package com.cloud.savecloudmanager.controller;

import com.cloud.savecloudmanager.dao.S3Dao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class S3ControllerTest {

    @Mock
    private S3Dao s3Dao;

    @InjectMocks
    private S3Controller controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetListOfFiles() {
        when(s3Dao.listFiles()).thenReturn(Collections.singletonList("file.txt"));

        ResponseEntity<List<String>> response = controller.getListOfFiles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList("file.txt"), response.getBody());
    }

    @Test
    public void testUploadFile() {
        // Mock the behavior of s3Dao.uploadFile
        when(s3Dao.uploadFile(anyString(), any(MultipartFile.class))).thenReturn("File uploaded successfully");

        // Create a mock MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);

        ResponseEntity<String> response = controller.uploadFile("file.txt", mockFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File uploaded successfully", response.getBody());
    }

    @Test
    public void testGetListOfFilesError() {
        when(s3Dao.listFiles()).thenThrow(new RuntimeException("An error occurred"));

        ResponseEntity<List<String>> response = controller.getListOfFiles();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void testUploadDirectorySuccess() {
        // Mock the behavior of the s3Dao.uploadDirectory method when it's called with valid parameters
        when(s3Dao.uploadDirectory(anyString(), anyString())).thenReturn("Directory uploaded successfully");

        // Call the method under test
        ResponseEntity<String> response = controller.uploadDirectory("testDirectory", "/path/to/directory");

        // Verify that the s3Dao.uploadDirectory method was called with the expected parameters
        verify(s3Dao, times(1)).uploadDirectory("testDirectory", "/path/to/directory");

        // Verify the response status and body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Directory uploaded successfully", response.getBody());
    }

}
