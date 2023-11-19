package com.cloud.savecloudmanager.dao;

import java.io.ByteArrayOutputStream;

public interface EncryptionDao {

    void uploadEncryptedFile(byte[] encryptedFile, String fileName);

    ByteArrayOutputStream downloadDecryptedFile(String fileName);
}
