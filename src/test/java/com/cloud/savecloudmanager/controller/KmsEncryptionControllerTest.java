package com.cloud.savecloudmanager.controller;

import com.cloud.savecloudmanager.services.EncryptionDaoImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class KmsEncryptionControllerTest {

    @Mock
    private EncryptionDaoImpl encryptionDaoImpl;

    @InjectMocks
    private KmsEncryptionController kmsEncryptionController;

    @Test
    public void testUploadEncryptedFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

        when(encryptionDaoImpl.encryptFile(any(byte[].class))).thenReturn("encryptedContent".getBytes());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(kmsEncryptionController).build();

        mockMvc.perform(multipart("/file/uploadEncrypted").file(file))
                .andExpect(status().isCreated())
                .andExpect(content().string("File uploaded and encrypted successfully"));

        verify(encryptionDaoImpl, times(1)).uploadEncryptedFile(any(byte[].class), eq("test.txt"));
    }

    @Test
    void testDownloadEncryptedFileNotFound() {
        when(encryptionDaoImpl.downloadDecryptedFile(anyString())).thenReturn(null);

        ResponseEntity<byte[]> response = kmsEncryptionController.downloadEncryptedFile("nonexistentFile.txt");

        verify(encryptionDaoImpl, times(1)).downloadDecryptedFile("nonexistentFile.txt");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
