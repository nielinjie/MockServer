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
package org.uniknow.agiledev.docMockRest.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import org.junit.Test;

import javax.ws.rs.Path;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Verifies functionality of annotation scanner
 */
public class SwaggerAnnotationScannerTest {

    /**
     * Class with swagger `Api` annotation used to verify whether it is found by
     * annotation scanner
     */
    @Api
    public class TestClassOne {

    }

    /**
     * Class with swagger `SwaggerDefinition` annotation used to verify whether
     * it is found by annotation scanner
     */
    @SwaggerDefinition
    public class TestClassTwo {

    }

    /**
     * Class with JAXRS `Path` annotation used to verify whether it is found by
     * annotation scanner
     */
    @Path("TEST")
    public class TestClassThree {

    }

    /**
     * Verifies 'swagger' annotated classes are returned by scanner
     */
    @Test
    public void testRetrievalAnnotatedClasses() {
        SwaggerAnnotationScanner scanner = new SwaggerAnnotationScanner();

        // Scan package org.uniknow.agiledev.docMockRest.swagger
        Set<Class<?>> resources = scanner
            .getResources("org.uniknow.agiledev.docMockRest.swagger");

        // Verify annotated classes within this test class are found
        assertTrue("Found resources '" + resources + "' doesn't contain '"
            + TestClassOne.class + "'", resources.contains(TestClassOne.class));
        assertTrue("Found resources '" + resources + "' doesn't contain '"
            + TestClassTwo.class + "'", resources.contains(TestClassTwo.class));
        assertTrue("Found resources '" + resources + "' doesn't contain '"
            + TestClassThree.class + "'",
            resources.contains(TestClassThree.class));
    }
}
