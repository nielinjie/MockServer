/**
 * Copyright (C) 2016 UniKnow (info.uniknow@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uniknow.agiledev.docMockRest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.docMockRest.swagger.SwaggerMockServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class responsible for loading responses in json file
 */
public class JsonIOResponsesMappingsLoader implements MappingsLoader {

    private final static Logger LOG = LoggerFactory
        .getLogger(SwaggerMockServer.class);

    /*
     * Contains location of file containing stub responses
     */
    private final InputStream locationResponsesFile;

    /**
     * TODO: Make it RAML/SWAGGER independent.
     */
    private final SwaggerMockServer mockServer;

    /**
     * Constructor
     * 
     * @param locationResponsesFile
     *            Location of file containing stub responses.
     */
    public JsonIOResponsesMappingsLoader(SwaggerMockServer mockServer,
        String locationResponsesFile) throws FileNotFoundException {
        this(mockServer, new FileInputStream(locationResponsesFile));
    }

    public JsonIOResponsesMappingsLoader(SwaggerMockServer mockServer,
        URL locationResponsesFile) throws IOException {
        this(mockServer, locationResponsesFile.openStream());
    }

    public JsonIOResponsesMappingsLoader(SwaggerMockServer mockServer,
        InputStream responsesFile) {
        this.locationResponsesFile = responsesFile;
        this.mockServer = mockServer;
    }

    @Override
    public void loadMappingsInto(StubMappings stubMappings) throws SystemError {

        // JsonStubMappingCreator jsonStubMappingCreator = new
        // JsonStubMappingCreator(
        // stubMappings);

        try {
            ObjectMapper mapper = new ObjectMapper();
            StubMapping[] mappings = mapper.readValue(locationResponsesFile,
                StubMapping[].class);

            for (StubMapping mapping : mappings) {
                // Check whether operation exist for specified stub response
                // NOTE 由于stubmapping（wiremock定义）里面没有operationid，只能用request pattern获取。
                // 考虑分别处理，在不需要response文件的时候使用operationid，
                // 是不是不要response文件的时候根本就不需要getOperation？ - 貌似是的。
                if (mockServer.getOperation(mapping.getRequest()) != null) {


                    ResponseDefinition response = mapping.getResponse();

                    // When body file specified load response and put in body.
                    // Reason for this is that responses are within jar and
                    // wiremock is not able to handle those correctly
                    if (response != null && response.getBodyFileName() != null) {
                        URL locationResponseBodyFile = getClass()
                            .getClassLoader().getResource(
                                response.getBodyFileName());
                        if (locationResponseBodyFile != null) {
                            LOG.info("Reading response body from {}",
                                locationResponseBodyFile);
                            mapping.setResponse(ResponseDefinitionBuilder
                                .like(response)
                                .withBody(
                                    IOUtils.toString(locationResponseBodyFile
                                        .openStream())).withBodyFile(null)
                                .build());
                        } else {
                            throw new SystemError("Can't find body file "
                                + response.getBodyFileName());
                        }
                    }

                    stubMappings.addMapping(mapping);
                } else {
                    throw new SystemError(
                        "Attempting to create stub for non existing operation ('"
                            + mapping.getRequest() + "')");
                }
            }
        } catch (IOException error) {
            throw new SystemError(error);
        }
    }
}
