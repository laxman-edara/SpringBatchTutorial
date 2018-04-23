/**
 * 
 */
package com.laxman.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.laxman.springbatch.domain.Contact;

/**
 * @author ledara
 * @param <O>
 * @param <I>
 *
 */
public class BatchItemProcessor implements ItemProcessor<Contact, Contact> {


	@Override
	public Contact process(Contact item) throws Exception {
		System.out.println("In processor");
		return item;
	}

}
