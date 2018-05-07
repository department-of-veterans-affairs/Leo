package gov.va.vinci.leo.ae;

/*
 * #%L
 * Leo Service
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Thomas Ginter
 * Date: 8/8/14
 * Time: 14:30
 */
public class LeoRemoteAnnotatorTest {
    @Test
    public void testCreateDescriptor() throws Exception {
        LeoRemoteAnnotator remoteAnnotator = new LeoRemoteAnnotator("tcp://localhost:61616", "myInputQueue")
                .setName("BOB");
        assertNotNull(remoteAnnotator);
        assertNotNull(remoteAnnotator.getDescriptor());
        String name = remoteAnnotator.getName();

        assertTrue(remoteAnnotator.getName().startsWith("BOB"));
    }
}
