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

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.uniknow.agiledev.dbc4java.Validated;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Validated
public class IoUtil {

    /**
     * Returns content of file on classpath.
     * 
     * @param fileName
     *            Name of file
     * @return content of file
     * @throws FileNotFoundException
     *             when specified file could not be found.
     */
    public static String contentFromFile(String fileName)
        throws FileNotFoundException {
        if ((fileName != null) && !fileName.trim().isEmpty()) {
            InputStream in = IoUtil.class.getClassLoader().getResourceAsStream(
                fileName);

            if (in == null) {
                throw new FileNotFoundException(fileName
                    + " could not be found");
            } else {
                return convertStreamToString(in);
            }
        } else {
            throw new ValidationException("Invalid value for filename '"
                + fileName + "'");
        }
    }

    public static String convertStreamToString(@NotNull InputStream is) {
        if (is != null) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } else {
            throw new ValidationException("InputStream may not be null");
        }
    }
}