/**
 * 
 */
package com.laxman.springbatch.writer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transaction;

import org.hibernate.SessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.laxman.springbatch.domain.Contact;

/**
 * @author ledara
 *
 */
@Repository
@Transactional
public class BatchItemWriter implements ItemWriter<Contact> {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void write(List<? extends Contact> items) throws Exception {
		for(Contact contact: items) {
			em.persist(contact);
		}
		em.flush();
		
	}

}
