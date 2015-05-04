/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mrobson.example.distributedtx.cxfjpa.persistence.dao;

import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.mrobson.example.distributedtx.datamodel.Person;

/**
 * @author <a href="mailto:mrobson@redhat.com">Matthew Robson</a>
 * 
 * Apr 30, 2015
 */
public class BaseDao implements Dao {
	private EntityManager entityManager;
	private static final Logger log = Logger.getLogger(BaseDao.class);

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Person savePerson(Person entity) {
		log.debug("Saving: " + entity);
		entity = entityManager.merge(entity);
		return entity;
	}

	public Person getPersonById(long id) {
		log.debug("Fidning: " + id);
		Person entity = entityManager.find(Person.class, id);
		return entity;
	}
}