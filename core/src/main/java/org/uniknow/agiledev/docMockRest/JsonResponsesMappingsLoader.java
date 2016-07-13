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
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.stubbing.JsonStubMappingCreator;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
import org.uniknow.agiledev.docMockRest.swagger.SwaggerMockServer;

import java.io.File;
import java.io.IOException;

/**
 * Class responsible for loading responses in json file
 */
public class JsonResponsesMappingsLoader implements MappingsLoader {

    /*
     * Contains location of file containing stub responses
     */
    private final String locationResponsesFile;

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
    public JsonResponsesMappingsLoader(SwaggerMockServer mockServer,
        String locationResponsesFile) {
        this.locationResponsesFile = locationResponsesFile;
        this.mockServer = mockServer;
    }

    @Override
    public void loadMappingsInto(StubMappings stubMappings) throws SystemError {

        JsonStubMappingCreator jsonStubMappingCreator = new JsonStubMappingCreator(
            stubMappings);

        try {
            ObjectMapper mapper = new ObjectMapper();
            StubMapping[] mappings = mapper.readValue(new File(
                locationResponsesFile), StubMapping[].class);

            for (StubMapping mapping : mappings) {
                // Check whether operation exist for specified stub response
                if (mockServer.getOperation(mapping.getRequest()) != null) {
                    stubMappings.addMapping(mapping);
                } else {
                    throw new SystemError(
                        "Attempting to create stub for non existing operation");
                }
            }
        } catch (IOException error) {
            throw new SystemError(error);
        }
    }
}
