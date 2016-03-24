package org.uniknow.agiledev.docMockRest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
public class MockServer {
    private final static Logger log = LoggerFactory.getLogger(MockServer.class);

    /**
     * Default constructor for testing purposes only
     */
    MockServer() {}

    /**
     * Constructor Mock Server
     *
     * @param ramlfile File containing RAML definition on which mocks will be based
     * @param port Port on which mock server will be reachable.
     */
    public MockServer(@NotNull String ramlfile, int port) {
        log.info("Starting MockServer using RAML file: {} on port: {}", ramlfile, port);

        Raml raml = getSpecification(ramlfile);

        createMockServer(raml, port);
    }

    /**
     * Creates server mocking REST APIs which are specified within RAML model
     */
    void createMockServer(@NotNull Raml specification, @Min(0) int port) {
        WireMockServer wireMockServer = new WireMockServer(
                wireMockConfig().port(port));
        wireMockServer.start();
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Hello world!")));

        final Collection<Resource> resources = specification.getResources().values();

        stubResourcesRecursive(wireMockServer, resources);

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
     * @param ramlFileName Name of RAML file that contains REST API specification
     *
     * @return Rest API specification in the form of RAML model.
     */
    Raml getSpecification(@NotNull String ramlFileName) {
        // TODO: Should also be possible to load RAML file from path on system.
        URL url = getClass().getClassLoader().getResource(ramlFileName);
        if (url == null) {
            log.error("File: {} does not exists!", ramlFileName);
            System.exit(1);
        }
        log.debug("URL for the resource: {}", url.toString());

        if (new File(url.getPath()).isDirectory()) {
            log.error("{} is a directory!", ramlFileName);
            System.exit(1);
        }

        Raml raml = new RamlDocumentBuilder().build(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(ramlFileName), ramlFileName);
        log.info("RAML Title: {} version: {}", raml.getTitle(), raml.getVersion());

        return raml;
    }

    private void stubResourcesRecursive(WireMockServer wireMockServer, Collection<Resource> resources) {
        for (Resource resource : resources) {
            log.debug("Relative uri:{}", resource.getRelativeUri());

            if (hasAJsonBodyExmple(resource)) {
                stubJsonBodyExmple(wireMockServer, resource);
            }

            List<String> statusCodes = statusCodesThatHasExampleBody(resource, ActionType.GET);
            if (!statusCodes.isEmpty()) {
                // only one response per resource is possible
                stubJsonBodyExmpleWithCode(wireMockServer, resource, statusCodes.get(0), ActionType.GET);
            }

            List<String> statusCodesPost = statusCodesThatHasExampleBody(resource, ActionType.POST);
            if (!statusCodesPost.isEmpty()) {
                // only one response per resource is possible
                stubJsonBodyExmpleWithCode(wireMockServer, resource, statusCodesPost.get(0), ActionType.POST);
            }

            if (!resource.getResources().isEmpty()) {
                Collection<Resource> childResources = resource.getResources().values();
                stubResourcesRecursive(wireMockServer, childResources);
            }
        }
    }

    // TODO: now only get and post on resource with responses  for status codes in RAML
    private void stubJsonBodyExmpleWithCode(WireMockServer wireMockServer, Resource resource, String statusCode,
            ActionType actionType) {
        String resourceMatch = replaceResourceIdWithAnyMatcher(resource);
        log.debug("stubJsonBodyExmpleWithCode: {}  status code: {} resourceMatch: {}",
                resource.getUri(),
                statusCode,
                resourceMatch);

        if (ActionType.GET.equals(actionType)) {
            wireMockServer.stubFor(
                    get(urlMatching(resourceMatch))
                            .withHeader("Content-Type", equalTo("application/json"))
                            .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withStatus(Integer.parseInt(statusCode))
                                    .withBody(resource.getAction(actionType)
                                            .getResponses()
                                            .get(statusCode)
                                            .getBody()
                                            .get("application/json")
                                            .getExample())));
        } else if (ActionType.POST.equals(actionType)) {
            wireMockServer.stubFor(
                    post(urlMatching(resourceMatch))
                            .withHeader("Content-Type", equalTo("application/json"))
                            .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withStatus(Integer.parseInt(statusCode))
                                    .withBody(resource.getAction(actionType)
                                            .getResponses()
                                            .get(statusCode)
                                            .getBody()
                                            .get("application/json")
                                            .getExample())));
        }
    }

    // TODO: now only get on resource without responses for status codes in RAML
    private void stubJsonBodyExmple(WireMockServer wireMockServer, Resource resource) {
        String resourceMatch = replaceResourceIdWithAnyMatcher(resource);
        log.debug("stubJsonBodyExmple:{} resourceMatch: {}:", resource.getUri(), resourceMatch);

        wireMockServer.stubFor(
                get(urlEqualTo(resource.getUri()))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(resource.getAction(ActionType.GET)
                                        .getBody()
                                        .get("application/json")
                                        .getExample())));
    }

    private String replaceResourceIdWithAnyMatcher(Resource resource) {
        return resource.getUri().replaceAll("\\{[0-9a-zA-Z]*\\}", "[0-9a-zA-Z.]*");
    }

    private List<String> statusCodesThatHasExampleBody(Resource resource, ActionType actionType) {
        List<String> statusCodes = Arrays.asList("200", "201", "202", "204", "400", "401", "403", "404", "405", "409");

        return statusCodes.stream().filter(s -> hasAJsonBodyExmpleByStatusCode(resource, s, actionType))
                .peek(p -> log.debug("StatusCode match with example body: {}", p))
                .collect(
                        Collectors.toList());
    }

    private boolean hasAJsonBodyExmpleByStatusCode(Resource resource, String statusCode, ActionType actionType) {
        return resource.getAction(actionType) != null
                && !resource.getAction(actionType).hasBody()
                && resource.getAction(actionType).getResponses() != null
                && resource.getAction(actionType).getResponses().containsKey(statusCode)
                && resource.getAction(actionType).getResponses().get(statusCode).hasBody()
                && resource.getAction(actionType).getResponses().get(statusCode).getBody()
                .containsKey("application/json")
                && resource.getAction(actionType).getResponses().get(statusCode).getBody().get("application/json")
                .getExample() != null;
    }

    private boolean hasAJsonBodyExmple(Resource resource) {
        return resource.getAction(ActionType.GET) != null
                && resource.getAction(ActionType.GET).hasBody()
                && resource.getAction(ActionType.GET).getBody().containsKey("application/json")
                && resource.getAction(ActionType.GET).getBody().get("application/json").getExample() != null;
    }
}
