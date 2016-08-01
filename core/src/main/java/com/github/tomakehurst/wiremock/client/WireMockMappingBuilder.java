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
package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestMatcher;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

/**
 * Created by mase on 8/1/2016.
 */
public class WireMockMappingBuilder extends MappingBuilder {

    public WireMockMappingBuilder(RequestMethod method, UrlPattern urlPattern) {
        super(method, urlPattern);
    }

    public WireMockMappingBuilder(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    public WireMockMappingBuilder(String customRequestMatcherName,
        Parameters parameters) {
        super(customRequestMatcherName, parameters);
    }
}
