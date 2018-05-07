package gov.va.vinci.leo.model;

/*
 * #%L
 * Leo Core
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

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 4/7/16.
 */
public class FeatureNameTypeTest {

    @Test
    public void testGettersAndSetters() throws Exception {
        FeatureNameType fnt = new FeatureNameType("name", "type")
                .setName("myName")
                .setType("myType");
        assertEquals("myName", fnt.getName());
        assertEquals("myType", fnt.getType());
    }
}
