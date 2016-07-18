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
import org.junit.Test;

import javax.validation.ValidationException;

import static org.easymock.EasyMock.mock;

/**
 * Validates functionality of `RequestPatternMatcher`.
 */
public class RequestPatternMatcherTest {

    /**
     * Verifies `ValidationException` is thrown in case of first
     * `RequestPattern` is null
     */
    @Test(expected = ValidationException.class)
    public void testMatchFirstNull() {
        RequestPattern second = mock(RequestPattern.class);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        matcher.match(null, second);
    }
}
