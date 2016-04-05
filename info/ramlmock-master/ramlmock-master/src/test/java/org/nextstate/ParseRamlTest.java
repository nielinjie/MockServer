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

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

public class ParseRamlTest {

    @Test
    public void shouldParseMyRaml() {
        Raml raml = new RamlDocumentBuilder().build(
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("prello_api.raml"), "prello_api.raml");
        System.out.println(raml);
    }
}
