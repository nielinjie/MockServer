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
package org.uniknow.agiledev.docMockRest.raml;

import org.junit.Test;
import org.raml.model.Raml;
import org.uniknow.agiledev.docMockRest.raml.Raml2HtmlRenderer;

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
     * Check FileNotFoundException occurs when non existing template is
     * specified
     */
    @Test(expected = FileNotFoundException.class)
    public void renderWithNonExistingTemplate() throws FileNotFoundException {
        Raml raml = createMock(Raml.class);
        replay(raml);

        Raml2HtmlRenderer renderer = new Raml2HtmlRenderer(raml);
        renderer.render("non-existing-template");
    }

    /**
     * Checks ValidationException occurs when template null is specified
     */
    @Test(expected = ValidationException.class)
    public void renderWithTemplateNull() throws FileNotFoundException {
        Raml raml = createMock(Raml.class);
        replay(raml);

        Raml2HtmlRenderer renderer = new Raml2HtmlRenderer(raml);
        renderer.render(null);
    }

    /**
     * Checks ValidationException occurs when template is blank string
     */
    @Test(expected = ValidationException.class)
    public void renderWithTemplateBlank() throws FileNotFoundException {
        Raml raml = createMock(Raml.class);
        replay(raml);

        Raml2HtmlRenderer renderer = new Raml2HtmlRenderer(raml);
        renderer.render("   ");
    }

    /**
     * Checks ValidationException occurs when template is empty string
     */
    @Test(expected = ValidationException.class)
    public void renderWithTemplateEmpty() throws FileNotFoundException {
        Raml raml = createMock(Raml.class);
        replay(raml);

        Raml2HtmlRenderer renderer = new Raml2HtmlRenderer(raml);
        renderer.render("");
    }

}
