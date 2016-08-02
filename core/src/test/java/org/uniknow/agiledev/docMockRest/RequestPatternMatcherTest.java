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

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.MultiValuePattern;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.junit.Test;

import javax.validation.ValidationException;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Validates functionality of `RequestPatternMatcher`.
 */
public class RequestPatternMatcherTest {

    /**
     * Verifies `ValidationException` is thrown in case first `RequestPattern`
     * is null
     */
    @Test(expected = ValidationException.class)
    public void testMatchFirstNull() {
        RequestPattern second = mock(RequestPattern.class);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        matcher.match(null, second);
    }

    /**
     * Validates `ValidationException` is thrown in case second `RequestPattern
     * is null
     */
    @Test(expected = ValidationException.class)
    public void testMatchSecondNull() {
        RequestPattern first = mock(RequestPattern.class);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        matcher.match(first, null);
    }

    /**
     * Validates request patterns don't match if methods are different
     */
    @Test
    public void testMatchRequestPatternsMismatchMethods() {
        RequestPattern first = mock(RequestPattern.class);
        expect(first.getMethod()).andReturn(RequestMethod.GET);

        RequestPattern second = mock(RequestPattern.class);
        expect(second.getMethod()).andReturn(RequestMethod.POST);

        replay(first, second);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        assertFalse(matcher.match(first, second));

        verify(first, second);
    }

    // /**
    // * Validates request patterns don't match if first RequestPattern has no
    // URL
    // * pattern
    // */
    // @Test
    // public void testMatchFirstRequestPatternHasNoUrlPattern() {
    // RequestPattern first = mock(RequestPattern.class);
    // expect(first.getMethod()).andReturn(RequestMethod.GET);
    // expect(first.getUrlPattern()).andReturn(null);
    //
    // RequestPattern second = mock(RequestPattern.class);
    // expect(second.getMethod()).andReturn(RequestMethod.GET);
    // expect(second.getUrl()).andReturn("/test/requestPatternMatcher").times(
    // 2);
    //
    // replay(first, second);
    //
    // RequestPatternMatcher matcher = new RequestPatternMatcher();
    // assertFalse(matcher.match(first, second));
    //
    // verify(first, second);
    // }

    /**
     * Validates request patterns match if method AND url RequestPatterns match.
     * 
     * No Query parameters, header, or body is specified.
     */
    @Test
    public void testMatchingRequestPatternsMatchingUrls() {
        RequestPattern first = mock(RequestPattern.class);
        expect(first.getMethod()).andReturn(RequestMethod.GET);
        expect(first.getUrlMatcher()).andReturn(
            UrlPattern.fromOneOf(null, "/test/.*", null, null)).atLeastOnce();
        ;
        expect(first.getQueryParameters()).andReturn(null);
        expect(first.getHeaders()).andReturn(null);

        RequestPattern second = mock(RequestPattern.class);
        expect(second.getMethod()).andReturn(RequestMethod.GET);
        expect(second.getUrl()).andReturn("/test/requestPatternMatcher")
            .atLeastOnce();

        replay(first, second);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        assertTrue(matcher.match(first, second));

        verify(first, second);

    }

    /**
     * Validates request patterns match if method, query patterns and url
     * RequestPatterns match.
     * 
     * No Query parameters, header, or body is specified.
     */
    @Test
    public void testMatchingRequestPatternsMismatchQueryParameters() {
        Map<String, MultiValuePattern> requestParameters = new HashMap<>();
        requestParameters.put("TEST", mock(MultiValuePattern.class));

        RequestPattern first = mock(RequestPattern.class);
        expect(first.getMethod()).andReturn(RequestMethod.GET);
        expect(first.getUrlMatcher()).andReturn(
            UrlPattern.fromOneOf(null, "/test/.*", null, null)).atLeastOnce();
        expect(first.getQueryParameters()).andReturn(requestParameters);

        RequestPattern second = mock(RequestPattern.class);
        expect(second.getMethod()).andReturn(RequestMethod.GET);
        expect(second.getUrl()).andReturn("/test/requestPatternMatcher")
            .atLeastOnce();
        expect(second.getQueryParameters()).andReturn(null);

        replay(first, second);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        assertFalse(matcher.match(first, second));

        verify(first, second);

    }

    /**
     * Validates request patterns match if method, query parameters and url
     * RequestPatterns match.
     * 
     * No Query parameters, header, or body is specified.
     */
    @Test
    public void testMatchingRequestPatternsWithQueryParameters() {
        Map<String, MultiValuePattern> requestParameters = new HashMap<>();
        requestParameters.put("TEST", mock(MultiValuePattern.class));

        RequestPattern first = mock(RequestPattern.class);
        expect(first.getMethod()).andReturn(RequestMethod.GET);
        expect(first.getUrlMatcher()).andReturn(
            UrlPattern.fromOneOf(null, "/test/.*", null, null)).atLeastOnce();
        expect(first.getQueryParameters()).andReturn(requestParameters);
        expect(first.getHeaders()).andReturn(null);

        RequestPattern second = mock(RequestPattern.class);
        expect(second.getMethod()).andReturn(RequestMethod.GET);
        expect(second.getUrl()).andReturn("/test/requestPatternMatcher")
            .atLeastOnce();
        expect(second.getQueryParameters()).andReturn(requestParameters);

        replay(first, second);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        assertTrue(matcher.match(first, second));

        verify(first, second);

    }

    /**
     * Validates request patterns match if method, query parameters and url
     * RequestPatterns match.
     * 
     * No Query parameters, header, or body is specified.
     */
    @Test
    public void testMatchingRequestPatternsWithHeaders() {
        Map<String, MultiValuePattern> headers = new HashMap<>();
        headers.put("TEST", mock(MultiValuePattern.class));

        RequestPattern first = mock(RequestPattern.class);
        expect(first.getMethod()).andReturn(RequestMethod.GET);
        expect(first.getUrlMatcher()).andReturn(
            UrlPattern.fromOneOf(null, "/test/.*", null, null)).atLeastOnce();
        expect(first.getQueryParameters()).andReturn(null);
        expect(first.getHeaders()).andReturn(headers);

        RequestPattern second = mock(RequestPattern.class);
        expect(second.getMethod()).andReturn(RequestMethod.GET);
        expect(second.getUrl()).andReturn("/test/requestPatternMatcher")
            .atLeastOnce();
        expect(second.getHeaders()).andReturn(headers);

        replay(first, second);

        RequestPatternMatcher matcher = new RequestPatternMatcher();
        assertTrue(matcher.match(first, second));

        verify(first, second);

    }

}
