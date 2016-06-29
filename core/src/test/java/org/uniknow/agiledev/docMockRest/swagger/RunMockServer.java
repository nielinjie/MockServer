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

import java.io.FileNotFoundException;

/**
 * Created by mase on 6/29/2016.
 */
public class RunMockServer {

    public static void main(String[] args) throws FileNotFoundException {
        new SwaggerMockServer(
            "org.uniknow.agiledev.docMockRest.examples.swagger.annotated", 8080);
    }
}
