package com.cloud.savecloudmanager.services;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import com.amazonaws.services.kms.model.DescribeKeyResult;
import com.amazonaws.services.kms.model.KeyMetadata;
import org.springframework.beans.factory.annotation.Value;

public class KmsKeyIdRetriever {
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
    public String retrieveKms() {
       System.out.println(awsKey);
        return "done";
    }
}
