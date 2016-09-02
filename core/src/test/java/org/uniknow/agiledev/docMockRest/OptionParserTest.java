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
package org.uniknow.agiledev.docMockRest;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.junit.Before;
import org.junit.Test;

public class OptionParserTest {

    OptionParser parser;
    OptionSpec<Integer> port;

    @Before
    public void setup() {
        parser = new OptionParser("r::");
        parser.acceptsAll(asList("r", "ramlfile")).withRequiredArg().required()
            .ofType(String.class);
        port = parser.acceptsAll(asList("p", "port")).withRequiredArg()
            .required().ofType(Integer.class);
    }

    @Test
    public void shouldParseFileAndPort() {
        OptionSet options = parser.parse("-ramlfile=raml.fil", "-port=8099");

        assertTrue(options.has("ramlfile"));
        assertTrue(options.hasArgument("ramlfile"));
        assertEquals("raml.fil", options.valueOf("ramlfile"));

        assertTrue(options.has(port));
        assertTrue(options.hasArgument(port));
        Integer expectedPort = 8099;
        assertEquals(expectedPort, port.value(options));
    }

    @Test
    public void shouldParseRAndP() {
        OptionSet options = parser.parse("-r=raml.fil", "-p=8099");

        assertTrue(options.has("ramlfile"));
        assertTrue(options.hasArgument("ramlfile"));
        assertEquals("raml.fil", options.valueOf("ramlfile"));

        assertTrue(options.has("port"));
        assertTrue(options.hasArgument("port"));
        assertEquals(8099, options.valueOf("port"));
    }

    @Test(expected = OptionException.class)
    public void shouldMissRequiredArg() {
        parser.parse("-r=raml.fil");
    }
}
