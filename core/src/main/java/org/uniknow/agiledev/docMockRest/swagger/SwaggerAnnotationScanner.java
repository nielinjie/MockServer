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
package org.uniknow.agiledev.docMockRest.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import org.reflections.Reflections;

import javax.ws.rs.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mase on 6/27/2016.
 */
public class SwaggerAnnotationScanner {

    // private final FastClasspathScanner scanner;

    public SwaggerAnnotationScanner() {

        // scanner = new FastClasspathScanner()
        // .matchClassesWithAnnotation(Api.class));
        // provider.addIncludeFilter(new
        // AnnotationTypeFilter(SwaggerDefinition.class));
        // provider.addIncludeFilter(new AnnotationTypeFilter(Path.class));
    }

    /**
     * Returns Set of classes which are annotated with Swagger or Jax-RS
     * annotations.
     */
    public Set<Class<?>> getResources(String prefix) {
        Set<Class<?>> annotatedClasses = new HashSet();

        // Get all annotated classes
        Reflections scanner = new Reflections(prefix);
        annotatedClasses.addAll(scanner.getTypesAnnotatedWith(Api.class));
        annotatedClasses.addAll(scanner
            .getTypesAnnotatedWith(SwaggerDefinition.class));
        annotatedClasses.addAll(scanner.getTypesAnnotatedWith(Path.class));

        return annotatedClasses;
    }

}
