CXF JPA XA: demonstrates RESTful web services with CXF and JPA Persistence with XA Transactions
===============================================================================================
Author: Matt Robson 

Technologies: Fuse, OSGi, CXF, OpenJPA, Blueprint, Karaf Features, Fuse BOM, Aries Auto Enlist XA Transactions, Swagger, JSON, ExceptionMapper 

Product: Fuse 6.1 

Breakdown
---------
This code example shows how to leverage RESTful (JAX-RS) web services using CXF and persist Entities using OpenJPA. It demonstrates how to wire an EntityManager to a JPA Context and on to a Service. It also makes use of Aries Auto Enlistment for XA Transactions.  Other interesting aspects include the use of the Fuse BOM, Karaf Features, Blueprint Placeholders, Swagger and  ExceptionMapper.

For more information see:

* <https://access.redhat.com/site/documentation/JBoss_Fuse/> for more information about using JBoss Fuse

System Requirements
-------------------
Before building and running this quick start you need:

* Maven 3.2 or higher
* Java 1.7
* JBoss Fuse 6.1

Build and Deploy
----------------

1) clone this project

	git clone https://github.com/mrobson/fuse-cxf-jpa-xa.git

2) change to project directory 

	cd fuse-cxf-jpa-xa

3) update your username and password

	vi xa-datasource/src/main/resources/OSGI-INF/blueprint/datasource.xml
	<cm:property name="datasource.username" value="username" />
	<cm:property name="datasource.password" value="password" />

4) build

	mvn clean install

5) start JBoss Fuse 6.1

	./fuse or ./start

6) start Oracle database (refer to vendor documentation if you need to do this, for testing I recommend using Oracle XE)

7) deploy Oracle JDBC driver

Download the latest driver from Oracle and install it to your local maven repository (account required):

	mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=12.1.0.1 -Dpackaging=jar

From the Karaf console:

	osgi:install -s wrap:mvn:com.oracle/ojdbc6/12.1.0.1

8) add the features file

	features:addurl mvn:org.mrobson.example.distributedtx/features/1.0-SNAPSHOT/xml/features

9) install

	features:install distributedtx-jpa-example

10) verify

	osgi:list
	[ 819] [Active     ] [Created     ] [       ] [  100] distributedtx :: XA-Datasource (1.0.0.SNAPSHOT)
	[ 820] [Active     ] [            ] [       ] [  110] distributedtx :: Datamodel (1.0.0.SNAPSHOT)
	[ 821] [Active     ] [Created     ] [       ] [  120] distributedtx :: CXF JPA (1.0.0.SNAPSHOT)

Usage
-----

You can verify your CXF web services are available at:

	http://localhost:8181/cxf

With a correct deployment, this example has an endpoint address of:

	http://localhost:8181/cxf/jpatx/

To view the WADL, you can use the following address:

	http://localhost:8181/cxf/jpatx/?_wadl

As a quick test, you can use the GET operations directly from the browser:

	http://localhost:8181/cxf/jpatx/person/findById/1

Run a Test
----------

1) change to cxfjpa directory

	cd fuse-cxf-jpa-xa/cxfjpa

2) run included test which persists a person and then looks up that person

mvn -Dtest=PersonTest test

Note: reference json file can be found at: cxfjpa/src/test/resources/person.json

Make it Fail
------------

To test transaction rollback (and see the ExceptionMapper in action) , you can make a request fail.

From fuse-cxf-jpa-xa/cxfjpa:

	vi src/test/resources/person.json

Delete the "firstName" value to make it blank/null:

	{"id":null,"version":null,"firstName":"","lastName":"R","country":"Canada","addresses":[{"id":null,"version":null,"city":"Toronto"}]}

You will see a HTTP 400 returned along with a detailed description of the issue:

	Response-Code: 400
	Content-Type: application/json
	Headers: {Content-Type=[application/json], Date=[Thu, 30 Apr 2015 21:40:04 GMT]}
	Payload: Could not save person: [FirstName= LastName=R Country=Canada Addresses=[[City=Toronto]]] Cause: javax.transaction.RollbackException: Unable to commit: transaction marked for rollback Error: SQLIntegrityConstraintViolationException: ORA-01400: cannot insert NULL into ("PRODUCTCONFIG"."PERSON"."FIRSTNAME")

Remove the Services
-------------------

To remove the bundle we installed, you can simply uninstall the feature:

1) uninstall the feature

	features:uninstall distributedtx-jpa-example
