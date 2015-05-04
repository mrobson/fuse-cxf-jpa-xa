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
package org.mrobson.example.distributedtx.cxfjpa.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.mrobson.example.distributedtx.cxfjpa.persistence.PersonService;
import org.mrobson.example.distributedtx.datamodel.Person;
import org.mrobson.example.distributedtx.cxfjpa.exception.ApplicationException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.ApiResponse;

/**
 * @author <a href="mailto:mrobson@redhat.com">Matthew Robson</a>
 * 
 * Apr 30, 2015
 */
@Path(PersonService.SERVICE_NAME)
@Api(value = PersonService.SERVICE_NAME, description = "Person Based Operations")
public class PersonRestService {
	private static final Logger LOG = LoggerFactory.getLogger(PersonRestService.class);
	
	private PersonService personService;

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	@POST
    @Path("/addPerson/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Adding a new Person")
    @ApiResponses(value = { 
    	@ApiResponse(code = 200, message = "The person has been saved"),
    	@ApiResponse(code = 400, message = "Persistence exception"),
    	@ApiResponse(code = 500, message = "Invalid Person") 
    })
	public Response savePerson(Person ps) throws ApplicationException {
		LOG.info("Invoking savePerson, Person name is: {}", ps.getFirstName());
		Person savedPs = null;
		try {
			savedPs = personService.savePersonWS(ps);
		}  catch (Exception e) {
	         throw new ApplicationException("Could not save person: " + ps.toString() + " Cause: " + ExceptionUtils.getCause(e) + " Error: " + ExceptionUtils.getRootCauseMessage(e));
		}
        return Response.ok().entity(savedPs).build();
	}
	
    @GET
    @Path("/findById/{id}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Find Person by ID", notes = "This method returns a fully formed Person Entity based on the provided ID", response = Person.class)
    @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Found the right person"),
      @ApiResponse(code = 400, message = "No person could be found"),
      @ApiResponse(code = 500, message = "The supplied ID is invalid")
    })
    public Person getPerson(@ApiParam(value = "ID of Customer to fetch", required = true) @PathParam("id") String id) throws ApplicationException {
    	LOG.info("Invoking getPerson with the id: {}", id);
        long personId = Long.parseLong(id);
        Person ps = personService.getPersonByIdWS(personId);
		if (ps == null) {
			throw new ApplicationException("Could not find a person with id: " + personId + ".");
		}
		return ps;
    }
}