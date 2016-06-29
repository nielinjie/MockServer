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

import static java.util.Arrays.asList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.FileNotFoundException;

/**
 * @deprecated Replaced by org.uniknow.agiledev.docMockRest.MockServerStandalone
 */
public class MockServerStandalone {

    /**
     * Method to start standalone Mock Server
     * 
     * @param args
     *            arguments containing the location of the specification file
     *            and port number on which mock server need to be accessible.
     * 
     * @throws FileNotFoundException
     *             if specification file could not be found.
     */
    public static void main(String[] args) throws FileNotFoundException {

        OptionParser parser = new OptionParser("r::");
        OptionSpec<String> ramlfile = parser
            .acceptsAll(asList("r", "ramlfile")).withRequiredArg().required()
            .ofType(String.class);
        OptionSpec<Integer> port = parser.acceptsAll(asList("p", "port"))
            .withRequiredArg().required().ofType(Integer.class);
        OptionSpec<String> responseFiles = parser
            .acceptsAll(asList("responses")).withRequiredArg().required()
            .ofType(String.class);

        OptionSet options = parser.parse(args);

        new RamlMockServer(ramlfile.value(options), port.value(options),
            responseFiles.value(options));
    }
}
