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

import com.github.tomakehurst.wiremock.matching.MultiValuePattern;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uniknow.agiledev.dbc4java.Validated;

import javax.el.ValueExpression;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Checks whether `RequestPattern` matches.
 */
@Validated
public class RequestPatternMatcher {

    private final static Logger LOG = LoggerFactory
        .getLogger(RequestPatternMatcher.class);

    /**
     * Checks whether the second `RequestPattern` 'matches' the first
     * 'RequestPattern'.
     * 
     * @param first
     *            First `RequestPattern`.
     * @param second
     *            Second `RequestPattern` that will be compared with the first
     *            to see whether they 'match'.
     * 
     * @return true if request patterns match; false otherwise
     */
    public boolean match(@NotNull RequestPattern first,
        @NotNull RequestPattern second) {

        LOG.debug("Check whether request pattern {} matches {}", first, second);
        // Check whether methods of both request patterns match
        if (first.getMethod().equals(second.getMethod())) {
            // Check whether URL of second pattern matches the URL pattern of
            // the first one.
            String secondRequestUrl = second.getUrl() == null ? second
                .getUrlPattern() : second.getUrl();
            String urlPattern = first.getUrlPattern();
            if ((urlPattern != null)
                && (Pattern.matches(urlPattern, secondRequestUrl) || urlPattern
                    .equals(secondRequestUrl))) {

                // Check whether first request has mandatory query parameters
                Map<String, MultiValuePattern> mandatoryQueryParameters = first
                    .getQueryParameters();
                if (mandatoryQueryParameters != null
                    && !mandatoryQueryParameters.isEmpty()) {
                    // Check whether second request pattern contains all
                    // mandatory query parameters
                    Map<String, MultiValuePattern> queryParameters = second
                        .getQueryParameters();
                    if ((queryParameters != null) && !queryParameters.isEmpty()) {
                        if (!queryParameters.keySet().containsAll(
                            mandatoryQueryParameters.keySet())) {
                            LOG.debug("Missing mandatory query parameter");
                            return false;
                        }
                    } else {
                        LOG.debug("Missing mandatory query parameter");
                        return false;
                    }
                }

                // Check whether first request has mandatory header parameters
                Map<String, MultiValuePattern> mandatoryHeaders = first
                    .getHeaders();
                if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty()) {
                    // Check whether second request pattern contains all
                    // mandatory headers
                    Map<String, MultiValuePattern> headers = second
                        .getHeaders();
                    if ((headers != null) && !headers.isEmpty()) {
                        if (!headers.keySet().containsAll(
                            mandatoryHeaders.keySet())) {
                            LOG.debug("Missing mandatory header parameters");
                            return false;
                        }
                    } else {
                        LOG.debug("Missing mandatory headers");
                        return false;
                    }
                }

                LOG.debug("Request patterns match.");
                return true;
            } else {
                LOG.debug("Mismatch URLs");
            }
        } else {
            LOG.debug("Mismatch methods");
        }
        return false;
    }
}
