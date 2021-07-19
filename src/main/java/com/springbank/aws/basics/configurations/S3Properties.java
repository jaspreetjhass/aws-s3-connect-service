package com.springbank.aws.basics.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "aws.services.s3")
public class S3Properties {

	private boolean destroyS3Objects;
	private boolean uploadEnabled;
	private String filePath;
	private String originalBucketName;
	private String archivedBucketName;
}
