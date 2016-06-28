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

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.Test;
import org.raml.model.Raml;
import org.uniknow.agiledev.docMockRest.raml.MockServer;

import javax.validation.ValidationException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

/**
 * Validates functionality MockServer
 */
public class MockServerTest {

    /**
     * Verifies ValidationException is thrown when specified RAML file is null
     */
    @Test(expected = ValidationException.class)
    public void testConstructorRamlFileNull() throws FileNotFoundException {
        new MockServer(null, 0, "test");
    }

    /**
     * Verifies ValidationException is thrown when specified port number is
     * below zero
     */
    @Test(expected = ValidationException.class)
    public void testCreateMockServerPortNumberBelowZero()
        throws FileNotFoundException {
        MockServer server = new MockServer();
        Raml model = createMock(Raml.class);
        replay(model);

        server.createMockServer(model, -1, "test");
    }

    /**
     * Verifies ValidationException is thrown when specification is null
     */
    @Test(expected = ValidationException.class)
    public void testCreateMockServerSpecificationNull()
        throws FileNotFoundException {
        MockServer server = new MockServer();

        server.createMockServer(null, 1, "test");
    }

    /**
     * Verifies ValidationException occurs when collection of stubbed resources
     * is null
     */
    @Test(expected = ValidationException.class)
    public void stubResourcesNull() {
        MockServer server = new MockServer();
        WireMockServer mockServer = createMock(WireMockServer.class);
        replay(mockServer);

        server.stubResources(mockServer, null);
    }

    /**
     * Verifies ValidationException occurs when mock server is null
     */
    @Test(expected = ValidationException.class)
    public void stubResourcesMockServerNull() {
        MockServer server = new MockServer();

        server.stubResources(null, new ArrayList<>());
    }
}
