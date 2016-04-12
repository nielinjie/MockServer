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

import org.junit.Test;

import javax.validation.ValidationException;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

/**
 * Validates functionality of `IoUtil`.
 */
public class IoUtilTest {

    /**
     * Verifies ValidationException is thrown when passed filename is null
     */
    @Test(expected = ValidationException.class)
    public void getContentWithFileNameNull() throws FileNotFoundException {
        IoUtil.contentFromFile(null);
    }

    /**
     * Verifies ValidationException is thrown when passed filename is empty
     * string
     */
    @Test(expected = ValidationException.class)
    public void getContentWithFileNameEmpty() throws FileNotFoundException {
        IoUtil.contentFromFile("");
    }

    /**
     * Verifies ValidationException is thrown when passed filename is blank
     * string
     */
    @Test(expected = ValidationException.class)
    public void getContentWithFileNameBlank() throws FileNotFoundException {
        IoUtil.contentFromFile("     ");
    }

    /**
     * Verifies FileNotFoundException is thrown when passed filename doesn't
     * exist on classpath
     */
    @Test(expected = FileNotFoundException.class)
    public void getContentNonExistingFile() throws FileNotFoundException {
        IoUtil.contentFromFile("NONE-EXISTING-FILE.TXT");
    }

    /**
     * Verifies correct content is returned in case of existing file
     */
    @Test
    public void getContentExistingFile() throws FileNotFoundException {
        assertEquals("Content IoUtil.test file",
            IoUtil.contentFromFile("IoUtil.test"));
    }

}
