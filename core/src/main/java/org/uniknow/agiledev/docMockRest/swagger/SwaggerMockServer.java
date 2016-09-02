/**
 * Copyright (C) 2016 UniKnow (info.uniknow@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uniknow.agiledev.docMockRest.swagger;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RemoteMappingBuilder;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.ConfigurationException;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;
import org.uniknow.agiledev.docMockRest.RequestPatternMatcher;

import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Mock Server based on Swagger specification
 */
@Validated
public class SwaggerMockServer {

    final static Logger LOG = LoggerFactory.getLogger(SwaggerMockServer.class);
    final StubHelper stubHelper = new StubHelper(this);

    /*
     * Contains instance of created wire mock server
     */
    WireMockServer wireMockServer;

    /**
     * Maps operation ID to matching stub
     */
    private final Map<String, RemoteMappingBuilder> stubs = new HashMap<>();

    /**
     * Maps Request expression to matching Operation
     */
    private final Map<RequestPattern, Operation> operations = new HashMap<>();

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
        LOG.info("Starting MockServer listening on port {}", port);
        wireMockServer = new WireMockServer(wireMockConfig().port(port)
            .notifier(new Slf4jNotifier(true)));
        wireMockServer.start();

        // Runtime.getRuntime().addShutdownHook(new Thread() {
        // @Override
        // public void run() {
        // LOG.info("Shutting down the mock server");
        // wireMockServer.shutdown();
        // }
        // });
    }

    public SwaggerMockServer(SwaggerConfig config, int port) throws IOException {
        this(port);
        config.precessServer(this);
    }

    /**
     * Gracefully shutdown the server.
     * 
     * This method assumes it is being called as the result of an incoming HTTP
     * request.
     */
    public void shutdown() {
        LOG.info("Shutdown mock server");
        wireMockServer.shutdown();
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
     * Returns stub for specified operation
     * 
     * @param operationID
     *            Identifier of operation for which we want to retrieve Stub
     * @return Stub for specified operation
     */
    public RemoteMappingBuilder when(
        @NotNull @NotEmpty @NotBlank String operationID) {
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
    public void stubFor(@NotNull RemoteMappingBuilder stub) {
        // Check whether operation exist for specified mapping
        RequestPattern request = stub.build().getRequest();
        if (operationExist(stub)) {
            wireMockServer.stubFor(stub);
        } else {
            throw new ConfigurationException("Operation you attempt to stub ("
                + request + ")is not specified in specs");
        }
    }

    /**
     * Returns Operation that matches the specified request
     * 
     * @return matching operation or null.
     */
    //TODO 疑似bug，两个pattern，如何相互match？
    public Operation getOperation(@NotNull RequestPattern request) {

        String requestUrl = request.getUrl() == null ? request.getUrlPattern()
            : request.getUrl();

        RequestPatternMatcher matcher = new RequestPatternMatcher();

        // Find Operation that matches the specified Request
        for (RequestPattern spec : operations.keySet()) {
            if (matcher.match(spec, request)) {
                return operations.get(spec);
            }
        }

        LOG.debug("No matching operation found for {}", request);
        return null;
    }

    /**
     * Verifies operation exist for specified request
     * 
     * @return true if operation exist, false otherwise.
     */
    private boolean operationExist(RemoteMappingBuilder stub) {
        return getOperation(stub.build().getRequest()) != null;
    }

    public Map<String, RemoteMappingBuilder> getStubs() {
        return stubs;
    }

    public Map<RequestPattern, Operation> getOperations() {
        return operations;
    }

    public Swagger getSpecification() {
        return specification;
    }

    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public void setSpecification(Swagger specification) {
        this.specification = specification;
    }

    // /**
    // * Creates `RequestPattern` based on passed REST operation.
    // *
    // * @param operation
    // * REST operation
    // *
    // * @return RequestPattern for passed operation
    // */
    // private RequestPattern createRequestPattern(HttpMethod method, String
    // url,
    // Operation operation) {
    // RequestPatternBuilder builder = new RequestPatternBuilder(method, url)
    // return null;
    // }
}
