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
package org.uniknow.agiledev.docMockRest;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.uniknow.agiledev.docMockRest.raml.RamlMockServer;
import org.uniknow.agiledev.docMockRest.swagger.SwaggerMockServer;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.util.Arrays.asList;

/**
 * Created by mase on 6/29/2016.
 */
public class MockServerStandalone {

    /**
     * Method to start standalone Mock Server
     * 
     * @param args
     *            arguments containing the location of the specification file
     *            and port number on which mock server need to be accessible.
     * @throws FileNotFoundException
     *             if specification file could not be found.
     */
    public static void main(String[] args) throws IOException {

        OptionParser parser = new OptionParser("r::");
        OptionSpec<String> raml = parser
            .accepts("raml", "specifies location of RAML file")
            .withRequiredArg().ofType(String.class);
        OptionSpec<String> swagger = parser
            .accepts("swagger",
                "specifies package containing annotated classes")
            .withRequiredArg().ofType(String.class);
        OptionSpec<Integer> port = parser
            .accepts("port", "specifies port at which server will run")
            .withRequiredArg().ofType(Integer.class).defaultsTo(80);
        OptionSpec<String> responses = parser.accepts("responses")
            .withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if (options.has(raml) && options.has(swagger)) {
            throw new SystemError("Can't specify both RAML and Swagger");
        } else if (options.has(raml)) {
            // Create MockServer supporting RAML
            new RamlMockServer(raml.value(options), port.value(options),
                responses.value(options));

        } else if (options.has(swagger)) {
            // Create MockServer supporting Swagger
            new SwaggerMockServer(swagger.value(options), port.value(options));
        } else {
            parser.printHelpOn(System.out);
            throw new SystemError(
                "You MUST either specify RAML file or Swagger");
        }

    }
}
