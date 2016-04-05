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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.StringTemplateSource;
import org.raml.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by mase on 3/29/2016.
 */

public class Raml2HtmlRenderer {

    private final static Logger LOG = LoggerFactory
        .getLogger(Raml2HtmlRenderer.class);

    private final Handlebars handlebars;
    private final Raml raml;

    public Raml2HtmlRenderer(Raml raml) {
        this.handlebars = HandlebarsFactory.defaultHandlebars();
        this.raml = raml;
    }

    public String renderFull() {
        return renderFull(null);
    }

    public String renderFull(String mainTemplateFile) {
        return renderClassPathTemplate(
            orDefault(mainTemplateFile, "template.hbs"), raml);
    }

    public String renderResource(String uri) {
        LOG.debug("Rendering {}", uri);
        return renderResource(uri, null);
    }

    public String renderResource(String uri, String resourceTemplateFile) {
        LOG.debug("Rendering {}", uri);
        return renderClassPathTemplate(
            orDefault(resourceTemplateFile, "resource.hbs"),
            getResourceContext(uri));
    }

    public String renderExample(String uri, String method, String status,
        String mimeType) {
        Action action = getResourceContext(uri).getAction(
            ActionType.valueOf(method.toUpperCase()));

        String exampleFormat = "<div class=\"listingblock\">"
            + "<div class=\"content\">"
            + "<pre class=\"CodeRay highlight raml_example\">%s</pre>"
            + "</div>" + "</div>";

        if (status != null) {
            String exampleForResponse = getResponseForAction(action, status)
                .getBody().get(mimeType).getExample();
            return String.format(exampleFormat, exampleForResponse);
        } else {
            return String.format(exampleFormat, action.getBody().get(mimeType)
                .getExample());
        }
    }

    public String renderHeaderList(String uri, String method, String status) {
        Action action = getResourceContext(uri).getAction(
            ActionType.valueOf(method.toUpperCase()));
        if (status != null) {
            return renderClassPathTemplate("header_list.hbs",
                getResponseForAction(action, status).getHeaders());
        }
        return renderClassPathTemplate("header_list.hbs", action.getHeaders());
    }

    public String renderSchema(String schemaName) {
        if (raml.getConsolidatedSchemas().get(schemaName) == null) {
            throw new IllegalArgumentException("schema does not exist: "
                + schemaName);
        }

        return renderClassPathTemplate("schema.hbs", raml
            .getConsolidatedSchemas().get(schemaName));
    }

    public String renderStatuses(String uri, String method) {
        Action action = getResourceContext(uri).getAction(
            ActionType.valueOf(method.toUpperCase()));
        return renderClassPathTemplate("statuses.hbs", action.getResponses());
    }

    public String getBaseUri() {
        return raml.getBaseUri();
    }

    protected Response getResponseForAction(Action action, String status) {
        for (Map.Entry<String, Response> response : action.getResponses()
            .entrySet()) {
            if (response.getKey().equals(status)) {
                return response.getValue();
            }
        }
        throw new IllegalArgumentException(format(
            "no response found for status %s in action %s", status, action));
    }

    public String renderRamlContext(String templateContent) {
        return renderHandlebars(templateContent, raml);
    }

    private Resource getResourceContext(String uri) {

        return raml.getResource(uri);
    }

    String renderClassPathTemplate(String classPathTemplate, Object context) {
        return renderHandlebars(fileContent(classPathTemplate), context);
    }

    private String renderHandlebars(String templateContent, Object context) {
        try {
            Template template = handlebars.compile(new StringTemplateSource(
                "raml_template", templateContent));
            return template.apply(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String fileContent(String templateLocation) {
        return IoUtil.contentFromFile(templateLocation);
    }

    <T> T orDefault(T nullable, T defaultValue) {
        if (nullable == null) {
            return defaultValue;
        }
        return nullable;
    }

}