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
import io.swagger.jaxrs.Reader;
import io.swagger.models.*;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.File;
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
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig()
            .port(port));
        wireMockServer.start();

        // Create stubs for resources within specification
        stubResources(wireMockServer, specification);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Shutting down the mock server");
                wireMockServer.shutdown();
            }
        });
    }

    /**
     * Stub the passed resources recursively.
     * 
     * @param wireMockServer
     *            Server that will be used to mock the resources
     * @param specification
     *            Swagger specification
     */
    final void stubResources(@NotNull WireMockServer wireMockServer,
        @NotNull Swagger specification) {

        for (Map.Entry<String, Path> paths : specification.getPaths()
            .entrySet()) {
            System.out.println("Processing resource " + paths.getKey());
            stubResource(wireMockServer, paths.getKey(), paths.getValue());
        }

    }

    private void stubResource(WireMockServer wireMockServer, String url,
        Path path) {
        stubOperation(wireMockServer, HttpMethod.GET, url, path.getGet());
        stubOperation(wireMockServer, HttpMethod.PUT, url, path.getPut());
        stubOperation(wireMockServer, HttpMethod.POST, url, path.getPost());
        stubOperation(wireMockServer, HttpMethod.DELETE, url, path.getDelete());
    }

    private void stubOperation(WireMockServer wireMockServer,
        HttpMethod method, String url, Operation operation) {
        if (operation != null) {
            System.out.println("Creating stub for [" + method + "]:" + url);

            MappingBuilder urlMatcher;
            switch (method) {
            case GET:
                urlMatcher = get(urlMatching(url));
                break;

            case POST:
                urlMatcher = post(urlMatching(url));
                break;

            case PUT:
                urlMatcher = put(urlMatching(url));
                break;

            case DELETE:
                urlMatcher = delete(urlMatching(url));
                break;

            default:
                LOG.warn("[" + method + "]" + url + " is not supported yet");
                return;
            }

            wireMockServer.stubFor(urlMatcher.willReturn(aResponse()
                .withStatus(HttpStatus.SC_NO_CONTENT)));
        }
    }

    /**
     * Create response for Operation
     */

}
