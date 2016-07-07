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

import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.stubbing.JsonStubMappingCreator;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;

/**
 * Responsible for reading file containing Responses for defined stubs.
 */
public class ResponseFileLoader implements MappingsLoader {

    /*
     * File containing responses for defined stubs
     */
    private String responseFile;

    public ResponseFileLoader(String responseFile) {
        this.responseFile = responseFile;
    }

    /**
     * Loads responses as defined within response file into passed stub mappings
     * 
     * @param stubMappings
     */
    @Override
    public void loadMappingsInto(StubMappings stubMappings) {
        JsonStubMappingCreator mappingCreator = new JsonStubMappingCreator(
            stubMappings);
    }
}
