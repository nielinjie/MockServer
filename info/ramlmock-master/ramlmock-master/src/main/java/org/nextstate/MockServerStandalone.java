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
package org.nextstate;

import static java.util.Arrays.asList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class MockServerStandalone {

    public static void main(String[] args) {

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
