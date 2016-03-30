# README #

This README document the steps which are necessary to get DocMockRest up and running.

DocMockRest is a simple standalone server to mock RESTful API services specified by a RAML specification file. This system allows you to quickly define a REST API and have them return JSON, XML or any other type of response.

### How do I get it running? ###

#### Prerequisites

The DocMockRest tool requires Java 1.8 and if you are planning to build the system you also need Maven 3.

##### Building system

. Get the source code from [BitBucket](https://bitbucket.org/uniknow/docmockrest)
. Build complete jar by executing `mvn clean install`. The result will be `DocMockRest-<version>-full.jar` within the target directory. 

##### Running system

The system can be started by executing the following instruction within the directory where the `DocMockRest-<version>-full.jar` exist:
 
    java -jar DocMockRest-<version>-full.jar -r <RAML file> -p <Port Number> -responses <location response files>
    
The `-r` parameter specifies the location of the RAML file we want to mock. The `-p` parameter specifies the port on which the mocked API will run, and the `-responses` parameter specifies the location where the response files reside. 

The structure of the response files matches the URI of the resources within the RAML file; eg, the response for `http://localhost:80/example` will be at `<location response files>/example/response.<content type>`. The extension of the response file will match the `content-type` of the resource. 

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* [UniKnow](mailto:uniknow.info@gmail.com)
* TODO: Setup jira