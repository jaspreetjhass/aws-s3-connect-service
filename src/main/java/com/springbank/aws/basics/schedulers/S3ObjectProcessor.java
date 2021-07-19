package com.springbank.aws.basics.schedulers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.springbank.aws.basics.configurations.S3Operations;
import com.springbank.aws.basics.utilities.AppConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class S3ObjectProcessor {

	@Autowired
	private S3Operations s3Operations;

	@Scheduled(initialDelay = 10000, fixedRate = 20000)
	public void processFile() {
		final List<Bucket> listBuckets = s3Operations.listBuckets();
		log.info("buckets found are : {}", listBuckets.stream().map(Bucket::getName).collect(Collectors.toList()));
		if (!CollectionUtils.isEmpty(listBuckets)) {
			final String originalBucketName = listBuckets.stream()
					.filter(bucket -> bucket.getName().contains(AppConstant.ORI)).findFirst().get().getName();
			final String archivedBucketName = listBuckets.stream()
					.filter(bucket -> bucket.getName().contains(AppConstant.ARCHIVED)).findFirst().get().getName();
			final ObjectListing listObjects = s3Operations.listObjects(originalBucketName);
			final List<S3ObjectSummary> objectSummaries = listObjects.getObjectSummaries();
			if (!CollectionUtils.isEmpty(objectSummaries)) {
				log.info("objects found in bucket : {}", originalBucketName);
				objectSummaries.forEach(objectSummary -> {
					s3Operations.copyObject(originalBucketName, objectSummary.getKey(), archivedBucketName,
							objectSummary.getKey().concat(AppConstant.DASH).concat(AppConstant.PROCESSED));
					s3Operations.deleteObject(originalBucketName, objectSummary.getKey());
					log.info("object moved from {} to {}", originalBucketName, archivedBucketName);
				});
			} else {
				log.info("no object found in bucket : {}. retry after sometime", originalBucketName);
			}
		}
	}

}
