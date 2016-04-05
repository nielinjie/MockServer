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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import org.apache.http.HttpHeaders;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.loader.FileResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;

@Validated
public class MockServer {
    private final static Logger log = LoggerFactory.getLogger(MockServer.class);

    /**
     * Default constructor for testing purposes only
     */
    MockServer() {
    }

    /**
     * Constructor Mock Server
     * 
     * @param specificationFile
     *            Location of file containing RAML definition on which mocks
     *            will be based
     * @param port
     *            Port on which mock server will be reachable.
     * @param responseFiles
     *            Location of files containing responses for mocked resources
     * @throws FileNotFoundException
     *             if specification file doesn't exist
     */
    public MockServer(String specificationFile, int port, String responseFiles)
        throws FileNotFoundException {
        log.info("Starting MockServer using RAML file: {} on port: {}",
            specificationFile, port);

        Raml raml = getSpecification(specificationFile);

        createMockServer(raml, port, responseFiles);
    }

    /**
     * Generates HTML documentation for specified RAML file.
     * 
     * @param specification
     *            RAML specification that will be converted into HTML file
     * 
     * @return
     */
    private String generateHtml(Raml specification) {
        String ramlHtml = new Raml2HtmlRenderer(specification).renderFull();
        return ramlHtml;
    }

    /**
     * Creates server mocking REST APIs which are specified within RAML model
     * 
     * @param specification
     *            RAML specification
     * @param port
     *            HTTP port on which mocket REST API can be reached.
     */
    void createMockServer(@NotNull Raml specification, @Min(0) int port,
        String responseFiles) {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig()
            .port(port).withRootDirectory(responseFiles)
            .extensions(new MockResponses(specification, responseFiles)));
        wireMockServer.start();

        // Create stub returning info regarding mocked interfaces.
        wireMockServer.stubFor(get(urlEqualTo("/info")).willReturn(
            aResponse().withHeader(HttpHeaders.CONTENT_TYPE,
                MediaType.TEXT_HTML).withBody(
                new Raml2HtmlRenderer(specification).renderFull())));

        final Collection<Resource> resources = specification.getResources()
            .values();

