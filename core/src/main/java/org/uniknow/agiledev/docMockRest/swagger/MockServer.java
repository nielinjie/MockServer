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
package org.uniknow.agiledev.docMockRest.swagger;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.Stubbing;
import com.github.tomakehurst.wiremock.stubbing.ListStubMappingsResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.jaxrs.Reader;
import io.swagger.models.*;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;
import org.uniknow.agiledev.docMockRest.SystemError;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Mock Server based on Swagger specification
 */
@Validated
public class MockServer {

    private final static Logger LOG = LoggerFactory.getLogger(MockServer.class);

    /*
     * Contains instance of created wire mock server
     */
    private WireMockServer wireMockServer;

    /**
     * Maps operation ID to matching stub
     */
    private final Map<String, MappingBuilder> stubs = new HashMap<>();

    /*
     * Contains Swagger configuration as mocked by this server
     */
    private Swagger specification;

    /**
     * Default constructor for testing purposes only
     */
    MockServer() {
    }

    /**
     * Constructor Mock Server
     * 
     * @param specificationFile
     *            Location of file containing Swagger definition on which mocks
     *            will be based
     * @param port
     *            Port on which mock server will be reachable.
     */
    MockServer(File specificationFile, int port) {
        // TODO
    }

    /**
     * Constructor Mock Server
     * 
     * @param prefix
     *            Package that need to be scanned for annotated classes
     * @param port
     *            Port on which mock server will be reachable.
     */
    MockServer(String prefix, int port) {
        LOG.info(
            "Starting MockServer using annotated classes within: {} on port: {}",
            prefix, port);

        Swagger specification = getSpecification(prefix);

        createMockServer(specification, port);
    }

    /**
     * Returns specification based on annotated classes.
     * 
     * @param prefix
     *            Package that need to be scanned for annotated classes
     * 
     * @return Swagger specification
     */
    private Swagger getSpecification(String prefix) {

        // Get all swagger annotated classes within specified package
        SwaggerAnnotationScanner scanner = new SwaggerAnnotationScanner();
        Set<Class<?>> resources = scanner.getResources(prefix);

        // Read all annotated classes and create rest specifications
        Swagger swagger = null;
        Reader reader = new Reader(swagger);
        swagger = reader.read(resources);

        return swagger;
    }

    /**
     * Creates server mocking REST APIs which are specified within swagger model
     * 
     * @param specification
     *            Swagger specification
     * @param port
     *            HTTP port on which mocket REST API can be reached.
     */
    void createMockServer(@NotNull Swagger specification, @Min(0) int port) {
        wireMockServer = new WireMockServer(wireMockConfig().port(port));
        wireMockServer.start();

        this.specification = specification;

        // Create stubs for resources within specification
        stubResources(specification);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Shutting down the mock server");
                wireMockServer.shutdown();
            }
        });
    }

    /**
     * Returns stub for specified operation
     * 
     * @param operationID
     *            Identifier of operation for which we want to retrieve Stub
     * 
     * @return Stub for specified operation
     */
    public MappingBuilder when(@NotNull @NotEmpty @NotBlank String operationID) {
        // Get URL of operation
        if (stubs.containsKey(operationID)) {
            return stubs.get(operationID);
        } else {
            throw new NotFoundException(
                "Specified operation could not be found");
        }
    }

    /**
     * Updates defintion for stub
     */
    public void stubFor(@NotNull MappingBuilder stub) {
        wireMockServer.stubFor(stub);
    }

    /**
     * Stub the passed resources recursively.
     * 
     * @param specification
     *            Swagger specification
     */
    private void stubResources(Swagger specification) {

        for (Map.Entry<String, Path> paths : specification.getPaths()
            .entrySet()) {
            System.out.println("Processing resource " + paths.getKey());
            stubResource(paths.getKey(), paths.getValue());
        }

    }

    private void stubResource(String url, Path path) {
        stubOperation(HttpMethod.GET, url, path.getGet());
        stubOperation(HttpMethod.PUT, url, path.getPut());
        stubOperation(HttpMethod.POST, url, path.getPost());
        stubOperation(HttpMethod.DELETE, url, path.getDelete());
    }

    private void stubOperation(HttpMethod method, String url,
        Operation operation) {
        if (operation != null) {
            System.out.println("Creating stub for [" + method + "]:" + url);

            // Replace path parameters in URL by proper regular expressions.
            Map<String, String> pathParameters = new HashMap<>();
            for (Parameter parameter : operation.getParameters()) {
                System.out
                    .println("Verifying parameter " + parameter.getName());
                if (parameter.getIn().equalsIgnoreCase("path")) {
                    System.out.println("Adding " + parameter.getName()
                        + " to path parameters");
                    PathParameter pathParameter = (PathParameter) parameter;
                    pathParameters.put(parameter.getName(),
                        pathParameter.getType());
                }
            }
            for (String parameterName : pathParameters.keySet()) {
                // TODO: Replace . (match any character) by proper regular
                // expression based on type.
                url = url.replace("{" + parameterName + "}", ".");
            }

            MappingBuilder stub;
            switch (method) {
            case GET:
                stub = get(urlMatching(url));
                break;

            case POST:
                stub = post(urlMatching(url));
                break;

            case PUT:
                stub = put(urlMatching(url));
                break;

            case DELETE:
                stub = delete(urlMatching(url));
                break;

            default:
                LOG.warn("[" + method + "]" + url + " is not supported yet");
                return;
            }

            // Add stub to dictionary for later retrieval
            System.out.println("Adding stub for operation "
                + operation.getOperationId());
            stubs.put(operation.getOperationId(), stub);

            // Create default stub for operation
            wireMockServer.stubFor(stub.willReturn(aResponse()
                .withStatus(HttpStatus.SC_NOT_IMPLEMENTED)
                .withHeader("Content-Type", "text/plain")
                .withHeader("Cache-Control", "no-cache")
                .withBody("No mocked response defined yet")));
        }
    }

}
