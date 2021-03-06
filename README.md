
# This Fork

This Fork is focused on swagger.


# (Original) README #

[![Build Status](https://semaphoreci.com/api/v1/projects/4398be27-000e-497f-af5a-e2127a383d1a/753049/badge.svg)](https://semaphoreci.com/uniknow/docmockrest)

[DocMockRest](http://uniknow.bitbucket.org/DocMockRest/) is a simple standalone server to mock RESTful API services specified by a RAML specification file or by swagger annotated classes. This system allows you to quickly define a REST API and have them return JSON, XML or any other type of response.

*TODO:*

* Mocked services based on swagger annotated classes are currently returning always HTTP status `501`. This response (and the body) can be changed programmatically via the exposed Java API. Instead of this it should be checked whether examples are provided and if so use those. Questions that need to be addressed is what should be done if multiple examples are provided.
* Currently path parameters are replaced by regular expression that accepts any character. Instead of this the type of the parameter should be checked and based on that the proper regular expression should be inserted in the URL.

### How do I get it running? ###

#### Prerequisites

The [DocMockRest](http://uniknow.bitbucket.org/DocMockRest/) tool requires Java 1.8 and if you are planning to build the system you also need Maven 3.3 or above.

#### Building system

1.  Get the source code from [BitBucket](https://bitbucket.org/uniknow/docmockrest)
1.  Build complete jar by executing `mvn clean install`. The result will be `DocMockRest-<version>-full.jar` within the target directory. 

#### Running system

The system can be started by executing the following instruction within the directory where the `DocMockRest-<version>-full.jar` exist:
 
    java -jar DocMockRest-<version>-full.jar -raml <RAML file> || -swagger <package> -port <Port Number> -responses <location response files>
    
The `-raml` parameter specifies the location of the RAML file we want to mock. The `-port` parameter specifies the port on which the mocked API will run (default port 80), and the `-responses` parameter specifies the location where the response files reside.

The structure of the response files matches the URI of the resources within the RAML file; eg, the response for `http://localhost:80/example` will be at `<location response files>/example/response.<content type>`. The extension of the response file will match the `content-type` of the resource. 

To check which calls are mocked by the server you can invoke `http://localhost:80/__admin`

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* [UniKnow](mailto:uniknow.info@gmail.com)
* [Issue reporting](https://bitbucket.org/uniknow/docmockrest/issues)