        stubResources(wireMockServer, resources);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutting down the mock server");
                wireMockServer.shutdown();
            }
        });
    }

    /**
     * Returns REST API specification.
     * 
     * @param specificationFile
     *            Location of RAML file that contains REST API specification
     * @return Rest API specification in the form of RAML model.
     * @throws FileNotFoundException
     *             if specification file could not be found.
     */
    @NotNull
    Raml getSpecification(@NotNull String specificationFile)
        throws FileNotFoundException {
        log.debug("Loading specification file '{}'", specificationFile);
        File file = new File(specificationFile);
        if (!file.exists()) {
            log.error("Specification file {} does not exists!",
                specificationFile);
            throw new FileNotFoundException("Specification file '"
                + specificationFile + "' could not be found.");
        }

        if (file.isDirectory()) {
            log.error("{} is a directory!", specificationFile);
            throw new IllegalArgumentException("'" + specificationFile
                + "' is a directory.");
        }

        Raml raml = new RamlDocumentBuilder(new FileResourceLoader(file
            .getParentFile().getAbsolutePath())).build(
            new FileInputStream(file), "");
        log.info("Loaded specifications for REST API '{}' version '{}'",
            raml.getTitle(), raml.getVersion());

        return raml;
    }

    /**
     * Stub the passed resources recursively.
     * 
     * @param wireMockServer
     *            Server that will be used to mock the resources
     * @param resources
     *            Resources that will be mocked
     */
    final void stubResources(@NotNull WireMockServer wireMockServer,
        @NotNull Collection<Resource> resources) {
        for (Resource resource : resources) {
            log.info("stub {}", resource.getUri());

            if (hasAJsonBodyExample(resource)) {
                stubJsonBodyExample(wireMockServer, resource);
            }

            List<String> statusCodes = statusCodesThatHasExampleBody(resource,
                ActionType.GET);
            if (!statusCodes.isEmpty()) {
                // only one response per resource is possible
                stubJsonBodyExampleWithCode(wireMockServer, resource,
                    statusCodes.get(0), ActionType.GET);
            }

            List<String> statusCodesPost = statusCodesThatHasExampleBody(
                resource, ActionType.POST);
            if (!statusCodesPost.isEmpty()) {
                // only one response per resource is possible
                stubJsonBodyExampleWithCode(wireMockServer, resource,
                    statusCodesPost.get(0), ActionType.POST);
            }

            if (!resource.getResources().isEmpty()) {
                Collection<Resource> childResources = resource.getResources()
                    .values();
                stubResources(wireMockServer, childResources);
            }
        }
    }

    // TODO: now only get and post on resource with responses for status codes
    // in RAML
    private void stubJsonBodyExampleWithCode(WireMockServer wireMockServer,
        Resource resource, String statusCode, ActionType actionType) {
        String resourceMatch = replaceResourceIdWithAnyMatcher(resource);
        // log.debug("stub {},  status code: {} resourceMatch: {}",
        // resource.getUri(),
        // statusCode,
        // resourceMatch);

        // TODO: Requires that content-type header is set, should check whether
        // that can be defined in RAML file (mediaTypeExtension).
        MappingBuilder urlMatcher;
        switch (actionType) {

        case GET:
            urlMatcher = get(urlMatching(resourceMatch));
            break;

        case POST:
            urlMatcher = post(urlMatching(resourceMatch));
            break;

        default:
            log.warn("[" + actionType + "]" + resourceMatch
                + " is not supported yet");
            return;
        }

        // Create mock for resource
        wireMockServer.stubFor(urlMatcher.withHeader(HttpHeaders.CONTENT_TYPE,
            equalTo("application/json")).willReturn(
            aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .withStatus(Integer.parseInt(statusCode))
                .withBody(
                    resource.getAction(actionType).getResponses()
                        .get(statusCode).getBody().get("application/json")
                        .getExample())));
    }

    // TODO: now only get on resource without responses for status codes in RAML
    private void stubJsonBodyExample(WireMockServer wireMockServer,
        Resource resource) {
        // String resourceMatch = replaceResourceIdWithAnyMatcher(resource);
        // log.debug("stubJsonBodyExample:{} resourceMatch: {}:",
        // resource.getUri(), resourceMatch);

        log.debug("stub [GET]" + resource.getUri());
        wireMockServer.stubFor(get(urlEqualTo(resource.getUri())).withHeader(
            HttpHeaders.CONTENT_TYPE, equalTo("application/json")).willReturn(
            aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .withBody(
                    resource.getAction(ActionType.GET).getBody()
                        .get("application/json").getExample())));
    }

    /**
     * Replaces {id} within URI of resource by id
     * 
     * @param resource
     * @return
     */
    private String replaceResourceIdWithAnyMatcher(Resource resource) {
        return resource.getUri().replaceAll("\\{[0-9a-zA-Z]*\\}",
            "[0-9a-zA-Z.]*");
    }

    private List<String> statusCodesThatHasExampleBody(Resource resource, ActionType actionType) {

        List<String> statusCodes = Arrays.asList("200", "201", "202", "204", "400", "401", "403", "404", "405", "409");

        return statusCodes.stream().filter(s -> hasAJsonBodyExampleByStatusCode(resource, s, actionType))
                .peek(p -> log.debug("StatusCode match with example body: {}", p))
                .collect(
                        Collectors.toList());
    }

    private boolean hasAJsonBodyExampleByStatusCode(Resource resource,
        String statusCode, ActionType actionType) {
        return resource.getAction(actionType) != null
            && !resource.getAction(actionType).hasBody()
            && resource.getAction(actionType).getResponses() != null
            && resource.getAction(actionType).getResponses()
                .containsKey(statusCode)
            && resource.getAction(actionType).getResponses().get(statusCode)
                .hasBody()
            && resource.getAction(actionType).getResponses().get(statusCode)
                .getBody().containsKey("application/json")
            && resource.getAction(actionType).getResponses().get(statusCode)
                .getBody().get("application/json").getExample() != null;
    }

    private boolean hasAJsonBodyExample(Resource resource) {
        return resource.getAction(ActionType.GET) != null
            && resource.getAction(ActionType.GET).hasBody()
            && resource.getAction(ActionType.GET).getBody()
                .containsKey("application/json")
            && resource.getAction(ActionType.GET).getBody()
                .get("application/json").getExample() != null;
    }
}
