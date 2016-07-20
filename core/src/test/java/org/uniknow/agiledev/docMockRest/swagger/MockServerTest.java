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

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import io.swagger.models.Operation;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uniknow.agiledev.docMockRest.SystemError;

import javax.validation.ValidationException;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.*;

/**
 * Validates functionality of MockServer (swagger)
 */
public class MockServerTest {

    private static SwaggerMockServer server;

    @BeforeClass
    public static void init() {
        server = new SwaggerMockServer(
            "org.uniknow.agiledev.docMockRest.examples.swagger.annotated", 8080);

    }

    /**
     * Verifies correct stub is returned
     */
    @Test
    public void testRetrievingStub() {
        MappingBuilder stub = server.when("createUser");
        assertNotNull(stub);
    }

    /**
     * Verifies default response for stubbed operation is 501
     */
    @Test
    public void testInvokeStubbedOperationWithNoResponseDefined()
        throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request;
        HttpResponse response;

        // Check proper response is returned for operation with path parameter
        request = new HttpGet("http://localhost:8080/user/x");
        response = client.execute(request);
        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, response.getStatusLine()
            .getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());

        // Check proper response is returned for operation with query parameters
        request = new HttpGet(
            "http://localhost:8080/user/login?username=test&password=test");
        response = client.execute(request);
        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, response.getStatusLine()
            .getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());
    }

    /**
     * Verifies response for stubbed operation can be modified
     */
    @Test
    public void testModifyingResponseStubbedOperation() throws IOException {
        HttpClient client = new DefaultHttpClient();

        // Verify default response is 501
        HttpGet request = new HttpGet("http://localhost:8080/user/logout");
        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, response.getStatusLine()
            .getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());

        // Change response into 403
        server.stubFor(server.when("logoutUser").willReturn(
            aResponse().withBody("Unauthorized access").withStatus(
                HttpStatus.SC_FORBIDDEN)));

        // Verify response is now 403
        request = new HttpGet("http://localhost:8080/user/logout");
        response = client.execute(request);
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusLine()
            .getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());
    }

    /**
     * Verifies Operation for mocked stub can be retrieved successfully
     */
    @Test
    public void testGetOperationByStub() {
        Operation operation = server.getOperation(new RequestPattern(
            RequestMethod.GET, "/user/test"));
        assertNotNull(operation);
    }

    /**
     * Verifies exception is thrown in case passed request pattern is null
     */
    @Test(expected = ValidationException.class)
    public void testGetOperationByStubRequestNull() {
        server.getOperation(null);
    }

    /**
     * Verifies null is returned for non existing operation/stub
     */
    @Test
    public void testGetNonExistingOperation() {
        assertNull(server.getOperation(new RequestPattern(RequestMethod.GET,
            "/nonexisting/operation")));
    }

    /**
     * Initiates a MockServer reading response file
     */
    @Test
    public void initiateServerWithResponseFile() throws IOException {
        SwaggerMockServer serverWithResponse = null;
        try {
            // Find responses file on classpath
            String responseFileLocation = this.getClass()
                .getResource("responses.json").getFile();

            // Initiate mock server
            serverWithResponse = new SwaggerMockServer(
                "org.uniknow.agiledev.docMockRest.swagger", 9090,
                responseFileLocation);

            // Verify correct result is returned
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(
                "http://localhost:9090/HelloWorld/TEST");
            HttpResponse response = client.execute(request);
            assertEquals(HttpStatus.SC_OK, response.getStatusLine()
                .getStatusCode());
            assertEquals("Hello TEST",
                new BasicResponseHandler().handleResponse(response));
        } finally {
            if (serverWithResponse != null) {
                serverWithResponse.shutdown();
            }
        }

    }

    /**
     * Initiates a MockServer reading responses for non existing operations
     */
    @Test(expected = SystemError.class)
    public void initiateServerWithResponseFileContainingNonExistingOperations()
        throws IOException {
        SwaggerMockServer serverWithResponse = null;

        try {
            // Find responses file on classpath
            String responseFileLocation = this.getClass()
                .getResource("responses-non-existing-operation.json").getFile();

            // Initiate mock server
            serverWithResponse = new SwaggerMockServer(
                "org.uniknow.agiledev.docMockRest.swagger", 7070,
                responseFileLocation);
        } finally {
            if (serverWithResponse != null) {
                serverWithResponse.shutdown();
            }
        }
    }

}
