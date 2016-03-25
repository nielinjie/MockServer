package org.uniknow.agiledev.docMockRest;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.model.Resource;

import javax.validation.ValidationException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
        new MockServer(null,0, "test");
    }

    /**
     * Verifies ValidationException is thrown when specified port number is below zero
     */
    @Test(expected = ValidationException.class)
    public void testCreateMockServerPortNumberBelowZero() {
        MockServer server = new MockServer();
        Raml model = createMock(Raml.class);
        replay(model);

        server.createMockServer(model, -1,"test");
    }

    /**
     * Verifies ValidationException is thrown when specification is null
     */
    @Test(expected = ValidationException.class)
    public void testCreateMockServerSpecificationNull() {
        MockServer server = new MockServer();

        server.createMockServer(null, 1,"test");
    }

    /**
     * Verifies ValidationException occurs when collection of stubbed resources is null
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
