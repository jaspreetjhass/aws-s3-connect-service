package com.springbank.aws.basics.configurations;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;

@Component
public class S3Operations {

	@Autowired
	private AmazonS3 amazonS3;

	public boolean doesBucketExists(final String bucketName) {
		return amazonS3.doesBucketExistV2(bucketName);
	}

	public void createBucket(final String bucketName) {
		amazonS3.createBucket(bucketName);
	}

	public void putObject(final String bucketName, final String fileName, final File file) {
		amazonS3.putObject(bucketName, fileName, file);
	}

	public List<Bucket> listBuckets() {
		return amazonS3.listBuckets();
	}

	public ObjectListing listObjects(final String bucketName) {
		return amazonS3.listObjects(bucketName);
	}

	public void copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) {
		amazonS3.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
	}

	public void deleteObject(String bucketName, String key) {
		amazonS3.deleteObject(bucketName, key);
	}

}
