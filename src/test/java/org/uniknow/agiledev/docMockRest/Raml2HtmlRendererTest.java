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

import org.junit.Test;
import org.raml.model.Raml;

import javax.validation.ValidationException;

import java.io.FileNotFoundException;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

/**
 * Validates functionality of RAML 2 HTML renderer
 */
public class Raml2HtmlRendererTest {

    /**
     * Verifies exception is thrown when attempting to instantiate renderer with
     * RAML spec null
     */
    @Test(expected = ValidationException.class)
    public void createInstanceRamlNull() {
        new Raml2HtmlRenderer(null);
    }

    /**
     * Check what happens if non existing template is specified
     */
    @Test(expected = FileNotFoundException.class)
    public void renderWithNonExistingTemplate() throws FileNotFoundException {
        Raml raml = createMock(Raml.class);
        replay(raml);

        Raml2HtmlRenderer renderer = new Raml2HtmlRenderer(raml);
        renderer.render("non-existing-template");
    }

}
