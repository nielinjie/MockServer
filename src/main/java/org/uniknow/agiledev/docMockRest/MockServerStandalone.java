package org.uniknow.agiledev.docMockRest;

import static java.util.Arrays.asList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.FileNotFoundException;

public class MockServerStandalone {

    /**
     * Method to start standalone Mock Server
     *
     * @param args arguments containing the location of the specification file and port number on which mock server need to be accessible.
     *
     * @throws FileNotFoundException if specification file could not be found.
     */
    public static void main(String[] args) throws FileNotFoundException {

        OptionParser parser = new OptionParser("r::");
        OptionSpec<String> ramlfile = parser.acceptsAll(asList("r", "ramlfile"))
                .withRequiredArg().required()
                .ofType(String.class);
        OptionSpec<Integer> port = parser.acceptsAll(asList("p", "port"))
                .withRequiredArg().required()
                .ofType(Integer.class);

        OptionSet options = parser.parse(args);

        new MockServer(ramlfile.value(options), port.value(options));
    }
}
