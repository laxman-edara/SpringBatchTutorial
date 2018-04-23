/**
 * 
 */
package com.laxman.springbatch.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.laxman.springbatch.domain.Contact;
import com.laxman.springbatch.listener.JobNotificationListener;
import com.laxman.springbatch.processor.BatchItemProcessor;
import com.laxman.springbatch.writer.BatchItemWriter;

/**
 * @author ledara
 *
 */
@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
@PropertySources(value = { @PropertySource(value = "environment.properties") })
public class ApplicationConfig {

	@Autowired
	Environment env;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Value("${spring.parsing.delimiter}")
	private String delimiterValue;

	@Value(value = "${spring.parsing.jobName}")
	private String processJobName;

	@Bean
	public LineMapper<Contact> lineMapper() {
		DefaultLineMapper<Contact> lineMapper = new DefaultLineMapper<Contact>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(delimiterValue);
		lineTokenizer.setNames(new String[] { "id", "firstName", "lastName", "email", "dateOfBirth" });
		BeanWrapperFieldSetMapper<Contact> fieldSetMapper = new BeanWrapperFieldSetMapper<Contact>();
		fieldSetMapper.setTargetType(Contact.class);
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

	@Bean
	public FlatFileItemReader<Contact> batchItemReader() {
		FlatFileItemReader<Contact> itemReader = new FlatFileItemReader<Contact>();
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	@Bean
	public BatchItemProcessor batchItemProcessor() {
		return new BatchItemProcessor();
	}

	@Bean
	public BatchItemWriter writer() {
		return new BatchItemWriter();
	}

	@Bean
	public Step batchStep1() {
		return stepBuilderFactory.get("step1").<Contact, Contact>chunk(2).reader(batchItemReader())
				.processor(batchItemProcessor()).writer(writer()).build();
	}

	@Bean(name = "jobBuilder")
	public JobBuilder jobBuilder(JobNotificationListener listener, Step step1) {
		return jobBuilderFactory.get(processJobName).incrementer(new RunIdIncrementer()).listener(listener);
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("driverClassName"));
		dataSource.setUrl(env.getProperty("driverURL"));
		dataSource.setUsername(env.getProperty("dbUserName"));
		dataSource.setPassword(env.getProperty("dbPassword"));
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[]{"com.laxman.springbatch.domain"});

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalJpaProperties());

		return em;
	}

	Properties additionalJpaProperties() {
		Properties properties = new Properties();
		// properties.setProperty("hibernate.hbm2ddl.auto", "validate");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		properties.setProperty("hibernate.show_sql", "true");

		return properties;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
