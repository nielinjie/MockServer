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
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.core.ConfigurationException;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsLoader;
import io.swagger.jaxrs.Reader;
import io.swagger.models.*;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import org.apache.http.HttpStatus;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;

import javax.annotation.RegEx;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Mock Server based on Swagger specification
 */
@Validated
public class SwaggerMockServer {

    private final static Logger LOG = LoggerFactory
        .getLogger(SwaggerMockServer.class);

    /*
     * Contains instance of created wire mock server
     */
    private WireMockServer wireMockServer;

    /**
     * Maps operation ID to matching stub
     */
    private final Map<String, MappingBuilder> stubs = new HashMap<>();

    /**
     * Maps Request expression to matching Operation
     */
    private final Map<String, Operation> operations = new HashMap<>();

    /*
     * Contains Swagger configuration as mocked by this server
     */
    private Swagger specification;

    /**
     * Default constructor for testing purposes only
     */
    SwaggerMockServer() {
        this(80);
    }

    /**
     * Creates instance of MockServer listening on speficied port
     */
    public SwaggerMockServer(int port) {
        LOG.info("Starting MockServer listening on port <>", port);
        wireMockServer = new WireMockServer(wireMockConfig().port(port));
        wireMockServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Shutting down the mock server");
                wireMockServer.shutdown();
            }
        });
    }

    /**
     * Constructor Mock Server
     * 
     * @param prefix
     *            Package that need to be scanned for annotated classes
     * @param port
     *            Port on which mock server will be reachable.
     */
    public SwaggerMockServer(String prefix, int port) {
        this(port);

        // Create Swagger specification based on annotated classes
        Swagger specification = getSpecification(prefix);

        // Create default stubs for operations within specification
        createStubs(specification);
    }

    /**
     * Constructor Mock Server
     *
     * @param prefix
     *            Package that need to be scanned for annotated classes
     * @param port
     *            Port on which mock server will be reachable.
     * @param responseFile
     * File containing responses for stubs
     */
    public SwaggerMockServer(String prefix, int port, String responseFile) {
        this(prefix,port);

        FileSource source = null;
        wireMockServer.loadMappingsUsing(new JsonFileMappingsLoader(source));
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
        LOG.info("Create swagger model based on annotated classes within {}",
            prefix);
        System.out
            .println("Create swagger model based on annotated classes within "
                + prefix);

        // Get all swagger annotated classes within specified package
        SwaggerAnnotationScanner scanner = new SwaggerAnnotationScanner();
        Set<Class<?>> resources = scanner.getResources(prefix);
        System.out.println("Found annotated classes are " + resources);

        // Read all annotated classes and create rest specifications
        Swagger swagger = null;
        Reader reader = new Reader(swagger);
        swagger = reader.read(resources);

        return swagger;
    }

    /**
     * Reset mock server removing all previously defined stubs
     */
    public void reset() {
        stubs.clear();

        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
        wireMockServer.resetScenarios();
    }

    /**
     * Creates server mocking REST APIs which are specified within swagger model
     * 
     * @param specification
     *            Swagger specification
     */
    void createStubs(@NotNull Swagger specification) {
        // Remove any previously defined stubs
        reset();

        this.specification = specification;

        // Create stubs for resources within specification
        stubResources(specification);
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
     * Updates definition for stub
     */
    public void stubFor(@NotNull MappingBuilder stub) {
        // Check whether operation exist for specified mapping
        RequestPattern request = stub.build().getRequest();
        if (operationExist(request)) {
            wireMockServer.stubFor(stub);
        } else {
            throw new ConfigurationException("Operation you attempt to stub (" + request + ")is not specified in specs");
        }
    }

    /**
     * Verifies operation exist for specified request
     *
     * @param request Request we want to stub
     *
     * @return true if operation exist, false otherwise.
     */
    private boolean operationExist(RequestPattern request) {
        String requestExpression = request.getMethod() +":" + request.getUrl();

        for (String urlExpression : operations.keySet()) {
            System.out.println(urlExpression + " matches " + requestExpression + "=" +
            Pattern.matches(urlExpression, requestExpression));
        }
        return true;
    }

    /**
     * Stub the operations as specified within specification.
     * 
     * @param specification
     *            Swagger specification
     */
    private void stubResources(Swagger specification) {

        if (specification.getPaths() != null
            && !specification.getPaths().isEmpty()) {
            for (Map.Entry<String, Path> paths : specification.getPaths()
                .entrySet()) {
                System.out.println("Processing operation(s) at path "
                    + paths.getKey());
                stubResource(paths.getKey(), paths.getValue());
            }
        } else {
            LOG.warn("No operations found. Make sure that the annotated classes are on the classpath of the server.");
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

            // Replace path parameter place holders by regular expression.
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

            // Add operation to dictionary for later retrieval
            String urlExpression = method + ":" + url;
            System.out.println("Adding operation " + operation.getOperationId() + " for URL expression " + urlExpression);
            operations.put(urlExpression,operation);

            // Create default stub for operation
            wireMockServer.stubFor(stub.willReturn(aResponse()
                .withStatus(HttpStatus.SC_NOT_IMPLEMENTED)
                .withHeader("Content-Type", "text/plain")
                .withHeader("Cache-Control", "no-cache")
                .withBody("No mocked response defined yet")));
        }
    }

}
