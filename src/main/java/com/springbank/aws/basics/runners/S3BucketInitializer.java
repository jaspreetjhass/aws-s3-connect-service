package com.springbank.aws.basics.runners;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.springbank.aws.basics.configurations.S3Properties;
import com.springbank.aws.basics.configurations.S3Operations;
import com.springbank.aws.basics.utilities.AppConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableConfigurationProperties(S3Properties.class)
public class S3BucketInitializer implements CommandLineRunner {

	@Autowired
	private S3Properties awsProperties;
	@Autowired
	private S3Operations s3Operations;

	@Override
	public void run(final String... args) throws Exception {
		if (!StringUtils.hasLength(awsProperties.getOriginalBucketName())
				|| !StringUtils.hasLength(awsProperties.getArchivedBucketName()))
			throw new IllegalArgumentException("original & archived bucket name are not passed");

		final String randomUUIDStr = UUID.randomUUID().toString();

		String originalBucketName = awsProperties.getOriginalBucketName().concat(AppConstant.DASH)
				.concat(randomUUIDStr);
		String archivedBucketName = awsProperties.getArchivedBucketName().concat(AppConstant.DASH)
				.concat(randomUUIDStr);

		originalBucketName = originalBucketName.length() > AppConstant.NUMBER_TWENTY
				? originalBucketName.substring(AppConstant.NUMBER_ZERO, AppConstant.NUMBER_TWENTY)
				: originalBucketName;
		archivedBucketName = archivedBucketName.length() > AppConstant.NUMBER_TWENTY
				? archivedBucketName.substring(AppConstant.NUMBER_ZERO, AppConstant.NUMBER_TWENTY)
				: archivedBucketName;

		if (!s3Operations.doesBucketExists(originalBucketName)) {
			s3Operations.createBucket(originalBucketName);
			log.info("original bucket : {} is created successfully. check whether upload feature is enabled or not",
					originalBucketName);
			if (awsProperties.isUploadEnabled()) {
				log.info("upload feature is enabled. check file path");
				if (!StringUtils.hasLength(awsProperties.getFilePath()))
					throw new FileNotFoundException("file path is not correct");
				final File file = new File(awsProperties.getFilePath());
				if (file.isDirectory()) {
					final String[] list = file.list();
					for (final String fileName : list) {
						s3Operations.putObject(originalBucketName, fileName,
								new File(awsProperties.getFilePath().concat(fileName)));
						log.info("file : {} is uploaded successfully to bucket : {}", file.getName(),
								originalBucketName);
					}
				}

			}
		}
		if (!s3Operations.doesBucketExists(archivedBucketName.concat(randomUUIDStr))) {
			s3Operations.createBucket(archivedBucketName);
			log.info("archived bucket : {} is created successfully.", archivedBucketName);
		}
	}

}
