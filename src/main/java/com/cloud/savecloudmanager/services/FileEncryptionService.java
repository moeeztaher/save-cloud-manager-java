package com.cloud.savecloudmanager.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class FileEncryptionService {

    @Value("${application.bucket.name}")
    private String bucketName;
    @Value("${cloud.aws.credentials.accessKey}")
    private String awsId;

    @Value("${cloud.aws.credentials.secretKey}")
    private String awsKey;
    @Value("${cloud.aws.credentials.kms}")
    private String kmsKeyId;

    @Value("${cloud.aws.region.static}")
    private String region;

    public byte[] encryptFile(byte[] fileContent) {

        AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsId, awsKey)))
                .withRegion(region)
                .build();

        List<byte[]> ciphertextChunks = new ArrayList<>();

        int chunkSize = 4096;
        for (int i = 0; i < fileContent.length; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, fileContent.length);
            byte[] chunk = Arrays.copyOfRange(fileContent, i, endIndex);

            EncryptRequest encryptRequest = new EncryptRequest()
                    .withKeyId(kmsKeyId)
                    .withPlaintext(ByteBuffer.wrap(chunk));

            EncryptResult encryptResponse = kmsClient.encrypt(encryptRequest);

            byte[] ciphertextChunk = encryptResponse.getCiphertextBlob().array();
            ciphertextChunks.add(ciphertextChunk);
        }

        return concatenateByteArrays(ciphertextChunks);
    }

    private byte[] concatenateByteArrays(List<byte[]> byteArrays) {
        int totalLength = byteArrays.stream().mapToInt(array -> array.length).sum();
        byte[] result = new byte[totalLength];

        int currentIndex = 0;
        for (byte[] array : byteArrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }

        return result;
    }

    public void uploadEncryptedFileToS3(byte[] encryptedFile, String fileName) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsId, awsKey)))
                .withRegion(region)
                .build();

        String base64EncodedCiphertext = Base64.getEncoder().encodeToString(encryptedFile);
        s3Client.putObject(bucketName, fileName, base64EncodedCiphertext);
//        s3Client.putObject(bucketName, fileName, Arrays.toString(encryptedFile));

        // Optionally, you might want to store metadata or other information in S3
        // s3Client.putObjectMetadata(bucketName, fileName, metadata);
    }

    public ByteArrayOutputStream downloadDecryptedFileFromS3(String fileName) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsId, awsKey)))
                .withRegion(region)
                .build();

        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

        try {
            String base64EncodedCiphertext = new String(readInputStream(objectInputStream));
            byte[] encryptedFile = Base64.getDecoder().decode(base64EncodedCiphertext);
            byte[] decryptedFile = decryptFile(encryptedFile);

            // Convert the decrypted content to a ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(decryptedFile);

            return outputStream;
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }

        return null;
    }


    private void saveToFile(byte[] content, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(content);
        }
    }


    private byte[] readInputStream(S3ObjectInputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }

    private byte[] decryptFile(byte[] encryptedFile) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsId, awsKey)))
                .withRegion(region)
                .build();

        List<byte[]> plaintextChunks = new ArrayList<>();

        int chunkSize = 4096;
        for (int i = 0; i < encryptedFile.length; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, encryptedFile.length);
            byte[] chunk = Arrays.copyOfRange(encryptedFile, i, endIndex);

            DecryptRequest decryptRequest = new DecryptRequest()
                    .withKeyId(kmsKeyId)
                    .withCiphertextBlob(ByteBuffer.wrap(chunk));

            try {
                DecryptResult decryptResponse = kmsClient.decrypt(decryptRequest);
                byte[] decryptedChunk = decryptResponse.getPlaintext().array();
                plaintextChunks.add(decryptedChunk);
            } catch (InvalidCiphertextException e) {
                // Handle the exception, log it, and consider the ciphertext invalid.
                e.printStackTrace();
            }
        }

        return concatenateByteArrays(plaintextChunks);
    }
}
