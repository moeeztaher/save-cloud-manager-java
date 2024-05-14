# Cloud Save Manager Backend

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS_S3-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![AWS KMS](https://img.shields.io/badge/AWS_KMS-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
  - [Running the Application](#running-the-application)
  - [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## Introduction
The Cloud Save Manager Backend is a Spring Boot application designed to handle the uploading and downloading of files to and from an AWS S3 bucket. This project provides a robust and scalable solution for managing file storage in the cloud with enhanced security using AWS Key Management Service (KMS) for encryption and decryption of files.

## Features
- Upload files to AWS S3
- Download files from AWS S3
- Secure file storage with AWS S3 and KMS encryption
- Easy configuration with Spring Boot

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven
- AWS account with S3 and KMS permissions
- AWS CLI configured with your credentials

### Installation
1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/cloud-save-manager-backend.git
    ```
2. Navigate to the project directory:
    ```bash
    cd cloud-save-manager-backend
    ```
3. Build the project using Maven:
    ```bash
    mvn clean install
    ```

## Usage

### Running the Application
1. Set up your AWS credentials and configure the necessary environment variables:
    ```bash
    export AWS_ACCESS_KEY_ID=your_access_key_id
    export AWS_SECRET_ACCESS_KEY=your_secret_access_key
    export AWS_REGION=your_aws_region
    export S3_BUCKET_NAME=your_s3_bucket_name
    export KMS_KEY_ID=your_kms_key_id
    ```
2. Run the application:
    ```bash
    mvn spring-boot:run
    ```

### API Endpoints
- **Upload a file**
  - **URL:** `/api/files/upload`
  - **Method:** `POST`
  - **Description:** Uploads a file to the specified S3 bucket with KMS encryption.
  - **Request Body:** `multipart/form-data`

- **Download a file**
  - **URL:** `/api/files/download/{filename}`
  - **Method:** `GET`
  - **Description:** Downloads a file from the specified S3 bucket and decrypts it using KMS.
  - **Path Variable:** `filename` - The name of the file to download.

## Configuration
Configuration details such as AWS credentials, S3 bucket name, and KMS key ID should be set in the `application.properties` file or through environment variables. Here is an example of the `application.properties` setup:

```properties
aws.accessKeyId=your_access_key_id
aws.secretKey=your_secret_access_key
aws.region=your_aws_region
s3.bucket.name=your_s3_bucket_name
kms.key.id=your_kms_key_id
```
Contributing
Contributions are welcome! Please follow these steps to contribute:

Fork the repository.
Create a new branch (git checkout -b feature/your-feature).
Commit your changes (git commit -m 'Add some feature').
Push to the branch (git push origin feature/your-feature).
Create a new Pull Request.
