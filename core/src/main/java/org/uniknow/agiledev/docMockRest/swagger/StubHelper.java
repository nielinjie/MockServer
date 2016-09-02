

package org.uniknow.agiledev.docMockRest.swagger;

import com.github.tomakehurst.wiremock.client.RemoteMappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import org.apache.http.HttpStatus;
import org.uniknow.agiledev.docMockRest.SystemError;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.uniknow.agiledev.docMockRest.swagger.SwaggerMockServer.LOG;

class StubHelper {
    private final SwaggerMockServer swaggerMockServer;

    StubHelper(SwaggerMockServer swaggerMockServer) {
        this.swaggerMockServer = swaggerMockServer;
    }

    /**
     * Creates server mocking REST APIs which are specified within swagger model
     * 
     * @param specification
     *            Swagger specification
     */
    void createStubs(@NotNull Swagger specification) {
        // Remove any previously defined stubs
        swaggerMockServer.reset();

        swaggerMockServer.setSpecification(specification);

        // Create stubs for resources within specification
        stubPaths(specification);
    }

    /**
     * Stub the operations as specified within specification.
     * 
     * @param specification
     *            Swagger specification
     */
    private void stubPaths(Swagger specification) {

        if (specification.getPaths() != null
            && !specification.getPaths().isEmpty()) {
            for (Map.Entry<String, Path> paths : specification.getPaths()
                .entrySet()) {
                LOG.debug("Processing operation(s) at path {}", paths.getKey());
                //path里面有参数作为不同的path ->
                //form http://swagger.io/specification/#pathTemplating -
                //Path templating refers to the usage of curly braces ({}) to mark a section of a URL path as replaceable using path parameters.

                stubPath(paths.getKey(), paths.getValue());
            }
        } else {
            LOG.warn("No operations found. Make sure that the annotated classes are on the classpath of the server.");
        }
    }

    private void stubPath(String url, Path path) {
        stubOperation(HttpMethod.GET, url, path.getGet());
        stubOperation(HttpMethod.PUT, url, path.getPut());
        stubOperation(HttpMethod.POST, url, path.getPost());
        stubOperation(HttpMethod.DELETE, url, path.getDelete());
    }

    private void stubOperation(HttpMethod method, String url,
        Operation operation) {
        if (operation != null) {
            LOG.info("Creating stub for [{}]:{}", method, url);
            RemoteMappingBuilder stub = createStub(method, url);

            // TODO: Add matching of query parameters and/or headers
            // TODO: path 里的参数如何？
            for (Parameter parameter : operation.getParameters()) {
                LOG.debug("Processing parameter {}", parameter.getIn());
                if (parameter.getRequired()) {
                    if (parameter.getIn().equalsIgnoreCase("query")) {
                        stub.withQueryParam(parameter.getName(), WireMock
                            .matching(createRegularExpression(parameter
                                .getPattern())));
                    }
                }
            }
            //TODO 在这里添加MockResponse -
            // 制造com.github.tomakehurst.wiremock.http.ResponseDefinition
            // 装进去 -
            // Create default response for stub
            stub.willReturn(
                WireMock.aResponse().withStatus(HttpStatus.SC_NOT_IMPLEMENTED)
                    .withHeader("Content-Type", "text/plain")
                    .withHeader("Cache-Control", "no-cache")
                    .withBody("No mocked response defined yet")).atPriority(
                Integer.MAX_VALUE);

            // Create response for bad request
            createResponseBadRequest(method, url, operation);

            // Add stub to dictionary for later retrieval
            LOG.info("Adding stub for operation {}", operation.getOperationId());
            swaggerMockServer.getStubs().put(operation.getOperationId(), stub);

            // Add operation to dictionary for later retrieval
            // TODO: Instead of creating own key, use request pattern
            // TODO 有问题，operation 是有id的，为何不用？
            // String urlExpression = method + ":" + url;
            RequestPattern request = stub.build().getRequest();
            LOG.info("Adding operation {} for request {}",
                operation.getOperationId(), request);
            swaggerMockServer.getOperations().put(request, operation);

            // Create default stub for operation
            swaggerMockServer.getWireMockServer().stubFor(stub);

        }
    }

    /**
     * Creates stub for specified URL
     */
    private RemoteMappingBuilder createStub(HttpMethod method, String url) {
        // Replace path parameter place holders by regular expression.
        // TODO: Replace . (match any character) by proper regular
        // expression based on type parameter.
        // 这里处理掉了path template 参数
        url = url.replaceAll("\\{.*\\}", ".*");

        // Make sure that url also matches requests including query
        // parameters
        // 替换可能跟wiremock语法有关。
        url = url + "(\\?.*)?";

        RemoteMappingBuilder stub;
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
            LOG.warn("[{}]:{} is not supported yet", method, url);
            throw new SystemError("Unsupported HTTP Method");
        }

        return stub;
    }

    /**
     * Creates default response for requests without mandatory parameters or
     * missing headers.
     */
    private void createResponseBadRequest(HttpMethod method, String url,
        Operation operation) {
        if ((operation != null) && hasMandatoryQueryParameters(operation)) {

            LOG.info("Creating default response for bad request [{}]:{}",
                method, url);
            RemoteMappingBuilder stub = createStub(method, url);

            // Create default response for stub
            stub.willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_BAD_REQUEST)
                    .withHeader("Content-Type", "text/plain")
                    .withHeader("Cache-Control", "no-cache")
                    .withBody(
                        "Invalid Request, missing mandatory parameter or header"))
                .atPriority(Integer.MAX_VALUE);

            swaggerMockServer.wireMockServer.stubFor(stub);
        }
    }

    /**
     * Returns whether the Operation has mandatory query parameters.
     */
    private boolean hasMandatoryQueryParameters(Operation operation) {
        for (Parameter parameter : operation.getParameters()) {
            if (parameter.getRequired()
                && parameter.getIn().equalsIgnoreCase("query")) {
                return true;
            }
        }
        return false;
    }

    private String createRegularExpression(String type) {
        return ".*";
    }
}