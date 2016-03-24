package org.uniknow.agiledev.docMockRest;

import org.junit.Test;
import org.raml.model.Raml;

import javax.validation.ValidationException;

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
    public void testConstructorRamlFileNull() {
        new MockServer(null,0);
    }

    /**
     * Verifies ValidationException is thrown when specified port number is below zero
     */
    @Test(expected = ValidationException.class)
    public void testCreateMockServerPortNumberBelowZero() {
        MockServer server = new MockServer();
        Raml model = createMock(Raml.class);
        replay(model);

        server.createMockServer(model, -1);
    }

    /**
     * Verifies ValidationException is thrown when specification is null
     */
    @Test(expected = ValidationException.class)
    public void testCreateMockServerSpecificationNull() {
        MockServer server = new MockServer();

        server.createMockServer(null, 1);
    }

}
