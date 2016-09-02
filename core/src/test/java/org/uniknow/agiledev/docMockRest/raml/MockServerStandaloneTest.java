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

import org.junit.Test;
import org.uniknow.agiledev.docMockRest.raml.MockServerStandalone;

import javax.validation.ValidationException;
import java.io.FileNotFoundException;

/**
 * Verifies functionality of `MockServerStandalone`.
 */
public class MockServerStandaloneTest {

    /**
     * Verifies ValidationException is thrown in case argument referencing RAML
     * specification file is empty
     */
    @Test(expected = ValidationException.class)
    public void testStartServerWithSpecificationEmpty()
        throws FileNotFoundException {
        MockServerStandalone.main(new String[] { "-r", "", "-p", "8080",
                "-responses", "responses-location" });
    }

    /**
     * Verifies FileNotFoundException is thrown in case argument referencing
     * RAML specification file is referencing non existing file
     */
    @Test(expected = FileNotFoundException.class)
    public void testStartServerWithSpecificationNotExisting()
        throws FileNotFoundException {
        MockServerStandalone.main(new String[] { "-r", "non-existing.raml",
                "-p", "8080", "-responses", "responses-location" });
    }

    /**
     * Verifies ValidationException is thrown in case argument specifying
     * location responses is empty
     */
    @Test(expected = ValidationException.class)
    public void testStartServerWithResponsesLocationEmpty()
        throws FileNotFoundException {
        MockServerStandalone.main(new String[] { "-r",
                "./../examples/specifications/example.raml", "-p", "8080",
                "-responses", "" });
    }

}
