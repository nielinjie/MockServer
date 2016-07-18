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

import com.github.tomakehurst.wiremock.matching.RequestPattern;
import org.uniknow.agiledev.dbc4java.Validated;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * Checks whether `RequestPattern` matches.
 */
@Validated
public class RequestPatternMatcher {

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

        // Check whether methods of both request patterns match
        if (first.getMethod().equals(second.getMethod())) {
            // Check whether URL of second pattern matches the URL pattern of
            // the first one.
            String secondRequestUrl = second.getUrl() == null ? second
                .getUrlPattern() : second.getUrl();
            if (Pattern.matches(first.getUrlPattern(), secondRequestUrl)) {
                return true;
            }
        }
        return false;
    }
}
