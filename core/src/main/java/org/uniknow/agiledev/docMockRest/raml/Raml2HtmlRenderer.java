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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.StringTemplateSource;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.raml.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;
import org.uniknow.agiledev.docMockRest.HandlebarsFactory;
import org.uniknow.agiledev.docMockRest.IoUtil;

import javax.validation.constraints.NotNull;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Transforms RAML specification into HTML page.
 */
@Validated
public class Raml2HtmlRenderer {

    private final static Logger LOG = LoggerFactory
        .getLogger(Raml2HtmlRenderer.class);

    private final Handlebars handlebars;

    /**
     * Name of default template file.
     */
    protected static final String DEFAULT_TEMPLATE = "template.hbs";

    /*
     * RAML specification
     */
    @NotNull
    private final Raml raml;

    /**
     * Constructor RAML to HTML renderer
     * 
     * @param raml
     *            specification on which generated HTML report is based
     */
    public Raml2HtmlRenderer(Raml raml) {
        this.handlebars = HandlebarsFactory.defaultHandlebars();
        this.raml = raml;
    }

    /**
     * Renders RAML specification into HTML
     * 
     * @return String containing HTML page
     * 
     * @throws FileNotFoundException
     *             if template used to render RAML specification could not be
     *             found.
     */
    public String render() throws FileNotFoundException {
        return render(DEFAULT_TEMPLATE);
    }

    /**
     * Renders RAML specification into HTML
     * 
     * @param templateFile
     *            Template that will be used to render RAML specification
     * 
     * @return String containing HTML page
     * 
     * @throws FileNotFoundException
     *             if template used to render RAML specification could not be
     *             found.
     */
    public String render(@NotNull @NotEmpty @NotBlank String templateFile)
        throws FileNotFoundException {
        return render(IoUtil.contentFromFile(templateFile), raml);
    }

    /**
     * Renders the RAML definition using the specified template file.
     * 
     * @param templateFile
     *            template file that will be used to render the RAML definition.
     * @param raml
     *            RAML definition.
     * 
     * @return HTML documentation RAML definition.
     */
    private String render(String templateFile, Object raml) {
        LOG.debug("Rendering RAML specification with template {}", templateFile);
        try {
            Template template = handlebars.compile(new StringTemplateSource(
                "raml_template", templateFile));
            return template.apply(raml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}