package com.springbank.aws.basics.starter;

import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.springbank.aws.basics.configurations.S3Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.springbank.aws.basics" })
@EnableConfigurationProperties(value = { S3Properties.class })
public class AwsBasicServicesApplication {

	@Autowired
	private AmazonS3 amazonS3;
	@Autowired
	private S3Properties awsProperties;

	public static void main(final String[] args) {
		SpringApplication.run(AwsBasicServicesApplication.class, args);
	}

	@PreDestroy
	public void removeS3Buckets() {
		if (awsProperties.isDestroyS3Objects()) {
			log.info("check S3 and remove all buckets initialized during the application boot up");
			final List<Bucket> listBuckets = amazonS3.listBuckets();
			listBuckets.stream().forEach(bucket -> {
				final String bucketName = bucket.getName();
				log.info("bucket name : {} --> bucket owner : {} --> creation date : {}", bucketName,
						bucket.getOwner().getId(), bucket.getCreationDate());
				final ObjectListing listObjects = amazonS3.listObjects(bucket.getName());
				final List<S3ObjectSummary> objectSummaries = listObjects.getObjectSummaries();
				if (!objectSummaries.isEmpty()) {
					log.info("objects present in bucket : {}. deleting objects present in the bucket", bucketName);
				}
				objectSummaries.forEach(objectSummary -> {
					final String objectKey = objectSummary.getKey();
					log.info("Object found with key : {} --> modified date : {} --> storage class : {}", objectKey,
							objectSummary.getLastModified(), objectSummary.getStorageClass());
					amazonS3.deleteObject(bucketName, objectKey);
					log.info("object with key : {} is deleted successfully", objectKey);
				});
				amazonS3.deleteBucket(bucket.getName());
				log.info("bucket : {} is deleted successfully", bucketName);
			});
		}
	}

}
