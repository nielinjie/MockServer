/**
 * Copyright (C) 2016 UniKnow (info.uniknow@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uniknow.agiledev.docMockRest.swagger;

import com.github.tomakehurst.wiremock.client.RemoteMappingBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
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
import java.net.Socket;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.Assert.*;

/**
 * Validates functionality of MockServer (swagger)
 */
public class MockServerSwaggerYamlTest {

    private static SwaggerMockServer server;

    @BeforeClass
    public static void init() {

        try {
            server = new SwaggerMockServer(
                    SwaggerConfig
                            .create()
                            .setSwaggerResourceName("/echo.yaml"),
                    8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Verifies correct stub is returned
     */
    @Test
    public void testRetrievingStub() {
        RemoteMappingBuilder stub = server.when("echo");
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
        request = new HttpGet("http://localhost:8080/echo?message=test");
        response = client.execute(request);
        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, response.getStatusLine()
                .getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());

        // Check proper response is returned for operation with query parameters
        request = new HttpGet(
                "http://localhost:8080/echo?message=test");
        response = client.execute(request);
        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, response.getStatusLine()
                .getStatusCode());
        EntityUtils.consumeQuietly(response.getEntity());
    }



    /**
     * Verifies Operation for mocked stub can be retrieved successfully
     */
    @Test
    public void testGetOperationByStub() {
        Operation operation = server.getOperation(new RequestPatternBuilder(
                RequestMethod.GET, UrlPattern.fromOneOf("/echo?message=test", null, null,
                null)).build());
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
        assertNull(server.getOperation(new RequestPatternBuilder(
                RequestMethod.GET, UrlPattern.fromOneOf("/nonexisting/operation",
                null, null, null)).build()));
    }







}
