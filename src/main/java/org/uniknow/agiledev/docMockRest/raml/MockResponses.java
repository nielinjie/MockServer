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
package org.uniknow.agiledev.docMockRest.raml;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.raml.model.Raml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;
import org.uniknow.agiledev.docMockRest.IoUtil;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

/**
 * Checks whether response for request is defined and it not returns default
 * response.
 */
@Validated
public class MockResponses extends ResponseTransformer {

    private final static Logger LOG = LoggerFactory
        .getLogger(MockResponses.class);

    private final String pathResponseFiles;

    @NotNull
    private final Raml specification;

    /**
     * Constructor
     * 
     * @param responseFiles
     *            - Path to directory in which response files are defined.
     */
    public MockResponses(Raml specification, String responseFiles) {
        this.specification = specification;
        pathResponseFiles = responseFiles;
    }

    private String getExtensionResponseFile(Request request) {

        String responseExtenstion;
        ContentTypeHeader contentType = request.contentTypeHeader();
        if (contentType != null) {
            try {
                switch (contentType.mimeTypePart()) {
                case MediaType.APPLICATION_JSON:
                    responseExtenstion = "json";
                    break;
                case MediaType.TEXT_XML:
                case MediaType.APPLICATION_XML:
                    responseExtenstion = "xml";
                    break;
                case MediaType.TEXT_HTML:
                    responseExtenstion = "html";
                    break;
                case MediaType.TEXT_PLAIN:
                    responseExtenstion = "txt";
                    break;
                default:
                    responseExtenstion = "txt";
                }
            } catch (NullPointerException err) {
                responseExtenstion = "txt";
            }
        } else {
            responseExtenstion = "txt";
        }

        return responseExtenstion;
    }

    /**
     * TODO
     * 
     * @param request
     * @param responseDefinition
     * @param fileSource
     * @return
     */
    @Override
    public ResponseDefinition transform(Request request,
        ResponseDefinition responseDefinition, FileSource fileSource) {
        // TODO: Check whether there is reponse defined for request if not use
        // passed response definition.
        LOG.debug("Processing request for '{}'", request.getUrl());

        if (pathResponseFiles != null) {
            // Determine extension of response file
            String responseExtenstion = getExtensionResponseFile(request);

            // Check whether response for specific request is defined
            Path pathResponseFile = Paths.get(pathResponseFiles,
                request.getUrl(), "response." + responseExtenstion);
            if (Files.exists(pathResponseFile)) {
                LOG.debug("Response defined for '{}' : {}", request.getUrl(),
                    pathResponseFile);
                try {
                    InputStream in = Files.newInputStream(pathResponseFile);
                    String response = IoUtil.convertStreamToString(in);

                    return ResponseDefinitionBuilder.like(responseDefinition)
                        .but().withBody(response).build();

                } catch (IOException error) {
                    return ResponseDefinitionBuilder
                        .like(responseDefinition)
                        .but()
                        .withHeader(HttpHeaders.CONTENT_TYPE,
                            MediaType.TEXT_PLAIN).withBody(error.getMessage())
                        .build();

                }
            }
        }

        if (responseDefinition.getBody() != null) {
            return ResponseDefinitionBuilder.like(responseDefinition).build();
        } else {
            return ResponseDefinitionBuilder.like(responseDefinition).but()
                .withStatus(HttpStatus.SC_NOT_FOUND)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .withBody("No mocked response found for " + request.getUrl())
                .build();
        }
    }

    /**
     * Validates response that will be returned
     * 
     * TODO
     * 
     * @param response
     *            String containing response
     * @return true if response valid; false otherwise
     */
    private boolean validateResponse(String response) {

        // If JSON response and JSON schema defined verify response
        // if (responseExtenstion == "json") {
        // // Get resource at which response applies
        // Resource resource = specification.getResource(request.getUrl());
        // if (resource != null) {
        // // Get schema that should be applied (Issue: how to determine
        // response code)
        // Action action = resource.getAction(request.getMethod().value());
        // String schema =
        // action.getResponses().get("response code").getBody().get(request.contentTypeHeader().firstValue()).getSchema();
        // JSONObject rawSchema = new JSONObject(new JSONTokener(schema));
        // Schema jsonSchema = SchemaLoader.load(rawSchema);
        //
        // jsonSchema.validate(new JSONObject(response));
        // } else {
        // System.out.println("Resource for request " + request.getUrl() +
        // " could not be found?");
        // }
        // }
        return true;
    }

    @Override
    public String name() {
        return "mock-responses";
    }

}
