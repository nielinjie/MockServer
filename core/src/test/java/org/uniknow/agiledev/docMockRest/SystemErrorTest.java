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

import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

/**
 * Verifies functionality `SystemError`.
 */
public class SystemErrorTest {

    /**
     * Verifies error message is properly set
     */
    @Test
    public void testConstructorMessage() {
        final String ERROR_MESSAGE = "TEST ERROR MESSAGE";

        SystemError error = new SystemError(ERROR_MESSAGE);

        assertEquals(ERROR_MESSAGE, error.getMessage());
    }

    /**
     * Verifies cause of system error is properly set
     */
    @Test
    public void testConstructorThrowable() {
        final Throwable cause = mock(Throwable.class);
        replay(cause);

        SystemError error = new SystemError(cause);

        assertEquals(cause, error.getCause());
    }

}
