/**
 * 
 */
package com.laxman.springbatch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laxman.springbatch.domain.Contact;

/**
 * @author ledara
 *
 */
@RestController
public class BatchRestController {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobBuilder jobBuilder;

	@Autowired
	private Step batchStep1;

	@Value(value = "${spring.resourceFile}")
	private String inputFile;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private FlatFileItemReader<Contact> batchItemReader;

	@RequestMapping("/launchjob")
	public ResponseEntity<String> handle() throws Exception {
		batchItemReader.setResource(resourceLoader.getResource("classpath:" + inputFile));
		Logger logger = LoggerFactory.getLogger(this.getClass());
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();
			Job jobInfo = jobBuilder.flow(batchStep1).end().build();
			JobExecution jobExec = jobLauncher.run(jobInfo, jobParameters);
			if (jobExec.getExitStatus().equals(ExitStatus.COMPLETED)) {
				return new ResponseEntity<String>("Job is Completed Successfully", HttpStatus.OK);
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return new ResponseEntity<String>("Job is Failed...Either correct it and return Job....",
				HttpStatus.NOT_ACCEPTABLE);
	}
}
