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
package org.mrobson.example.distributedtx.cxfjpa.persistence.rest.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mrobson.example.distributedtx.cxfjpa.persistence.PersonService;
import org.mrobson.example.distributedtx.datamodel.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author <a href="mailto:mrobson@redhat.com">Matthew Robson</a>
 * 
 * Apr 30, 2015
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class PersonTest extends BaseTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonTest.class);

	@Test
    public void addPersonTest() throws Exception {
        LOG.info("POST request to add a new Person");
        String inputFile = this.getClass().getResource("/newperson.json").getFile();
        File input = new File(inputFile);
        PostMethod post = new PostMethod(getBaseURL(PersonService.SERVICE_NAME) + "addPerson");
        post.addRequestHeader("Accept", "application/json");
        RequestEntity entity = new FileRequestEntity(input, "application/json; charset=ISO-8859-1");
        post.setRequestEntity(entity);
        HttpClient httpclient = new HttpClient();
        String response = null;
        try {
            int result = httpclient.executeMethod(post);
            LOG.info("Response status code: " + result);
            response = post.getResponseBodyAsString();
            LOG.info("Response body: " + response);
        } catch (IOException e) {
            LOG.error("Connection error to {}", getBaseURL(PersonService.SERVICE_NAME) + "addPerson");
            Assert.fail("Connection error");
        } finally {
            post.releaseConnection();
        }
        ObjectMapper mapper = new ObjectMapper();
		Person p = mapper.readValue(response, Person.class);
        Assert.assertTrue(response.contains(p.getFirstName()));
        getAddedPersonTest(p.getId());
    }
    
    public void getAddedPersonTest(Long personId) throws Exception {
        LOG.info("GET request to find Person by ID");
        HttpClient httpclient = new HttpClient();
        GetMethod get = new GetMethod(getBaseURL(PersonService.SERVICE_NAME) + "findById/" + personId);
        get.setRequestHeader("Accept", "application/json");
        try {
        	int httpResponse = httpclient.executeMethod(get);
            LOG.info("Response status code: " + httpResponse);
            LOG.info("Response body: " + get.getResponseBodyAsString());
        } catch (IOException e) {
            LOG.error("Connection error to {}", getBaseURL(PersonService.SERVICE_NAME) + "/findById/{id}");
            Assert.fail("Connection error");
        } finally {
            get.releaseConnection();
        }
        Assert.assertTrue(get.getResponseBodyAsString().contains(Long.toString(personId)));
    }
}