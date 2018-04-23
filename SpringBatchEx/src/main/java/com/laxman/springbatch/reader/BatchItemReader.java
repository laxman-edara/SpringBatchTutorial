package com.laxman.springbatch.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.laxman.springbatch.domain.Contact;

public class BatchItemReader extends FlatFileItemReader<Contact> {

	@Value("#{jobParameters['inputFile']}")
	private String resourceInfo;
	
	@Override
	public void setResource(Resource resource) {
		if (!StringUtils.isEmpty(resourceInfo)) {
			resource = new ClassPathResource(resourceInfo);
		}
	}

}
